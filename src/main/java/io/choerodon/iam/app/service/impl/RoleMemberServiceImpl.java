package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.MemberRole.MEMBER_ROLE_DELETE;
import static io.choerodon.iam.infra.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;
import static io.choerodon.iam.infra.utils.SagaTopic.User.ORG_USER_CREAT;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ExcelMemberRoleDTO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.dto.payload.WebHookUser;
import io.choerodon.iam.infra.enums.ExcelSuffix;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.enums.SendSettingBaseEnum;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.utils.excel.ExcelImportUserTask;
import io.choerodon.iam.infra.utils.excel.ExcelReadConfig;
import io.choerodon.iam.infra.utils.excel.ExcelReadHelper;

/**
 * @author superlee
 * @author wuguokai
 * @author zmf
 */
@Component
public class RoleMemberServiceImpl implements RoleMemberService {

    private final Logger logger = LoggerFactory.getLogger(RoleMemberServiceImpl.class);
    private static final String MEMBER_ROLE_NOT_EXIST_EXCEPTION = "error.memberRole.not.exist";
    private static final String SITE_MEMBERROLE_TEMPLATES_PATH = "/templates/siteMemberRoleTemplates";
    private static final String ORGANIZATION_MEMBERROLE_TEMPLATES_PATH = "/templates/organizationMemberRoleTemplates";
    private static final String PROJECT_MEMBERROLE_TEMPLATES_PATH = "/templates/projectMemberRoleTemplates";
    private static final String DOT_SEPARATOR = ".";
    private static final String SITE_ROOT = "role/site/default/administrator";
    private static final String ROOT_BUSINESS_TYPE_CODE = "SITEADDROOT";
    private static final String USER_BUSINESS_TYPE_CODE = "SITEADDUSER";
    private static final String BUSINESS_TYPE_CODE = "ADDMEMBER";
    private static final String PROJECT_ADD_USER = "PROJECTADDUSER";

    @Value(value = "${services.front.url: http://app.example.com}")
    private String frontUrl;
    private static final String DETAILS_URL = "%s/projects?id=%s&name=%s&organizationId=%s&type=organization";

    private TenantMapper tenantMapper;
    private ProjectMapper projectMapper;

    private MemberRoleMapper memberRoleMapper;

    private MemberRoleC7nMapper memberRoleC7nMapper;

    private RoleMapper roleMapper;

    private UserAssertHelper userAssertHelper;

    private final ObjectMapper mapper = new ObjectMapper();

    private LabelC7nMapper labelC7nMapper;

    private ClientMapper clientMapper;

    private UploadHistoryMapper uploadHistoryMapper;

    private OrganizationUserService organizationUserService;

    private ProjectAssertHelper projectAssertHelper;

    private UserMapper userMapper;

    private ExcelImportUserTask excelImportUserTask;

    private ExcelImportUserTask.FinishFallback finishFallback;
    private TransactionalProducer producer;
    private MemberRoleService memberRoleService;
    private ProjectPermissionMapper projectPermissionMapper;
    private MemberRoleRepository memberRoleRepository;
    private MessageClient messageClient;
    private MessageSendService messageSendService;
    private UserC7nService userC7nService;
    private ProjectC7nService projectC7nService;
    private RoleC7nMapper roleC7nMapper;


