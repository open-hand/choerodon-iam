package io.choerodon.iam.infra.utils.excel;

import static io.choerodon.iam.infra.constant.TenantConstants.BACKETNAME;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.core.message.MessageAccessor;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.validator.UserPasswordValidator;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.iam.api.vo.ExcelMemberRoleDTO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.RoleAssertHelper;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.enums.SendSettingBaseEnum;
import io.choerodon.iam.infra.mapper.ProjectPermissionMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.iam.infra.mapper.UploadHistoryMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.C7nCollectionUtils;
import io.choerodon.iam.infra.utils.CustomContextUtil;
import io.choerodon.iam.infra.utils.MockMultipartFile;
import io.choerodon.iam.infra.utils.RandomInfoGenerator;
import io.choerodon.iam.infra.valitador.RoleValidator;


/**
 * @author superlee
 */
@RefreshScope
@Component
public class ExcelImportUserTask {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportUserTask.class);
    private static final String ADD_USER = "ADDUSER";
    private static final String USER_DEFAULT_PWD = "abcd1234";

    private RoleMemberService roleMemberService;
    private OrganizationUserService organizationUserService;
    private FileClient fileClient;
    private UserC7nService userService;
    private UserPasswordValidator userPasswordValidator;
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private UserC7nMapper userC7nMapper;

    private RoleC7nMapper roleC7nMapper;

    private MemberRoleRepository memberRoleRepository;

    private RoleAssertHelper roleAssertHelper;

    private RoleRepository roleRepository;

    private RandomInfoGenerator randomInfoGenerator;

    private UserMapper userMapper;

    private ProjectPermissionService projectPermissionService;

    private ProjectPermissionMapper projectPermissionMapper;

    private ProjectAssertHelper projectAssertHelper;
    private MessageClient messageClient;
    @Autowired(required = false)
    private BusinessService businessService;
    @Autowired
    private OrganizationResourceLimitService organizationResourceLimitService;


    public ExcelImportUserTask(RoleMemberService roleMemberService,
                               OrganizationUserService organizationUserService,
                               FileClient fileClient,
                               ProjectPermissionService projectPermissionService,
                               ProjectPermissionMapper projectPermissionMapper,
                               ProjectAssertHelper projectAssertHelper,
                               UserC7nService userService,
                               UserPasswordValidator userPasswordValidator,
                               UserC7nMapper userC7nMapper,
                               RoleRepository roleRepository,
                               RoleC7nMapper roleC7nMapper,
                               MemberRoleRepository memberRoleRepository,
                               MessageClient messageClient,
                               RoleAssertHelper roleAssertHelper,
                               RandomInfoGenerator randomInfoGenerator,
                               UserMapper userMapper) {
        this.roleMemberService = roleMemberService;
        this.organizationUserService = organizationUserService;
        this.fileClient = fileClient;
        this.userService = userService;
        this.userPasswordValidator = userPasswordValidator;
        this.userC7nMapper = userC7nMapper;
        this.memberRoleRepository = memberRoleRepository;
        this.roleAssertHelper = roleAssertHelper;
        this.randomInfoGenerator = randomInfoGenerator;
        this.roleC7nMapper = roleC7nMapper;
        this.projectPermissionService = projectPermissionService;
        this.roleRepository = roleRepository;
        this.projectPermissionMapper = projectPermissionMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.userMapper = userMapper;
        this.messageClient = messageClient;
    }

    @Async("excel-executor")
    public void importUsers(Long userId, List<UserDTO> users, Long organizationId, UploadHistoryDTO uploadHistory, FinishFallback fallback) {
        logger.info("### begin to import users from excel, total size : {}", users.size());
        // 设置用户上下文
        User operator = userMapper.selectByPrimaryKey(userId);
        CustomContextUtil.setUserContext(operator.getLoginName(), userId, operator.getOrganizationId());

        List<UserDTO> validateUsers = new ArrayList<>();
        List<ErrorUserVO> errorUsers = new ArrayList<>();
        long begin = System.currentTimeMillis();
        users.forEach(u -> {
                    u.setOrganizationId(organizationId);
                    processUsers(u, errorUsers, validateUsers, organizationId);
                }
        );
        // excel里面根据email去重
        List<UserDTO> distinctEmailUsersOnExcel = distinctEmailOnExcel(validateUsers, errorUsers);
        // excel里面根据手机号去重
        List<UserDTO> distinctPhoneUsersOnExcel = distinctPhoneOnExcel(distinctEmailUsersOnExcel, errorUsers);
        // 数据库里面根据email去重
        List<UserDTO> distinctEmailUsersOnDatabase = distinctEmailOnDatabase(distinctPhoneUsersOnExcel, errorUsers);
        // 数据库里面根据phone去重
        List<UserDTO> usersToBeInserted = distinctPhoneOnDatabase(distinctEmailUsersOnDatabase, errorUsers);
        long end = System.currentTimeMillis();
        logger.info("process user for {} millisecond", (end - begin));
        List<List<UserDTO>> actualUsersToBeInserted = C7nCollectionUtils.fragmentList(usersToBeInserted, 999);
        int validateErrorUsers = errorUsers.size();
        actualUsersToBeInserted.forEach(userFragments -> {
            if (!userFragments.isEmpty()) {
                errorUsers.addAll(organizationUserService.batchCreateUsersOnExcel(userFragments, userId, organizationId));
            }
        });
        logger.info("Finished to try to insert users from excel...");
        int insertErrorUsers = errorUsers.size() - validateErrorUsers;
        Integer successCount = usersToBeInserted.size() - insertErrorUsers;
        Integer failedCount = errorUsers.size();
        uploadHistory.setSuccessfulCount(successCount);
        uploadHistory.setFailedCount(failedCount);
        uploadAndFallback(uploadHistory, fallback, errorUsers);
        if (successCount > 0) {
            sendNotice(successCount, userId, organizationId);
        }
    }

    @Async
    public void sendNotice(Integer successCount, Long userId, Long organizationId) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("addCount", String.valueOf(successCount));
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        userService.sendNotice(userIds, ADD_USER, paramsMap, organizationId, ResourceLevel.ORGANIZATION);
        logger.info("batch import user send station letter.");
    }

    private void uploadAndFallback(UploadHistoryDTO uploadHistoryDTO, FinishFallback fallback, List<ErrorUserVO> errorUsers) {
        logger.info("Start upload and fallback...");
        String url = "";
        if (uploadHistoryDTO.getFailedCount() > 0) {
            //失败的用户导出到excel
            try {
                long begin = System.currentTimeMillis();
                url = exportAndUpload(errorUsers);
                long end = System.currentTimeMillis();
                logger.info("export and upload file for {} millisecond", (end - begin));
                uploadHistoryDTO.setFinished(true);
            } catch (CommonException e) {
                uploadHistoryDTO.setFinished(false);
                throw e;
            } finally {
                uploadHistoryDTO.setUrl(url);
                fallback.callback(uploadHistoryDTO);
            }
        } else {
            //插入uploadHistory
            uploadHistoryDTO.setUrl(url);
            uploadHistoryDTO.setFinished(true);
            fallback.callback(uploadHistoryDTO);
        }
    }

    @Async("excel-executor")
    public void importMemberRole(Long fromUserId, List<ExcelMemberRoleDTO> memberRoles, UploadHistoryDTO uploadHistory, FinishFallback finishFallback) {
        Integer total = memberRoles.size();
        logger.info("### begin to import member-role from excel, total size : {}", total);
        List<ExcelMemberRoleDTO> errorMemberRoles = new CopyOnWriteArrayList<>();
        List<ExcelMemberRoleDTO> validateMemberRoles = new CopyOnWriteArrayList<>();
        // 1. 校验
        memberRoles.parallelStream().forEach(mr -> {
            if (StringUtils.isEmpty(mr.getLoginName())) {
                mr.setCause("登录名为空");
                errorMemberRoles.add(mr);
            } else if (StringUtils.isEmpty(mr.getRoleCode())) {
                mr.setCause("角色编码为空");
                errorMemberRoles.add(mr);
            } else {
                validateMemberRoles.add(mr);
            }
        });
        //2. 去重
        //***优化查询次数
        // 校验参数，以及装配用户要分配的角色
        List<ExcelMemberRoleDTO> distinctList = distinctMemberRole(validateMemberRoles, errorMemberRoles);
        distinctList.forEach(emr -> {
            String loginName = emr.getLoginName().trim();
            String code = emr.getRoleCode().trim();
            //检查loginName是否存在
            User userDTO = getUser(errorMemberRoles, emr, loginName);
            if (userDTO == null) {
                return;
            }
            Long userId = userDTO.getId();
            //检查role code是否存在
            Long sourceId = uploadHistory.getSourceType().equals(ResourceLevel.PROJECT.value()) ? projectAssertHelper.projectNotExisted(uploadHistory.getSourceId()).getOrganizationId() : uploadHistory.getSourceId();
            Role role = getRole(errorMemberRoles, emr, code, sourceId);
            if (role == null) {
                return;
            }
            if (!userDTO.getEnabled()) {
                emr.setCause("用户已停用");
                errorMemberRoles.add(emr);
                return;
            }
            if (role.getEnabled() != null && !role.getEnabled()) {
                emr.setCause("导入角色未启用");
                errorMemberRoles.add(emr);
                return;
            }
            boolean isNotOrgRole = ResourceLevel.ORGANIZATION.value().equals(uploadHistory.getSourceType())
                    && role.getTenantId() != null && !uploadHistory.getSourceId().equals(role.getTenantId());
            if (isNotOrgRole) {
                emr.setCause("导入角色不是当前组织所创建");
                errorMemberRoles.add(emr);
                return;
            }
            List<String> roleLabels = roleC7nMapper.listRoleLabels(role.getId()).stream().map(Label::getName).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(roleLabels)) {
                emr.setCause("导入角色不存在角色标签");
                errorMemberRoles.add(emr);
                return;
            }
            if (ResourceLevel.PROJECT.value().equals(uploadHistory.getSourceType()) && !roleLabels.contains(RoleLabelEnum.PROJECT_ROLE.value())) {
                emr.setCause("导入角色不属于项目层角色");
                errorMemberRoles.add(emr);
                return;
            }
            if (ResourceLevel.ORGANIZATION.value().equals(uploadHistory.getSourceType()) && !roleLabels.contains(RoleLabelEnum.TENANT_ROLE.value())) {
                emr.setCause("导入角色不属于组织层角色");
                errorMemberRoles.add(emr);
                return;
            }
            Long roleId = role.getId();
            //检查memberRole是否存在 导入组织成员重复，不在处理
            MemberRole memberRole;
            if (uploadHistory.getSourceType().equals(ResourceLevel.PROJECT.value())) {
                memberRole = getMemberRoleForProject(uploadHistory.getSourceId(), errorMemberRoles, emr, userId, roleId);
                if (memberRole == null) {
                    return;
                }
                Set<Long> roleIds = new HashSet<>();
                roleIds.add(roleId);
                try {
                    projectPermissionService.addProjectRolesForUser(uploadHistory.getSourceId(), userId, roleIds, fromUserId);
                    if (businessService != null) {
                        businessService.setUserProjectDate(uploadHistory.getSourceId(), userId, emr.getScheduleEntryTime(), emr.getScheduleExitTime());
                    }
                } catch (Exception e) {
                    ExcelMemberRoleDTO excelMemberRoleDTO = new ExcelMemberRoleDTO();
                    excelMemberRoleDTO.setLoginName(userDTO.getLoginName());
                    excelMemberRoleDTO.setRoleCode(role.getCode());
                    excelMemberRoleDTO.setCause(getErrorMessage(e));
                    errorMemberRoles.add(excelMemberRoleDTO);
                }
            } else {
                memberRole = getMemberRole(uploadHistory.getSourceId(), uploadHistory.getSourceType(), errorMemberRoles, emr, userId, roleId);
                if (memberRole == null) {
                    return;
                }
                Set<Long> roleIds = new HashSet<>();
                roleIds.add(roleId);
                try {
                    roleMemberService.addTenantRoleForUser(uploadHistory.getSourceId(), userId, roleIds, fromUserId);
                } catch (Exception e) {
                    ExcelMemberRoleDTO excelMemberRoleDTO = new ExcelMemberRoleDTO();
                    excelMemberRoleDTO.setLoginName(userDTO.getLoginName());
                    excelMemberRoleDTO.setRoleCode(role.getCode());
                    excelMemberRoleDTO.setCause(getErrorMessage(e));
                    errorMemberRoles.add(excelMemberRoleDTO);
                }
            }
        });
        Integer failedCount = errorMemberRoles.size();
        Integer successfulCount = total - failedCount;
        uploadHistory.setFailedCount(failedCount);
        uploadHistory.setSuccessfulCount(successfulCount);
        String url = "";
        if (failedCount > 0) {
            try {
                url = exportAndUploadMemberRole(errorMemberRoles);
                uploadHistory.setFinished(true);
            } catch (CommonException e) {
                uploadHistory.setFinished(false);
                throw e;
            } finally {
                uploadHistory.setUrl(url);
            }
        } else {
            uploadHistory.setUrl(url);
            uploadHistory.setFinished(true);
        }
        finishFallback.callback(uploadHistory);
    }

    private String getErrorMessage(Exception e) {
        if (!StringUtils.isEmpty(e.getMessage()) && MessageAccessor.getMessage(e.getMessage()) != null) {
            return MessageAccessor.getMessage(e.getMessage()).desc();
        } else {
            return "未知错误，请重试！";
        }
    }

    /**
     * 项目下导入用户 发送消息
     *
     * @param projectId
     * @param count
     */
    private void mgsProjectAddUser(Long projectId, Integer count) {
        MessageSender messageSender = new MessageSender();
        messageSender.setMessageCode(SendSettingBaseEnum.PROJECT_ADD_USER.value());
        messageSender.setTenantId(0L);

        Map<String, Object> map = new HashMap<>();
        map.put("createdAt", new Date());
        map.put("eventName", SendSettingBaseEnum.map.get(SendSettingBaseEnum.PROJECT_ADD_USER.value()));
        map.put("objectKind", SendSettingBaseEnum.PROJECT_ADD_USER.value());
        map.put("projectId", projectId);
        map.put("addCount", count);
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
        map.put("organizationId", projectDTO.getOrganizationId());
        messageSender.setObjectArgs(map);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName(), projectId);
        objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), projectDTO.getOrganizationId());
        messageSender.setAdditionalInformation(objectMap);
        messageClient.async().sendMessage(messageSender);
    }

    private MemberRole getMemberRole(Long sourceId, String sourceType, List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, Long userId, Long roleId) {
        MemberRole memberRole = new MemberRole();
        memberRole.setSourceType(sourceType);
        memberRole.setSourceId(sourceId);
        memberRole.setMemberType("user");
        memberRole.setMemberId(userId);
        memberRole.setRoleId(roleId);
        if (memberRoleRepository.selectOne(memberRole) != null) {
            emr.setCause("该用户已经被分配了该角色，sourceId={" + sourceId + "}");
            errorMemberRoles.add(emr);
            return null;
        }
        return memberRole;
    }

    private MemberRole getMemberRoleForProject(Long sourceId, List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, Long userId, Long roleId) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
        MemberRole memberRole = new MemberRole();
        memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
        memberRole.setSourceId(projectDTO.getOrganizationId());
        memberRole.setMemberType("user");
        memberRole.setMemberId(userId);
        memberRole.setRoleId(roleId);
        if (!CollectionUtils.isEmpty(projectPermissionMapper.selectByRoleIdAndUserId(roleId, sourceId, userId))) {
            emr.setCause("该用户已经被分配了该角色，sourceId={" + sourceId + "}");
            errorMemberRoles.add(emr);
            return null;
        }
        return memberRole;
    }

    private Role getRole(List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, String code, Long tenantId) {
        Role dto = new Role();
        dto.setCode(code);
        dto.setTenantId(tenantId);
        List<Role> roleList = roleRepository.select(dto);
        if (CollectionUtils.isEmpty(roleList)) {
            emr.setCause("角色编码不存在");
            errorMemberRoles.add(emr);
            return null;
        }
        if (roleList.size() == 1) {
            return roleList.get(0);
        } else {
            emr.setCause("角色编码组织下不唯一");
            errorMemberRoles.add(emr);
            return null;
        }
    }


    private User getUser(List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, String loginName) {
        UserDTO user = new UserDTO();
        if (loginName.matches(UserDTO.EMAIL_REG)) {
            user.setEmail(loginName);
        } else {
            user.setLoginName(loginName);
        }
        User userDTO = userMapper.selectOne(user);
        if (userDTO == null) {
            emr.setCause("登录名不存在");
            errorMemberRoles.add(emr);
            return null;
        }
        return userDTO;
    }

    /**
     * 导入member-role去重
     * 去重策略，根据loginName和code分组，value集合大于1，则有重复的，将重复的集合放入error集合中
     *
     * @param validateMemberRoles 源集合
     * @param errorMemberRoles    重复的数据集合
     * @return ExcelMemberRoleDTO
     */
    private List<ExcelMemberRoleDTO> distinctMemberRole(List<ExcelMemberRoleDTO> validateMemberRoles, List<ExcelMemberRoleDTO> errorMemberRoles) {
        List<ExcelMemberRoleDTO> distinctList = new ArrayList<>();
        //excel内去重
        Map<Map<String, String>, List<ExcelMemberRoleDTO>> distinctMap =
                validateMemberRoles.stream().collect(Collectors.groupingBy(m -> {
                    Map<String, String> map = new HashMap<>();
                    map.put(m.getLoginName(), m.getRoleCode());
                    return map;
                }));
        for (Map.Entry<Map<String, String>, List<ExcelMemberRoleDTO>> entry : distinctMap.entrySet()) {
            List<ExcelMemberRoleDTO> list = entry.getValue();
            distinctList.add(list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    ExcelMemberRoleDTO dto = list.get(i);
                    dto.setCause("excel中存在重复的数据");
                    errorMemberRoles.add(dto);
                }
            }
        }
        return distinctList;
    }

    private List<UserDTO> distinctEmailOnDatabase(List<UserDTO> validateUsers, List<ErrorUserVO> errorUsers) {
        List<UserDTO> insertList = new ArrayList<>();
        if (!validateUsers.isEmpty()) {
            Set<String> emailSet = validateUsers.stream().map(UserDTO::getEmail).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subEmailSet = C7nCollectionUtils.fragmentSet(emailSet, 999);
            Set<String> existedEmails = new HashSet<>();
            subEmailSet.forEach(set -> existedEmails.addAll(userC7nMapper.matchEmail(set)));
            for (UserDTO user : validateUsers) {
                if (existedEmails.contains(user.getEmail())) {
                    ErrorUserVO dto = getErrorUserDTO(user, "邮箱已存在");
                    errorUsers.add(dto);
                } else {
                    insertList.add(user);
                }
            }
        }
        return insertList;
    }

    private List<UserDTO> distinctPhoneOnDatabase(List<UserDTO> validateUsers, List<ErrorUserVO> errorUsers) {
        List<UserDTO> insertList = new ArrayList<>();
        if (!validateUsers.isEmpty()) {
            Set<String> phoneSet = validateUsers.stream().filter(u -> u.getPhone() != null)
                    .map(UserDTO::getPhone).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subPhoneSet = C7nCollectionUtils.fragmentSet(phoneSet, 999);
            Set<String> existedPhones = new HashSet<>();
            subPhoneSet.forEach(set -> existedPhones.addAll(userC7nMapper.matchPhone(set)));
            for (UserDTO user : validateUsers) {
                if (existedPhones.contains(user.getPhone())) {
                    ErrorUserVO dto = getErrorUserDTO(user, "手机号已存在");
                    errorUsers.add(dto);
                } else {
                    insertList.add(user);
                }
            }
        }
        return insertList;
    }

    private List<UserDTO> distinctEmailOnExcel(List<UserDTO> validateUsers, List<ErrorUserVO> errorUsers) {
        List<UserDTO> returnList = new ArrayList<>();
        Map<String, List<UserDTO>> emailMap = validateUsers.stream().collect(Collectors.groupingBy(UserDTO::getEmail));
        for (Map.Entry<String, List<UserDTO>> entry : emailMap.entrySet()) {
            List<UserDTO> list = entry.getValue();
            returnList.add(list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    ErrorUserVO dto = getErrorUserDTO(list.get(i), "Excel中存在重复的邮箱");
                    errorUsers.add(dto);
                }
            }
        }
        return returnList.stream().sorted(Comparator.comparing(UserDTO::getRealName)).collect(Collectors.toList());
    }

    private List<UserDTO> distinctPhoneOnExcel(List<UserDTO> validateUsers, List<ErrorUserVO> errorUsers) {
        // 手机号为空的用户 不做校验
        List<UserDTO> returnList = validateUsers.stream().filter(u -> u.getPhone() == null).collect(Collectors.toList());
        Map<String, List<UserDTO>> phoneMap = validateUsers.stream().filter(u -> u.getPhone() != null).collect(Collectors.groupingBy(UserDTO::getPhone));
        for (Map.Entry<String, List<UserDTO>> entry : phoneMap.entrySet()) {
            List<UserDTO> list = entry.getValue();
            returnList.add(list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    ErrorUserVO dto = getErrorUserDTO(list.get(i), "Excel中存在重复的手机号");
                    errorUsers.add(dto);
                }
            }
        }
        return returnList;
    }

    private String exportAndUpload(List<ErrorUserVO> errorUsers) {
        Map<String, String> propertyMap = new LinkedHashMap<>();
        propertyMap.put("realName", "用户名*");
        propertyMap.put("email", "邮箱*");
        propertyMap.put("roleCodes", "角色编码*");
        propertyMap.put("password", "密码");
        propertyMap.put("phone", "手机号");
        propertyMap.put("cause", "原因");
        HSSFWorkbook hssfWorkbook;
        try {
            hssfWorkbook = ExcelExportHelper.exportExcel2003(propertyMap, errorUsers, "error users", ErrorUserVO.class);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("something wrong was happened when exporting the excel, exception : {}", e.getMessage());
            throw new CommonException("error.excel.export", e);
        }
        return upload(hssfWorkbook, "errorUser.xls", "error-user");
    }

    private String upload(HSSFWorkbook hssfWorkbook, String originalFilename, String pathName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String url;
        try {
            hssfWorkbook.write(bos);
            MockMultipartFile multipartFile =
                    new MockMultipartFile("file", originalFilename, "application/vnd.ms-excel", bos.toByteArray());
            url = fileClient.uploadFile(0L, BACKETNAME, pathName, multipartFile);
        } catch (IOException e) {
            logger.error("HSSFWorkbook to ByteArrayOutputStream failed, exception: {}", e.getMessage());
            throw new CommonException("error.byteArrayOutputStream", e);
        } catch (Exception e) {
            logger.error("feign invoke exception : {}", e.getMessage());
            throw new CommonException("error.feign.invoke", e);
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                logger.info("byteArrayOutputStream close failed, exception: {}", e.getMessage());
            }
        }
        return url;
    }

    private String exportAndUploadMemberRole(List<ExcelMemberRoleDTO> errorMemberRoles) {
        Map<String, String> propertyMap = new LinkedHashMap<>();
        propertyMap.put("loginName", "登录名*");
        propertyMap.put("roleCode", "角色编码*");
        propertyMap.put("cause", "原因");
        HSSFWorkbook hssfWorkbook;
        try {
            hssfWorkbook = ExcelExportHelper.exportExcel2003(propertyMap, errorMemberRoles, "error", ExcelMemberRoleDTO.class);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("something wrong was happened when exporting the excel, exception : {}", e.getMessage());
            throw new CommonException("error.excel.export");
        }
        return upload(hssfWorkbook, "errorMemberRole.xls", "error-member-role");
    }

    private void processUsers(UserDTO user, List<ErrorUserVO> errorUsers, List<UserDTO> validateUsers, Long orgId) {
        //只有校验通过的用户才进行其他字段设置
        if (validateUsers(user, errorUsers, validateUsers, orgId)) {
            // 保存导入用户原始密码
            user.setOriginalPassword(user.getPassword());
            // 如果excel中用户密码为空，设置默认密码
            if (StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(USER_DEFAULT_PWD);
            }
            // 自动生成登录名
            user.setLoginName(randomInfoGenerator.randomLoginName());
            user.setLastPasswordUpdatedAt(new Date(System.currentTimeMillis()));
            user.setEnabled(true);
            user.setLocked(false);
            user.setLdap(false);
            user.setAdmin(false);
        }
    }

    private boolean validateUsers(UserDTO user, List<ErrorUserVO> errorUsers, List<UserDTO> insertUsers, Long orgId) {
        String realName = user.getRealName();
        String email = user.getEmail();
        String roleCodes = user.getRoleCodes();
        String phone = user.getPhone();
        String password = user.getPassword();
        trimUserField(user);
        boolean ok = false;
        if (StringUtils.isEmpty(realName) || StringUtils.isEmpty(email) || StringUtils.isEmpty(roleCodes)) {
            errorUsers.add(getErrorUserDTO(user, "用户名为空、邮箱为空或角色标签为空"));
        } else if (realName.length() > 32) {
            errorUsers.add(getErrorUserDTO(user, "用户名超过32位"));
        } else if (!Pattern.matches(UserDTO.EMAIL_REG, email)) {
            errorUsers.add(getErrorUserDTO(user, "非法的邮箱格式"));
        } else if (validateRoles(user, roleCodes, orgId)) {
            errorUsers.add(getErrorUserDTO(user, "角色不存在、未启用或层级不合法"));
        } else if (!StringUtils.isEmpty(phone) && !Pattern.matches(UserDTO.PHONE_REG, phone)) {
            errorUsers.add(getErrorUserDTO(user, "手机号格式不正确"));
        } else if (password != null && !userPasswordValidator.validate(password, user.getOrganizationId(), false)) {
            ErrorUserVO errorUser = new ErrorUserVO();
            BeanUtils.copyProperties(user, errorUser);
            String cause = "用户密码长度不符合系统设置中的范围";
            // 为了获取报错的密码长度，再进行一次校验，从Exception中拿到报错信息，乐观认为犯错是少数，所以这样处理
            try {
                userPasswordValidator.validate(password, user.getOrganizationId(), true);
            } catch (CommonException c) {
                if (c.getParameters().length >= 2) {
                    cause += "，长度应为" + c.getParameters()[0] + "-" + c.getParameters()[1];
                }
            }
            errorUser.setCause(cause);
            errorUsers.add(errorUser);
        } else {
            ok = true;
            insertUsers.add(user);
        }
        return ok;
    }

    private boolean validateRoles(UserDTO user, String roleCodes, Long orgId) {
        String[] roleCodeList = roleCodes.split(",");
        boolean rolesError = false;
        user.setRoles(new ArrayList<>());
        for (String roleCode : roleCodeList) {
            try {
                Role role = roleAssertHelper.roleExistedWithCode(orgId, roleCode);
                RoleValidator.validateRole(ResourceLevel.ORGANIZATION.value(), orgId, role, false);
                user.getRoles().add(role);
            } catch (CommonException e) {
                user.setRoles(null);
                rolesError = true;
                break;
            }
        }
        return rolesError;
    }

    private void trimUserField(UserDTO user) {
        String realName = user.getRealName();
        String email = user.getEmail();
        String roleCodes = user.getRoleCodes();
        String phone = user.getPhone();
        String password = user.getPassword();
        if (!StringUtils.isEmpty(realName)) {
            user.setRealName(realName.trim());
        }
        if (!StringUtils.isEmpty(email)) {
            user.setEmail(email.trim());
        }
        if (!StringUtils.isEmpty(roleCodes)) {
            user.setRoleCodes(roleCodes.trim());
        }
        if (!StringUtils.isEmpty(phone)) {
            user.setPhone(phone.trim());
        }
        if (!StringUtils.isEmpty(password)) {
            user.setPassword(password);
        }
    }

    private ErrorUserVO getErrorUserDTO(final UserDTO user, final String cause) {
        ErrorUserVO errorUser = new ErrorUserVO();
        BeanUtils.copyProperties(user, errorUser);
        errorUser.setCause(cause);
        // 重置为导入用户原始密码
        errorUser.setPassword(user.getOriginalPassword());
        return errorUser;
    }


    public interface FinishFallback {
        /**
         * 同步完成后回调
         *
         * @param uploadHistoryDTO 历史纪录
         */
        void callback(UploadHistoryDTO uploadHistoryDTO);
    }


    @Component
    public class FinishFallbackImpl implements FinishFallback {

        private UploadHistoryMapper uploadHistoryMapper;

        public FinishFallbackImpl(UploadHistoryMapper uploadHistoryMapper) {
            this.uploadHistoryMapper = uploadHistoryMapper;
        }

        @Override
        public void callback(UploadHistoryDTO uploadHistoryDTO) {
            UploadHistoryDTO history = uploadHistoryMapper.selectByPrimaryKey(uploadHistoryDTO.getId());
            history.setEndTime(new Date((System.currentTimeMillis())));
            history.setSuccessfulCount(uploadHistoryDTO.getSuccessfulCount());
            history.setFailedCount(uploadHistoryDTO.getFailedCount());
            history.setUrl(uploadHistoryDTO.getUrl());
            history.setFinished(uploadHistoryDTO.getFinished());
            history.setSourceId(uploadHistoryDTO.getSourceId());
            history.setSourceType(uploadHistoryDTO.getSourceType());
            uploadHistoryMapper.updateByPrimaryKeySelective(history);
        }
    }
}
