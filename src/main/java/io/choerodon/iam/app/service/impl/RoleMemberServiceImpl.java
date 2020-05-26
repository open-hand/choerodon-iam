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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MemberRoleRepository;
import org.hzero.iam.infra.mapper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ExcelMemberRoleDTO;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.ExcelSuffix;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
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
    private static final String ROOT_BUSINESS_TYPE_CODE = "siteAddRoot";
    private static final String USER_BUSINESS_TYPE_CODE = "siteAddUser";
    private static final String BUSINESS_TYPE_CODE = "addMember";
    private static final String PROJECT_ADD_USER = "projectAddUser";
    private TenantMapper tenantMapper;
    private ProjectMapper projectMapper;

    private MemberRoleMapper memberRoleMapper;

    private MemberRoleC7nMapper memberRoleC7nMapper;

    private RoleMapper roleMapper;

    private UserAssertHelper userAssertHelper;

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    private final ObjectMapper mapper = new ObjectMapper();

    private SagaClient sagaClient;

    private LabelMapper labelMapper;

    private LabelC7nMapper labelC7nMapper;

    private ClientMapper clientMapper;

    private UploadHistoryMapper uploadHistoryMapper;

    private OrganizationUserService organizationUserService;

    private UserService userService;

    private ProjectAssertHelper projectAssertHelper;

    private UserMapper userMapper;

    private ExcelImportUserTask excelImportUserTask;

    private ExcelImportUserTask.FinishFallback finishFallback;
    private TransactionalProducer producer;
    private MemberRoleService memberRoleService;
    private ProjectUserMapper projectUserMapper;
    private MemberRoleRepository memberRoleRepository;


    public RoleMemberServiceImpl(TenantMapper tenantMapper,
                                 ProjectMapper projectMapper,
                                 MemberRoleMapper memberRoleMapper,
                                 MemberRoleC7nMapper memberRoleC7nMapper,
                                 RoleMapper roleMapper,
                                 UserAssertHelper userAssertHelper,
                                 SagaClient sagaClient,
                                 MemberRoleService memberRoleService,
                                 LabelMapper labelMapper,
                                 LabelC7nMapper labelC7nMapper,
                                 ClientMapper clientMapper,
                                 UploadHistoryMapper uploadHistoryMapper,
                                 OrganizationUserService organizationUserService,
                                 UserService userService,
                                 ProjectUserMapper projectUserMapper,
                                 UserMapper userMapper,
                                 ProjectAssertHelper projectAssertHelper,
                                 ExcelImportUserTask excelImportUserTask,
                                 ExcelImportUserTask.FinishFallback finishFallback,
                                 TransactionalProducer producer,
                                 MemberRoleRepository memberRoleRepository) {
        this.tenantMapper = tenantMapper;
        this.projectMapper = projectMapper;
        this.memberRoleMapper = memberRoleMapper;
        this.memberRoleC7nMapper = memberRoleC7nMapper;
        this.roleMapper = roleMapper;
        this.userAssertHelper = userAssertHelper;
        this.sagaClient = sagaClient;
        this.labelMapper = labelMapper;
        this.labelC7nMapper = labelC7nMapper;
        this.clientMapper = clientMapper;
        this.uploadHistoryMapper = uploadHistoryMapper;
        this.organizationUserService = organizationUserService;
        this.userService = userService;
        this.userMapper = userMapper;
        this.excelImportUserTask = excelImportUserTask;
        this.finishFallback = finishFallback;
        this.memberRoleService = memberRoleService;
        this.projectAssertHelper = projectAssertHelper;
        this.projectUserMapper = projectUserMapper;
        this.producer = producer;
        this.memberRoleRepository = memberRoleRepository;
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
        deleteProjectRole(projectId, userId, syncAll);
        //删除用户所有项目角色时发送web hook
//        JSONObject jsonObject = new JSONObject();
//        List<Long> collect = roleAssignmentDeleteDTO.getData().keySet().stream().collect(Collectors.toList());
//        jsonObject.put("projectId", roleAssignmentDeleteDTO.getSourceId());
        // TODO notify-service
//        jsonObject.put("user", JSON.toJSONString(userService.getWebHookUser(collect.get(0))));
//        UserDTO userDTO = userMapper.selectByPrimaryKey(collect.get(0));
//        WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                SendSettingBaseEnum.DELETE_USERROLES.value(),
//                SendSettingBaseEnum.map.get(SendSettingBaseEnum.DELETE_USERROLES.value()),
//                jsonObject,
//                userDTO.getLastUpdateDate(),
//                userService.getWebHookUser(DetailsHelper.getUserDetails().getUserId())
//        );
//        Map<String, Object> params = new HashMap<>();
//        userService.sendNotice(DetailsHelper.getUserDetails().getUserId(), Arrays.asList(userDTO.getId()), SendSettingBaseEnum.DELETE_USERROLES.value(), params, roleAssignmentDeleteDTO.getSourceId(), webHookJsonSendDTO);
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
        if (devopsMessage) {
            List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
            UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
            userMemberEventMsg.setResourceId(sourceId);
            userMemberEventMsg.setUserId(memberId);
            userMemberEventMsg.setResourceType(sourceType);
            userMemberEventMsg.setUsername(userDTO.getLoginName());
            userMemberEventMsg.setSyncAll(syncAll);

            List<Long> ownRoleIds = insertOrUpdateRolesByMemberIdExecute(userId,
                    isEdit, sourceId, memberId, sourceType, memberRoles, returnList, MemberType.USER.value());
            if (!ownRoleIds.isEmpty()) {
                userMemberEventMsg.setRoleLabels(labelC7nMapper.selectLabelNamesInRoleIds(ownRoleIds));
            }
            userMemberEventPayloads.add(userMemberEventMsg);
            sendEvent(userMemberEventPayloads, MEMBER_ROLE_UPDATE);
            return returnList;
        } else {
            insertOrUpdateRolesByMemberIdExecute(userId, isEdit,
                    sourceId,
                    memberId,
                    sourceType,
                    memberRoles,
                    returnList, MemberType.USER.value());
            return returnList;
        }
    }

    public List<Long> insertOrUpdateRolesByMemberIdExecute(Long fromUserId, Boolean isEdit, Long sourceId,
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
                .stream().map(MemberRole::getRoleId).collect(Collectors.toList());
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
    // TODO notify-service

    private void snedMsg(String sourceType, Long fromUserId, MemberRole memberRoleDTO, Long sourceId, List<MemberRole> memberRoleDTOS) {
//        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
//        Role roleDTO = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());
//        Map<String, Object> params = new HashMap<>();
//        if (ResourceLevel.SITE.value().equals(sourceType)) {
//            if (SITE_ROOT.equals(roleDTO.getCode())) {
//                userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), ROOT_BUSINESS_TYPE_CODE, Collections.EMPTY_MAP, sourceId);
//            } else {
//                params.put("roleName", roleDTO.getName());
//                userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), USER_BUSINESS_TYPE_CODE, params, sourceId);
//            }
//        }
//        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
//            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(sourceId);
//            params.put("organizationName", organizationDTO.getName());
//            params.put("roleName", roleDTO.getName());
//            //webhook json
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("organizationId", organizationDTO.getId());
//            jsonObject.put("addCount", 1);
//            WebHookJsonSendDTO.User webHookUser = userService.getWebHookUser(memberRoleDTO.getMemberId());
//            jsonObject.put("userList", JSON.toJSONString(Arrays.asList(webHookUser)));
//
//            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                    SendSettingBaseEnum.ADD_MEMBER.value(),
//                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.ADD_MEMBER.value()),
//                    jsonObject,
//                    new Date(),
//                    userService.getWebHookUser(fromUserId)
//            );
//            userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), BUSINESS_TYPE_CODE, params, sourceId, webHookJsonSendDTO);
//        }
//        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
//            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(sourceId);
//            params.put("projectName", projectDTO);
//            params.put("roleName", roleDTO.getName());
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("organizationId", projectDTO.getOrganizationId());
//            jsonObject.put("addCount", 1);
//            WebHookJsonSendDTO.User webHookUser = userService.getWebHookUser(memberRoleDTO.getMemberId());
//            jsonObject.put("userList", JSON.toJSONString(Arrays.asList(webHookUser)));
//
//            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                    SendSettingBaseEnum.PROJECT_ADDUSER.value(),
//                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.PROJECT_ADDUSER.value()),
//                    jsonObject,
//                    new Date(),
//                    userService.getWebHookUser(fromUserId)
//            );
//            userService.sendNotice(fromUserId, Arrays.asList(memberRoleDTO.getMemberId()), PROJECT_ADD_USER, params, sourceId, webHookJsonSendDTO);
//        }
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
                                      Long userId) {
        ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
        // 当前项目 memberRoleIds
        List<MemberRole> projectMemberRoles = projectUserMapper.listMemberRoleByProjectIdAndUserId(sourceId, userId);
        // 获取当前用户在该组织下其他项目用到的memberRoleIds
        List<Long> otherMemberRoleIds = projectUserMapper.listMemberRoleWithOutProjectId(sourceId, userId, projectDTO.getOrganizationId()).stream().map(MemberRole::getId).collect(Collectors.toList());
        // 组织层和项目层都要被删除的角色
        List<MemberRole> delMemberRoles = new ArrayList<>();
        projectMemberRoles.forEach(t -> {
            if (!otherMemberRoleIds.contains(t.getId())) {
                delMemberRoles.add(t);
            }
        });
        if (!CollectionUtils.isEmpty(delMemberRoles)) {
            memberRoleService.batchDeleteMemberRole(sourceId, delMemberRoles);
        }
        projectUserMapper.deleteByIds(sourceId, projectMemberRoles.stream().map(MemberRole::getId).collect(Collectors.toList()));

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
                        .withSagaCode(MEMBER_ROLE_UPDATE)
                        .withPayloadAndSerialize(userMemberEventPayloads),
                builder -> {
                });

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrganizationMemberRole(Long tenantId, Long userId, List<Role> roles) {
        // 查询用户拥有的组织层角色
        List<MemberRole> memberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(tenantId, userId, RoleLabelEnum.TENANT_ROLE.value());
        Set<Long> newIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> oldIds = memberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        // 要添加的角色
        Set<Long> insertIds = newIds.stream().filter(id -> !oldIds.contains(id)).collect(Collectors.toSet());
        // 要删除的角色
        Set<Long> deleteIds = oldIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());

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
        List<Long> newRoleIds = newMemberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toList());
        Set<String> labelNames = new HashSet<>();
        if (!CollectionUtils.isEmpty(newRoleIds)) {
            labelNames = labelC7nMapper.selectLabelNamesInRoleIds(newRoleIds);
        }

        // 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(tenantId);
        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
        userMemberEventPayloads.add(userMemberEventPayload);
        updateMemberRole(userId, userMemberEventPayloads, ResourceLevel.ORGANIZATION, tenantId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public void insertAndSendEvent(Long fromUserId, User userDTO, MemberRole memberRole, String loginName) {

        Role roleDTO = roleMapper.selectByPrimaryKey(memberRole.getRoleId());
        organizationUserService.sendCreateUserAndUpdateRoleSaga(fromUserId, userDTO, Arrays.asList(roleDTO), memberRole.getSourceType(), memberRole.getSourceId());
    }

    @Saga(code = MEMBER_ROLE_DELETE, description = "iam删除用户角色")
    public void deleteProjectRole(Long projectId, Long userId, Boolean syncAll) {
        deleteRoleForProject(projectId, userId);
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
        userMemberEventMsg.setResourceId(projectId);
        userMemberEventMsg.setResourceType(ResourceLevel.PROJECT.value());
        User user = userAssertHelper.userNotExisted(userId);
        userMemberEventMsg.setUsername(user.getLoginName());
        userMemberEventMsg.setUserId(userId);
        userMemberEventMsg.setSyncAll(syncAll);
        deleteMemberRoleForSaga(userId, userMemberEventPayloads, ResourceLevel.PROJECT, projectId);
    }


}