    public RoleMemberServiceImpl(TenantMapper tenantMapper,
                                 ProjectMapper projectMapper,
                                 MemberRoleMapper memberRoleMapper,
                                 MemberRoleC7nMapper memberRoleC7nMapper,
                                 RoleC7nMapper roleC7nMapper,
                                 RoleMapper roleMapper,
                                 UserAssertHelper userAssertHelper,
                                 MemberRoleService memberRoleService,
                                 LabelC7nMapper labelC7nMapper,
                                 ClientMapper clientMapper,
                                 UploadHistoryMapper uploadHistoryMapper,
                                 OrganizationUserService organizationUserService,
                                 ProjectPermissionMapper projectPermissionMapper,
                                 UserMapper userMapper,
                                 ProjectAssertHelper projectAssertHelper,
                                 ExcelImportUserTask excelImportUserTask,
                                 ExcelImportUserTask.FinishFallback finishFallback,
                                 TransactionalProducer producer,
                                 MessageClient messageClient,
                                 MemberRoleRepository memberRoleRepository,
                                 @Lazy
                                         MessageSendService messageSendService,
                                 @Lazy
                                         UserC7nService userC7nService,
                                 @Lazy
                                         ProjectC7nService projectC7nService) {
        this.tenantMapper = tenantMapper;
        this.projectMapper = projectMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.memberRoleC7nMapper = memberRoleC7nMapper;
        this.roleC7nMapper = roleC7nMapper;
        this.roleMapper = roleMapper;
        this.userAssertHelper = userAssertHelper;
        this.labelC7nMapper = labelC7nMapper;
        this.clientMapper = clientMapper;
        this.uploadHistoryMapper = uploadHistoryMapper;
        this.organizationUserService = organizationUserService;
        this.userMapper = userMapper;
        this.excelImportUserTask = excelImportUserTask;
        this.finishFallback = finishFallback;
        this.memberRoleService = memberRoleService;
        this.projectAssertHelper = projectAssertHelper;
        this.projectPermissionMapper = projectPermissionMapper;
        this.messageClient = messageClient;
        this.producer = producer;
        this.memberRoleRepository = memberRoleRepository;
        this.messageSendService = messageSendService;
        this.userC7nService = userC7nService;
        this.projectC7nService = projectC7nService;
    }


    @Transactional(rollbackFor = CommonException.class)
    @Override
    public List<MemberRole> createOrUpdateRolesByMemberIdOnOrganizationLevel(Boolean isEdit, Long organizationId, List<Long> memberIds, List<MemberRole> memberRoleDTOList, String memberType) {
        List<MemberRole> memberRoleDTOS = new ArrayList<>();

        memberType = validate(memberRoleDTOList, memberType);

        // member type 为 'client' 时
        if (memberType != null && memberType.equals(MemberType.CLIENT.value())) {
            for (Long memberId : memberIds) {
                memberRoleDTOList.forEach(m ->
                        m.setMemberId(memberId)
                );
                memberRoleDTOS.addAll(
                        insertOrUpdateRolesOfClientByMemberId(isEdit, organizationId, memberId,
                                memberRoleDTOList,
                                ResourceLevel.ORGANIZATION.value()));
            }
            return memberRoleDTOS;
        }

        // member type 为 'user' 时
        for (Long memberId : memberIds) {
            memberRoleDTOList.forEach(m ->
                    m.setMemberId(memberId)
            );
            memberRoleDTOS.addAll(
                    insertOrUpdateRolesOfUserByMemberId(isEdit, organizationId, memberId,
                            memberRoleDTOList,
                            ResourceLevel.ORGANIZATION.value()));
        }
        return memberRoleDTOS;
    }

