package io.choerodon.base.infra.utils.excel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.choerodon.base.api.dto.ErrorUserDTO;
import io.choerodon.base.api.dto.ExcelMemberRoleDTO;
import io.choerodon.base.api.validator.RoleValidator;
import io.choerodon.base.api.validator.UserPasswordValidator;
import io.choerodon.base.app.service.OrganizationUserService;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.base.infra.asserts.RoleAssertHelper;
import io.choerodon.base.infra.dto.MemberRoleDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.UploadHistoryDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.feign.FileFeignClient;
import io.choerodon.base.infra.utils.CollectionUtils;
import io.choerodon.base.infra.utils.MockMultipartFile;
import io.choerodon.base.infra.utils.RandomInfoGenerator;
import io.choerodon.core.excel.ExcelExportHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;


/**
 * @author superlee
 */
@RefreshScope
@Component
public class ExcelImportUserTask {
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportUserTask.class);
    private static final String ADD_USER = "addUser";
    private static final String USER_DEFAULT_PWD = "abcd1234";
    private static final String BUSINESS_TYPE_CODE = "addMember";
    private static final String SITE_ROOT = "role/site/default/administrator";
    private static final String ROOT_BUSINESS_TYPE_CODE = "siteAddRoot";
    private static final String USER_BUSINESS_TYPE_CODE = "siteAddUser";

    private RoleMemberService roleMemberService;
    private OrganizationUserService organizationUserService;
    private FileFeignClient fileFeignClient;
    private UserService userService;
    private UserPasswordValidator userPasswordValidator;
    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private UserMapper userMapper;

    private RoleMapper roleMapper;

    private MemberRoleMapper memberRoleMapper;

    private RoleAssertHelper roleAssertHelper;

    private RandomInfoGenerator randomInfoGenerator;
    private OrganizationMapper organizationMapper;

    public ExcelImportUserTask(RoleMemberService roleMemberService,
                               OrganizationUserService organizationUserService,
                               FileFeignClient fileFeignClient,
                               UserService userService,
                               UserPasswordValidator userPasswordValidator,
                               UserMapper userMapper,
                               RoleMapper roleMapper,
                               MemberRoleMapper memberRoleMapper,
                               RoleAssertHelper roleAssertHelper,
                               RandomInfoGenerator randomInfoGenerator,
                               OrganizationMapper organizationMapper) {
        this.roleMemberService = roleMemberService;
        this.organizationUserService = organizationUserService;
        this.fileFeignClient = fileFeignClient;
        this.userService = userService;
        this.userPasswordValidator = userPasswordValidator;
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.roleAssertHelper = roleAssertHelper;
        this.randomInfoGenerator = randomInfoGenerator;
        this.organizationMapper = organizationMapper;
    }

    @Async("excel-executor")
    public void importUsers(Long userId, List<UserDTO> users, Long organizationId, UploadHistoryDTO uploadHistory, FinishFallback fallback) {
        logger.info("### begin to import users from excel, total size : {}", users.size());
        List<UserDTO> validateUsers = new ArrayList<>();
        List<ErrorUserDTO> errorUsers = new ArrayList<>();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
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
        List<UserDTO> insertUsers = distinctPhoneOnDatabase(distinctEmailUsersOnDatabase, errorUsers);
        long end = System.currentTimeMillis();
        logger.info("process user for {} millisecond", (end - begin));
        List<List<UserDTO>> list = CollectionUtils.subList(insertUsers, 999);
        List<UserDTO> userDTOS = new ArrayList<>();
        int validateErrorUsers = errorUsers.size();
        list.forEach(l -> {
            if (!l.isEmpty()) {
                errorUsers.addAll(organizationUserService.batchCreateUsersOnExcel(l, userId, organizationId));
            }
        });
        int insertErrorUsers = errorUsers.size() - validateErrorUsers;
        Integer successCount = insertUsers.size() - insertErrorUsers;
        Integer failedCount = errorUsers.size();
        uploadHistory.setSuccessfulCount(successCount);
        uploadHistory.setFailedCount(failedCount);
        uploadAndFallback(uploadHistory, fallback, errorUsers);
        if (successCount > 0) {
            sendNotice(successCount, userId, organizationId);
        }
    }

    private void sendNotice(Integer successCount, Long userId, Long organizationId) {
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("addCount", successCount);
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId);
        userService.sendNotice(userId, userIds, ADD_USER, paramsMap, organizationId);
        logger.info("batch import user send station letter.");
    }

    private void uploadAndFallback(UploadHistoryDTO uploadHistoryDTO, FinishFallback fallback, List<ErrorUserDTO> errorUsers) {
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
        //去重
        List<ExcelMemberRoleDTO> distinctList = distinctMemberRole(validateMemberRoles, errorMemberRoles);
        Map<MemberRoleDTO, String> excelMemberRoleDTOS = new HashMap<>();
        //***优化查询次数
        distinctList.parallelStream().forEach(emr -> {
            String loginName = emr.getLoginName().trim();
            String code = emr.getRoleCode().trim();
            //检查loginName是否存在
            UserDTO userDTO = getUser(errorMemberRoles, emr, loginName);
            if (userDTO == null) {
                return;
            }
            Long userId = userDTO.getId();
            //检查role code是否存在
            RoleDTO role = getRole(errorMemberRoles, emr, code);
            if (role == null) {
                return;
            }
            if (!userDTO.getEnabled()) {
                emr.setCause("用户已停用");
                errorMemberRoles.add(emr);
                return;
            }
            if (!uploadHistory.getSourceType().equals(role.getResourceLevel())) {
                emr.setCause("导入角色层级与当前层级不匹配");
                errorMemberRoles.add(emr);
                return;
            }
            if (role.getEnabled() != null && !role.getEnabled()) {
                emr.setCause("导入角色未启用");
                errorMemberRoles.add(emr);
                return;
            }
            boolean isNotOrgRole = ResourceLevel.ORGANIZATION.value().equals(uploadHistory.getSourceType())
                    && role.getOrganizationId() != null && !uploadHistory.getSourceId().equals(role.getOrganizationId());
            if (isNotOrgRole) {
                emr.setCause("导入角色不是当前组织所创建");
                errorMemberRoles.add(emr);
                return;
            }
            Long roleId = role.getId();
            //检查memberRole是否存在
            MemberRoleDTO memberRole = getMemberRole(uploadHistory.getSourceId(), uploadHistory.getSourceType(), errorMemberRoles, emr, userId, roleId);
            if (memberRole == null) {
                return;
            }
            roleMemberService.insertAndSendEvent(userDTO, memberRole, loginName);
            excelMemberRoleDTOS.put(memberRole, loginName);
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
                finishFallback.callback(uploadHistory);
            }
        } else {
            uploadHistory.setUrl(url);
            uploadHistory.setFinished(true);
            finishFallback.callback(uploadHistory);
        }
        //批量导入用户及角色时发送消息通知成员
        sendNoticeMsg(fromUserId, excelMemberRoleDTOS);

    }

    private void sendNoticeMsg(Long fromUserId, Map<MemberRoleDTO, String> excelMemberRoleDTOS) {
        for (Map.Entry<MemberRoleDTO, String> memberRoleDTOStringEntry : excelMemberRoleDTOS.entrySet()) {
            UserDTO userDTO = userService.queryByLoginName(memberRoleDTOStringEntry.getValue());
            Map<String, Object> params = new HashMap<>();
            RoleDTO roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTOStringEntry.getKey().getRoleId());
            if (memberRoleDTOStringEntry.getKey().getSourceId() == 0) {
                if (SITE_ROOT.equals(roleDTO.getCode())) {
                    userService.sendNotice(fromUserId, Arrays.asList(userDTO.getId()), ROOT_BUSINESS_TYPE_CODE, Collections.EMPTY_MAP, 0L);
                } else {
                    params.put("roleName", roleDTO.getName());
                    userService.sendNotice(fromUserId, Arrays.asList(userDTO.getId()), USER_BUSINESS_TYPE_CODE, Collections.EMPTY_MAP, 0L);
                }
            } else {
                params.put("organizationName", organizationMapper.selectByPrimaryKey(memberRoleDTOStringEntry.getKey().getSourceId()).getName());
                params.put("roleName", roleDTO.getName());
                userService.sendNotice(fromUserId, Arrays.asList(userDTO.getId()), BUSINESS_TYPE_CODE, params, memberRoleDTOStringEntry.getKey().getSourceId());
            }
        }
    }

    private MemberRoleDTO getMemberRole(Long sourceId, String sourceType, List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, Long userId, Long roleId) {
        MemberRoleDTO memberRole = new MemberRoleDTO();
        memberRole.setSourceType(sourceType);
        memberRole.setSourceId(sourceId);
        memberRole.setMemberType("user");
        memberRole.setMemberId(userId);
        memberRole.setRoleId(roleId);
        if (memberRoleMapper.selectOne(memberRole) != null) {
            emr.setCause("该用户已经被分配了该角色，sourceId={" + sourceId + "}");
            errorMemberRoles.add(emr);
            return null;
        }
        return memberRole;
    }

    private RoleDTO getRole(List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, String code) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setCode(code);
        RoleDTO role = roleMapper.selectOne(roleDTO);
        if (role == null) {
            emr.setCause("角色编码不存在");
            errorMemberRoles.add(emr);
            return null;
        }
        return role;
    }

    private UserDTO getUser(List<ExcelMemberRoleDTO> errorMemberRoles, ExcelMemberRoleDTO emr, String loginName) {
        UserDTO user = new UserDTO();
        if (loginName.matches(UserDTO.EMAIL_REG)) {
            user.setEmail(loginName);
        } else {
            user.setLoginName(loginName);
        }
        UserDTO userDTO = userMapper.selectOne(user);
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
     * @return
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

    private List<UserDTO> distinctEmailOnDatabase(List<UserDTO> validateUsers, List<ErrorUserDTO> errorUsers) {
        List<UserDTO> insertList = new ArrayList<>();
        if (!validateUsers.isEmpty()) {
            Set<String> emailSet = validateUsers.stream().map(UserDTO::getEmail).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subEmailSet = CollectionUtils.subSet(emailSet, 999);
            Set<String> existedEmails = new HashSet<>();
            subEmailSet.forEach(set -> existedEmails.addAll(userMapper.matchEmail(set)));
            for (UserDTO user : validateUsers) {
                if (existedEmails.contains(user.getEmail())) {
                    ErrorUserDTO dto = getErrorUserDTO(user, "邮箱已存在");
                    errorUsers.add(dto);
                } else {
                    insertList.add(user);
                }
            }
        }
        return insertList;
    }

    private List<UserDTO> distinctPhoneOnDatabase(List<UserDTO> validateUsers, List<ErrorUserDTO> errorUsers) {
        List<UserDTO> insertList = new ArrayList<>();
        if (!validateUsers.isEmpty()) {
            Set<String> phoneSet = validateUsers.stream().filter(u -> u.getPhone() != null)
                    .map(UserDTO::getPhone).collect(Collectors.toSet());
            //oracle In-list上限为1000，这里List size要小于1000
            List<Set<String>> subPhoneSet = CollectionUtils.subSet(phoneSet, 999);
            Set<String> existedPhones = new HashSet<>();
            subPhoneSet.forEach(set -> existedPhones.addAll(userMapper.matchPhone(set)));
            for (UserDTO user : validateUsers) {
                if (existedPhones.contains(user.getPhone())) {
                    ErrorUserDTO dto = getErrorUserDTO(user, "手机号已存在");
                    errorUsers.add(dto);
                } else {
                    insertList.add(user);
                }
            }
        }
        return insertList;
    }

    private List<UserDTO> distinctEmailOnExcel(List<UserDTO> validateUsers, List<ErrorUserDTO> errorUsers) {
        List<UserDTO> returnList = new ArrayList<>();
        Map<String, List<UserDTO>> emailMap = validateUsers.stream().collect(Collectors.groupingBy(UserDTO::getEmail));
        for (Map.Entry<String, List<UserDTO>> entry : emailMap.entrySet()) {
            List<UserDTO> list = entry.getValue();
            returnList.add(list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    ErrorUserDTO dto = getErrorUserDTO(list.get(i), "Excel中存在重复的邮箱");
                    errorUsers.add(dto);
                }
            }
        }
        return returnList.stream().sorted(Comparator.comparing(UserDTO::getRealName)).collect(Collectors.toList());
    }

    private List<UserDTO> distinctPhoneOnExcel(List<UserDTO> validateUsers, List<ErrorUserDTO> errorUsers) {
        // 手机号为空的用户 不做校验
        List<UserDTO> returnList = validateUsers.stream().filter(u -> u.getPhone() == null).collect(Collectors.toList());
        Map<String, List<UserDTO>> phoneMap = validateUsers.stream().filter(u -> u.getPhone() != null).collect(Collectors.groupingBy(UserDTO::getPhone));
        for (Map.Entry<String, List<UserDTO>> entry : phoneMap.entrySet()) {
            List<UserDTO> list = entry.getValue();
            returnList.add(list.get(0));
            if (list.size() > 1) {
                for (int i = 1; i < list.size(); i++) {
                    ErrorUserDTO dto = getErrorUserDTO(list.get(i), "Excel中存在重复的手机号");
                    errorUsers.add(dto);
                }
            }
        }
        return returnList;
    }

    private String exportAndUpload(List<ErrorUserDTO> errorUsers) {
        Map<String, String> propertyMap = new LinkedHashMap<>();
        propertyMap.put("realName", "用户名*");
        propertyMap.put("email", "邮箱*");
        propertyMap.put("roleCodes", "角色编码*");
        propertyMap.put("password", "密码");
        propertyMap.put("phone", "手机号");
        propertyMap.put("cause", "原因");
        HSSFWorkbook hssfWorkbook;
        try {
            hssfWorkbook = ExcelExportHelper.exportExcel2003(propertyMap, errorUsers, "error users", ErrorUserDTO.class);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            logger.error("something wrong was happened when exporting the excel, exception : {}", e.getMessage());
            throw new CommonException("error.excel.export", e);
        }
        return upload(hssfWorkbook, "errorUser.xls", "error-user");
    }

    private String upload(HSSFWorkbook hssfWorkbook, String originalFilename, String bucketName) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String url;
        try {
            hssfWorkbook.write(bos);
            MockMultipartFile multipartFile =
                    new MockMultipartFile("file", originalFilename, "application/vnd.ms-excel", bos.toByteArray());
            url = fileFeignClient.uploadFile(bucketName, multipartFile.getOriginalFilename(), multipartFile).getBody();
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

    private void processUsers(UserDTO user, List<ErrorUserDTO> errorUsers, List<UserDTO> validateUsers, Long orgId) {
        //只有校验通过的用户才进行其他字段设置
        if (validateUsers(user, errorUsers, validateUsers, orgId)) {
            // 保存导入用户原始密码
            user.setOriginalPassword(user.getPassword());
            // 如果excel中用户密码为空，设置默认密码
            if (StringUtils.isEmpty(user.getPassword())) {
                user.setPassword(USER_DEFAULT_PWD);
            }
            //加密
            user.setPassword(ENCODER.encode(user.getPassword()));
            // 自动生成登录名
            user.setLoginName(randomInfoGenerator.randomLoginName());
            user.setLastPasswordUpdatedAt(new Date(System.currentTimeMillis()));
            user.setEnabled(true);
            user.setLocked(false);
            user.setLdap(false);
            user.setAdmin(false);
        }
    }

    private boolean validateUsers(UserDTO user, List<ErrorUserDTO> errorUsers, List<UserDTO> insertUsers, Long orgId) {
        String realName = user.getRealName();
        String email = user.getEmail();
        String roleCodes = user.getRoleCodes();
        String phone = user.getPhone();
        String password = user.getPassword();
        trimUserField(user);
        boolean ok = false;
        if (StringUtils.isEmpty(realName) || StringUtils.isEmpty(email) || StringUtils.isEmpty(roleCodes)) {
            errorUsers.add(getErrorUserDTO(user, "用户名为空、邮箱为空或角色编码为空"));
        } else if (realName.length() > 32) {
            errorUsers.add(getErrorUserDTO(user, "用户名超过32位"));
        } else if (!Pattern.matches(UserDTO.EMAIL_REG, email)) {
            errorUsers.add(getErrorUserDTO(user, "非法的邮箱格式"));
        } else if (validateRoles(user, roleCodes, orgId)) {
            errorUsers.add(getErrorUserDTO(user, "角色不存在、未启用或层级不合法"));
        } else if (!StringUtils.isEmpty(phone) && !Pattern.matches(UserDTO.PHONE_REG, phone)) {
            errorUsers.add(getErrorUserDTO(user, "手机号格式不正确"));
        } else if (password != null && !userPasswordValidator.validate(password, user.getOrganizationId(), false)) {
            ErrorUserDTO errorUser = new ErrorUserDTO();
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
        for (int i = 0; i < roleCodeList.length; i++) {
            try {
                RoleDTO role = roleAssertHelper.roleNotExisted(roleCodeList[i]);
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

    private ErrorUserDTO getErrorUserDTO(final UserDTO user, final String cause) {
        ErrorUserDTO errorUser = new ErrorUserDTO();
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