    private String validate(List<MemberRole> memberRoleDTOList, String memberType) {
        if (memberType == null && memberRoleDTOList != null && !memberRoleDTOList.isEmpty()) {
            memberType = memberRoleDTOList.get(0).getMemberType();
        }
        if (memberRoleDTOList == null) {
            throw new CommonException("error.memberRole.null");
        }
        return memberType;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public List<MemberRole> createOrUpdateRolesByMemberIdOnProjectLevel(Boolean isEdit, Long projectId, List<Long> memberIds, List<MemberRole> memberRoleDTOList, String memberType) {
        List<MemberRole> memberRoleDTOS = new ArrayList<>();

        memberType = validate(memberRoleDTOList, memberType);

        // member type 为 'client' 时
        if (memberType != null && memberType.equals(MemberType.CLIENT.value())) {
            for (Long memberId : memberIds) {
                memberRoleDTOList.forEach(m ->
                        m.setMemberId(memberId)
                );
                memberRoleDTOS.addAll(
                        insertOrUpdateRolesOfClientByMemberId(isEdit, projectId, memberId,
                                memberRoleDTOList,
                                ResourceLevel.PROJECT.value()));
            }
            return memberRoleDTOS;
        }

        // member type 为 'user' 时
        for (Long memberId : memberIds) {
            memberRoleDTOList.forEach(m ->
                    m.setMemberId(memberId)
            );
            memberRoleDTOS.addAll(
                    insertOrUpdateRolesOfUserByMemberId(isEdit, projectId, memberId,
                            memberRoleDTOList,
                            ResourceLevel.PROJECT.value()));
        }
        return memberRoleDTOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOnProjectLevel(Long projectId, Long userId, Boolean syncAll) {
        deleteProjectRole(projectId, userId, null, syncAll);
        mgsDeleteUserRoles(projectId, userId);
    }


    @Override
    public MemberRole insertSelective(MemberRole memberRoleDTO) {
        if (memberRoleDTO.getMemberType() == null) {
            memberRoleDTO.setMemberType("user");
        }
        Role roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
        if (roleDTO == null) {
            throw new CommonException("error.member_role.insert.role.not.exist");
        }
        if (ResourceLevel.PROJECT.value().equals(memberRoleDTO.getSourceType())
                && projectMapper.selectByPrimaryKey(memberRoleDTO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.project.not.exist");
        }
        if (ResourceLevel.ORGANIZATION.value().equals(memberRoleDTO.getSourceType())
                && tenantMapper.selectByPrimaryKey(memberRoleDTO.getSourceId()) == null) {
            throw new CommonException("error.member_role.insert.organization.not.exist");
        }
        if (memberRoleMapper.selectOne(memberRoleDTO) != null) {
            throw new CommonException("error.member_role.has.existed");
        }
        if (memberRoleMapper.insertSelective(memberRoleDTO) != 1) {
            throw new CommonException("error.member_role.create");
        }
        //如果是平台root更新user表
        if (SITE_ROOT.equals(roleDTO.getCode())) {
            User userDTO = userMapper.selectByPrimaryKey(memberRoleDTO.getMemberId());
            userDTO.setAdmin(true);
            userMapper.updateByPrimaryKey(userDTO);
        }
        return memberRoleMapper.selectByPrimaryKey(memberRoleDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRole> insertOrUpdateRolesOfUserByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType) {
        return insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, memberId, memberRoles, sourceType, false);
    }

    @Override
    @Saga(code = MEMBER_ROLE_UPDATE, description = "iam更新用户角色", inputSchemaClass = List.class)
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRole> insertOrUpdateRolesOfUserByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType, Boolean syncAll) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        User userDTO = userAssertHelper.userNotExisted(memberId);
        List<MemberRole> returnList = new ArrayList<>();
        Set<String> previousRoleLabels = roleC7nMapper.listLabelByTenantIdAndUserId(memberId, sourceId);

        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
        userMemberEventMsg.setResourceId(sourceId);
        userMemberEventMsg.setUserId(memberId);
        userMemberEventMsg.setResourceType(sourceType);
        userMemberEventMsg.setPreviousRoleLabels(previousRoleLabels);
        userMemberEventMsg.setUsername(userDTO.getLoginName());
        userMemberEventMsg.setSyncAll(syncAll);

        Set<Long> ownRoleIds = insertOrUpdateRolesByMemberIdExecute(userId,
                isEdit, sourceId, memberId, sourceType, memberRoles, returnList, MemberType.USER.value());
        if (!ownRoleIds.isEmpty()) {
            userMemberEventMsg.setRoleLabels(labelC7nMapper.selectLabelNamesInRoleIds(ownRoleIds));
        }
        userMemberEventPayloads.add(userMemberEventMsg);
        sendEvent(userMemberEventPayloads, MEMBER_ROLE_UPDATE);
        return returnList;
    }

    public Set<Long> insertOrUpdateRolesByMemberIdExecute(Long fromUserId, Boolean isEdit, Long sourceId,
                                                          Long memberId, String sourceType,
                                                          List<MemberRole> memberRoleList,
                                                          List<MemberRole> returnList, String memberType) {
        MemberRole memberRole = new MemberRole();
        memberRole.setMemberId(memberId);
        memberRole.setMemberType(memberType);
        memberRole.setSourceId(sourceId);
        memberRole.setSourceType(sourceType);
        List<MemberRole> existingMemberRoleList = memberRoleMapper.select(memberRole);
        List<Long> existingRoleIds =
                existingMemberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        List<Long> newRoleIds = memberRoleList.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        //交集，传入的roleId与数据库里存在的roleId相交
        List<Long> intersection = existingRoleIds.stream().filter(newRoleIds::contains).collect(Collectors.toList());
        //传入的roleId与交集的差集为要插入的roleId
        List<Long> insertList = newRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        //数据库存在的roleId与交集的差集为要删除的roleId
        List<Long> deleteList = existingRoleIds.stream().filter(item ->
                !intersection.contains(item)).collect(Collectors.toList());
        returnList.addAll(existingMemberRoleList);
        List<MemberRole> memberRoleDTOS = new ArrayList<>();
        insertList.forEach(roleId -> {
            MemberRole mr = new MemberRole();
            mr.setRoleId(roleId);
            mr.setMemberId(memberId);
            mr.setMemberType(memberType);
            mr.setSourceType(sourceType);
            mr.setSourceId(sourceId);
            MemberRole memberRoleDTO = insertSelective(mr);
            returnList.add(memberRoleDTO);
            memberRoleDTOS.add(memberRoleDTO);
        });
        //批量添加，导入成功发送消息
        memberRoleDTOS.stream().forEach(memberRoleDTO -> {
            snedMsg(sourceType, fromUserId, memberRoleDTO, sourceId, memberRoleDTOS);
        });

        if (isEdit != null && isEdit && !deleteList.isEmpty()) {
            memberRoleC7nMapper.selectDeleteList(memberId, sourceId, memberType, sourceType, deleteList)
                    .forEach(t -> {
                        if (t != null) {
                            memberRoleMapper.deleteByPrimaryKey(t);
                            returnList.removeIf(memberRoleDTO -> memberRoleDTO.getId().equals(t));
                        }
                    });
        }
        //查当前用户/客户端有那些角色
        return memberRoleMapper.select(memberRole)
                .stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public ResponseEntity<Resource> downloadTemplatesByResourceLevel(String suffix, String resourceLevel) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("charset", "utf-8");
        //设置下载文件名
        String filename = "用户角色关系导入模板." + suffix;
        try {
            filename = URLEncoder.encode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.info("url encodes exception: {}", e.getMessage());
            throw new CommonException("error.encode.url");
        }
        headers.add("Content-Disposition", "attachment;filename=\"" + filename + "\"");
        InputStream inputStream;
        // 根据层级，设置excel文件路径
        String excelPath;
        if (ResourceLevel.SITE.value().equals(resourceLevel)) {
            excelPath = SITE_MEMBERROLE_TEMPLATES_PATH + DOT_SEPARATOR + suffix;
        } else if (ResourceLevel.ORGANIZATION.value().equals(resourceLevel)) {
            excelPath = ORGANIZATION_MEMBERROLE_TEMPLATES_PATH + DOT_SEPARATOR + suffix;
        } else if (ResourceLevel.PROJECT.value().equals(resourceLevel)) {
            excelPath = PROJECT_MEMBERROLE_TEMPLATES_PATH + DOT_SEPARATOR + suffix;
        } else {
            return null;
        }
        // 根据excel类型，设置响应头mediaType
        String mediaTypeValue;
        if (ExcelSuffix.XLS.value().equals(suffix)) {
            mediaTypeValue = "application/vnd.ms-excel";
        } else if (ExcelSuffix.XLSX.value().equals(suffix)) {
            mediaTypeValue = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            return null;
        }

        inputStream = this.getClass().getResourceAsStream(excelPath);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(mediaTypeValue))
                .body(new InputStreamResource(inputStream));

    }

    @Override
    @Transactional
    public void import2MemberRole(Long sourceId, String sourceType, MultipartFile file) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        validateSourceId(sourceId, sourceType);
        ExcelReadConfig excelReadConfig = initExcelReadConfig();
        long begin = System.currentTimeMillis();
        try {
            List<ExcelMemberRoleDTO> memberRoles = ExcelReadHelper.read(file, ExcelMemberRoleDTO.class, excelReadConfig);
            if (memberRoles.isEmpty()) {
                throw new CommonException("error.excel.memberRole.empty");
            }
            UploadHistoryDTO uploadHistory = initUploadHistory(sourceId, sourceType);
            long end = System.currentTimeMillis();
            logger.info("read excel for {} millisecond", (end - begin));
            excelImportUserTask.importMemberRole(userDetails.getUserId(), memberRoles, uploadHistory, finishFallback);
        } catch (IOException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new CommonException("error.excel.read", e);
        } catch (IllegalArgumentException e) {
            throw new CommonException("error.excel.illegal.column", e);
        }
    }

    private void validateSourceId(Long sourceId, String sourceType) {
        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)
                && tenantMapper.selectByPrimaryKey(sourceId) == null) {
            throw new CommonException("error.organization.not.exist");
        }
        if (ResourceLevel.PROJECT.value().equals(sourceType)
                && projectMapper.selectByPrimaryKey(sourceId) == null) {
            throw new CommonException("error.project.not.exist", sourceId);
        }
    }

    private ExcelReadConfig initExcelReadConfig() {
        ExcelReadConfig excelReadConfig = new ExcelReadConfig();
        String[] skipSheetNames = {"readme"};
        Map<String, String> propertyMap = new HashMap<>();
        propertyMap.put("登录名*", "loginName");
        propertyMap.put("角色编码*", "roleCode");
        excelReadConfig.setSkipSheetNames(skipSheetNames);
        excelReadConfig.setPropertyMap(propertyMap);
        return excelReadConfig;
    }

    private UploadHistoryDTO initUploadHistory(Long sourceId, String sourceType) {
        UploadHistoryDTO uploadHistory = new UploadHistoryDTO();
        uploadHistory.setBeginTime(new Date(System.currentTimeMillis()));
        uploadHistory.setType("member-role");
        uploadHistory.setUserId(DetailsHelper.getUserDetails().getUserId());
        uploadHistory.setSourceId(sourceId);
        uploadHistory.setSourceType(sourceType);
        if (uploadHistoryMapper.insertSelective(uploadHistory) != 1) {
            throw new CommonException("error.uploadHistory.insert");
        }
        return uploadHistoryMapper.selectByPrimaryKey(uploadHistory);
    }

    /**
     * 删除项目下所有角色 发送消息
     *
     * @param projectId
     * @param userId
     */
    private void mgsDeleteUserRoles(Long projectId, Long userId) {
        MessageSender messageSender = new MessageSender();
        messageSender.setMessageCode(SendSettingBaseEnum.DELETE_USER_ROLES.value());
        messageSender.setTenantId(0L);

        Map<String, Object> map = new HashMap<>();
        map.put("createdAt", new Date());
        map.put("eventName", SendSettingBaseEnum.map.get(SendSettingBaseEnum.DELETE_USER_ROLES.value()));
        map.put("objectKind", SendSettingBaseEnum.DELETE_USER_ROLES.value());
        map.put("projectId", projectId);
        User user = userMapper.selectByPrimaryKey(userId);
        map.put("loginName", user.getLoginName());
        map.put("userName", user.getRealName());
        messageSender.setObjectArgs(map);

        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName(), projectId);

        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
        objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), projectDTO.getOrganizationId());
        messageSender.setAdditionalInformation(objectMap);
        messageClient.async().sendMessage(messageSender);
    }

    private void snedMsg(String sourceType, Long fromUserId, MemberRole memberRoleDTO, Long sourceId, List<MemberRole> memberRoleDTOS) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        Role roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
        Map<String, String> params = new HashMap<>();
        if (ResourceLevel.SITE.value().equals(sourceType)) {
            if (SITE_ROOT.equals(roleDTO.getCode())) {
                messageSendService.sendSiteAddRoot(ROOT_BUSINESS_TYPE_CODE, memberRoleDTO.getMemberId());
            } else {
                User user = userC7nService.queryInfo(memberRoleDTO.getMemberId());
                messageSendService.sendSiteAddUserMsg(user, roleDTO.getName());
            }
        }
        User user = userC7nService.queryInfo(fromUserId);
        params.put("loginName", user.getLoginName());
        params.put("userName", user.getRealName());
        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
            Tenant tenant = tenantMapper.selectTenantDetails(sourceId);
            params.put("organizationName", tenant.getTenantName());
            params.put("roleName", roleDTO.getName());
            params.put("organizationId", String.valueOf(tenant.getTenantId()));
            params.put("addCount", String.valueOf(1));
            params.put("userList", JSON.toJSONString(userC7nService.getWebHookUser(memberRoleDTO.getMemberId())));
            messageSendService.sendAddMemberMsg(tenant, params, BUSINESS_TYPE_CODE, DetailsHelper.getUserDetails().getUserId());
        }
        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
            ProjectDTO projectDTO = projectC7nService.queryProjectById(sourceId, true, true, true);
            params.put("projectName", projectDTO.getName());
            params.put("roleName", roleDTO.getName());
            params.put("organizationId", String.valueOf(projectDTO.getOrganizationId()));
            params.put("addCount", String.valueOf(1));
            params.put("userList", JSON.toJSONString(userC7nService.getWebHookUser(memberRoleDTO.getMemberId())));
            params.put("resultDetailUrl", String.format(DETAILS_URL, frontUrl, projectDTO.getId(), projectDTO.getName(), projectDTO.getOrganizationId()));
            messageSendService.sendProjectAddUserMsg(projectDTO, params, PROJECT_ADD_USER, DetailsHelper.getUserDetails().getUserId());
        }
    }

    private void sendEvent(List<UserMemberEventPayload> userMemberEventPayloads, String code) {
        try {
            String input = mapper.writeValueAsString(userMemberEventPayloads);
            String refIds = userMemberEventPayloads.stream().map(t -> t.getUserId() + "").collect(Collectors.joining(","));
            String level = userMemberEventPayloads.get(0).getResourceType();
            Long sourceId = userMemberEventPayloads.get(0).getResourceId();
            producer.apply(StartSagaBuilder.newBuilder()
                            .withSagaCode(code)
                            .withJson(input)
                            .withRefType("users")
                            .withRefId(refIds)
                            .withLevel(ResourceLevel.valueOf(level.toUpperCase()))
                            .withSourceId(sourceId),
                    builder -> {
                    });
        } catch (Exception e) {
            throw new CommonException("error.iRoleMemberServiceImpl.updateMemberRole.event", e);
        }
    }

    @Override
    public List<MemberRole> insertOrUpdateRolesOfClientByMemberId(Boolean isEdit, Long sourceId, Long memberId, List<MemberRole> memberRoles, String sourceType) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        Client client = clientMapper.selectByPrimaryKey(memberId);
        if (client == null) {
            throw new CommonException("error.client.not.exist");
        }
        List<MemberRole> returnList = new ArrayList<>();
        insertOrUpdateRolesByMemberIdExecute(userId, isEdit,
                sourceId,
                memberId,
                sourceType,
                memberRoles,
                returnList, MemberType.CLIENT.value());
        return returnList;
    }

    private void deleteRoleForProject(Long sourceId,
                                      List<Long> roleIds,
                                      Long userId) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
        // 当前项目需要删除的 memberRoleIds
        List<MemberRole> projectMemberRoles = projectPermissionMapper.listMemberRoleByProjectIdAndUserId(sourceId, userId, roleIds);
        // 获取当前用户在该组织下其他项目用到的memberRoleIds
        List<Long> otherMemberRoleIds = projectPermissionMapper.listMemberRoleWithOutProjectId(sourceId, userId, projectDTO.getOrganizationId(), roleIds).stream().map(MemberRole::getId).collect(Collectors.toList());
        // 组织层和项目层都要被删除的角色
        List<MemberRole> delMemberRoles = new ArrayList<>();
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
        projectMemberRoles.forEach(t -> {
            if (!otherMemberRoleIds.contains(t.getId())) {
                t.setAdditionalParams(additionalParams);
                delMemberRoles.add(t);
            }
        });
        if (!CollectionUtils.isEmpty(delMemberRoles)) {
            memberRoleService.batchDeleteMemberRole(sourceId, delMemberRoles);
        }
        projectPermissionMapper.deleteByIds(sourceId, projectMemberRoles.stream().map(MemberRole::getId).collect(Collectors.toSet()));

    }

    /**
     * 根据数据批量删除 member-role 记录
     *
     * @param data   数据
     * @param isRole data的键是否是 roleId
     */
    private void deleteFromMap(Map<Long, List<Long>> data, boolean isRole, String memberType, Long sourceId, String sourceType, boolean doSendEvent, List<UserMemberEventPayload> userMemberEventPayloads, Boolean syncAll) {
        for (Map.Entry<Long, List<Long>> entry : data.entrySet()) {
            Long key = entry.getKey();
            List<Long> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                values.forEach(id -> {
                    Long roleId;
                    Long memberId;
                    if (isRole) {
                        roleId = key;
                        memberId = id;
                    } else {
                        roleId = id;
                        memberId = key;
                    }
                    UserMemberEventPayload userMemberEventPayload =
                            delete(roleId, memberId, memberType, sourceId, sourceType, doSendEvent);
                    if (userMemberEventPayload != null) {
                        userMemberEventPayload.setSyncAll(syncAll);
                        userMemberEventPayloads.add(userMemberEventPayload);
                    }
                });
            }
        }
    }

    private UserMemberEventPayload delete(Long roleId, Long memberId, String memberType,
                                          Long sourceId, String sourceType, boolean doSendEvent) {
        MemberRole memberRole = new MemberRole();
        memberRole.setRoleId(roleId);
        memberRole.setMemberId(memberId);
        memberRole.setMemberType(memberType);
        memberRole.setSourceId(sourceId);
        memberRole.setSourceType(sourceType);
        MemberRole mr = memberRoleMapper.selectOne(memberRole);
        if (mr == null) {
            throw new CommonException(MEMBER_ROLE_NOT_EXIST_EXCEPTION, roleId, memberId);
        }
        memberRoleMapper.deleteByPrimaryKey(mr.getId());
        UserMemberEventPayload userMemberEventMsg = null;
        //查询移除的role所包含的所有Label
        if (doSendEvent) {
            userMemberEventMsg = new UserMemberEventPayload();
            userMemberEventMsg.setResourceId(sourceId);
            userMemberEventMsg.setResourceType(sourceType);
            User user = userAssertHelper.userNotExisted(memberId);
            userMemberEventMsg.setUsername(user.getLoginName());
            userMemberEventMsg.setUserId(memberId);
        }
        return userMemberEventMsg;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = MEMBER_ROLE_UPDATE, description = "iam更新用户角色", inputSchemaClass = List.class)
    public void updateMemberRole(Long fromUserId, List<UserMemberEventPayload> userMemberEventPayloads, ResourceLevel level, Long sourceId) {
        // 发送saga同步角色
        producer.apply(StartSagaBuilder.newBuilder()
                        .withRefId(fromUserId.toString())
                        .withRefType("user")
                        .withSourceId(sourceId)
                        .withLevel(level)
                        .withSagaCode(MEMBER_ROLE_UPDATE)
                        .withPayloadAndSerialize(userMemberEventPayloads),
                builder -> {
                });
    }

    @Override
    @Saga(code = MEMBER_ROLE_DELETE, description = "删除用户角色", inputSchemaClass = List.class)
    public void deleteMemberRoleForSaga(Long userId, List<UserMemberEventPayload> userMemberEventPayloads, ResourceLevel level, Long sourceId) {
        // 发送saga同步角色
        producer.apply(StartSagaBuilder.newBuilder()
                        .withRefId(userId.toString())
                        .withRefType("user")
                        .withSourceId(sourceId)
                        .withLevel(level)
                        .withSagaCode(MEMBER_ROLE_DELETE)
                        .withPayloadAndSerialize(userMemberEventPayloads),
                builder -> {
                });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganizationMemberRole(Long tenantId, Long userId, List<Role> roles) {
        Tenant tenant = tenantMapper.selectByPrimaryKey(tenantId);
        User user = userMapper.selectByPrimaryKey(userId);


        // 查询用户拥有的组织层角色
        List<MemberRole> memberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(tenantId, userId, RoleLabelEnum.TENANT_ROLE.value());
        Set<Long> newIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> oldIds = memberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        // 要添加的角色
        Set<Long> insertIds = newIds.stream().filter(id -> !oldIds.contains(id)).collect(Collectors.toSet());
        // 要删除的角色
        Set<Long> deleteIds = oldIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());
        Long operatorId = DetailsHelper.getUserDetails().getUserId();

        List<MemberRole> insertMemberRoles = insertIds.stream().map(id -> {
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(userId);
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setRoleId(id);
            memberRole.setSourceId(tenantId);
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(tenantId);
            Map<String, Object> additionalParams = new HashMap<>();
            additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
            memberRole.setAdditionalParams(additionalParams);
            return memberRole;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertMemberRoles)) {
            memberRoleService.batchAssignMemberRoleInternal(insertMemberRoles);
        }

        List<MemberRole> deleteMemberRoles = deleteIds.stream().map(id -> {
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(userId);
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setRoleId(id);
            memberRole.setSourceId(tenantId);
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(tenantId);
            Map<String, Object> additionalParams = new HashMap<>();
            additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
            memberRole.setAdditionalParams(additionalParams);
            return memberRole;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteMemberRoles)) {
            memberRoleRepository.batchDelete(deleteMemberRoles);
        }

        List<MemberRole> newMemberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(tenantId, userId, RoleLabelEnum.TENANT_ROLE.value());
        // 用户现在拥有的角色标签
        Set<Long> newRoleIds = newMemberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        Set<String> labelNames = new HashSet<>();
        if (!CollectionUtils.isEmpty(newRoleIds)) {
            labelNames = labelC7nMapper.selectLabelNamesInRoleIds(newRoleIds);
        }
        Set<String> previousRoleLabels = new HashSet<>();
        if (!CollectionUtils.isEmpty(oldIds)) {
            previousRoleLabels = labelC7nMapper.selectLabelNamesInRoleIds(oldIds);
        }
        // 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(tenantId);
        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
        userMemberEventPayload.setPreviousRoleLabels(previousRoleLabels);
        userMemberEventPayloads.add(userMemberEventPayload);
        updateMemberRole(userId, userMemberEventPayloads, ResourceLevel.ORGANIZATION, tenantId);

        // 发送消息
        insertIds.forEach(roleId -> {
            Role role = roleMapper.selectByPrimaryKey(roleId);
            List<User> userList = new ArrayList<>();
            userList.add(user);
            messageSendService.sendAddMemberMsg(tenant, role.getName(), userList, operatorId);
        });
    }

    @Override
    public void addTenantRoleForUser(Long tenantId, Long userId, Set<Long> roleIds, Long operatorId) {
        Tenant tenant = tenantMapper.selectByPrimaryKey(tenantId);
        User user = userMapper.selectByPrimaryKey(userId);

        List<MemberRole> memberRoleList = new ArrayList<>();
        roleIds.forEach(roleId -> {
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(userId);
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setRoleId(roleId);
            memberRole.setSourceId(tenantId);
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(tenantId);
            memberRoleList.add(memberRole);
        });

        memberRoleService.batchAssignMemberRoleInternal(memberRoleList);

        List<MemberRole> newMemberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(tenantId, userId, RoleLabelEnum.TENANT_ROLE.value());
        // 用户现在拥有的角色标签
        Set<Long> newRoleIds = newMemberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        Set<String> labelNames = new HashSet<>();
        if (!CollectionUtils.isEmpty(newRoleIds)) {
            labelNames = labelC7nMapper.selectLabelNamesInRoleIds(newRoleIds);
        }
        Set<String> previousRoleLabels = roleC7nMapper.listLabelByTenantIdAndUserId(userId, tenantId);

        // 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(tenantId);
        userMemberEventPayload.setPreviousRoleLabels(previousRoleLabels);
        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
        userMemberEventPayloads.add(userMemberEventPayload);
        updateMemberRole(userId, userMemberEventPayloads, ResourceLevel.ORGANIZATION, tenantId);

        // 发送消息
        roleIds.forEach(roleId -> {
            Role role = roleMapper.selectByPrimaryKey(roleId);
            List<User> userList = new ArrayList<>();
            userList.add(user);
            messageSendService.sendAddMemberMsg(tenant, role.getName(), userList, operatorId);
        });
    }

    @Override
    public Set<Long> listUserPermission(Long userId, Set<Long> psIds, Long organizationId) {
        return memberRoleC7nMapper.listUserPermission(userId, psIds, organizationId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public void insertAndSendEvent(Long fromUserId, User userDTO, MemberRole memberRole, String loginName) {

        Role roleDTO = roleMapper.selectByPrimaryKey(memberRole.getRoleId());
        organizationUserService.sendCreateUserAndUpdateRoleSaga(fromUserId, userDTO, Arrays.asList(roleDTO), memberRole.getSourceType(), memberRole.getSourceId());
    }

    /**
     * 删除用户项目下角色
     *
     * @param projectId
     * @param userId
     * @param roleIds   roleIds不为null，删除指定角色
     * @param syncAll
     */
    @Saga(code = MEMBER_ROLE_DELETE, description = "iam删除用户角色")
    public void deleteProjectRole(Long projectId, Long userId, List<Long> roleIds, Boolean syncAll) {
        deleteRoleForProject(projectId, roleIds, userId);
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
        userMemberEventMsg.setResourceId(projectId);
        userMemberEventMsg.setResourceType(ResourceLevel.PROJECT.value());
        User user = userAssertHelper.userNotExisted(userId);
        userMemberEventMsg.setUsername(user.getLoginName());
        userMemberEventMsg.setUserId(userId);
        userMemberEventMsg.setSyncAll(syncAll);
        if (!CollectionUtils.isEmpty(roleIds)) {
            Set<Long> allRoleIds = projectPermissionMapper.listMemberRoleByProjectIdAndUserId(projectId, userId, null).stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
            allRoleIds.removeAll(roleIds);
            userMemberEventMsg.setRoleLabels(labelC7nMapper.selectLabelNamesInRoleIds(allRoleIds));
        }
        userMemberEventPayloads.add(userMemberEventMsg);
        deleteMemberRoleForSaga(userId, userMemberEventPayloads, ResourceLevel.PROJECT, projectId);
    }


}
