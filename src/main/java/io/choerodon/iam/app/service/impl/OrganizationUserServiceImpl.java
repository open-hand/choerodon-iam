package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.User.*;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.app.service.TenantService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.validator.UserValidator;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.annotation.OperateLog;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.MemberType;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;
import io.choerodon.iam.infra.mapper.MemberRoleC7nMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.RandomInfoGenerator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

@Service
@RefreshScope
public class OrganizationUserServiceImpl implements OrganizationUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationUserServiceImpl.class);
    private static final String BUSINESS_TYPE_CODE = "addMember";
    private static final String ERROR_ORGANIZATION_USER_NUM_MAX = "error.organization.user.num.max";
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;
    @Value("${spring.application.name:default}")
    private String serviceName;

    private final ObjectMapper mapper = new ObjectMapper();

    private final LabelC7nMapper labelC7nMapper;
    private final MemberRoleService memberRoleService;
    private final MemberRoleC7nMapper memberRoleC7nMapper;
    private final OrganizationAssertHelper organizationAssertHelper;
    private final OrganizationResourceLimitService organizationResourceLimitService;
    private final RandomInfoGenerator randomInfoGenerator;
    private final RoleMemberService roleMemberService;
    private final RoleService roleService;
    private final TenantService tenantService;
    private final TransactionalProducer producer;
    private final UserAssertHelper userAssertHelper;
    private final UserC7nMapper userC7nMapper;
    private final UserC7nService userC7nService;
    private final UserMapper userMapper;
    private final UserPasswordService userPasswordService;
    private final UserRepository userRepository;
    private final UserService userService;

    public OrganizationUserServiceImpl(LabelC7nMapper labelC7nMapper,
                                       MemberRoleC7nMapper memberRoleC7nMapper,
                                       OrganizationAssertHelper organizationAssertHelper,
                                       OrganizationResourceLimitService organizationResourceLimitService,
                                       RandomInfoGenerator randomInfoGenerator,
                                       RoleService roleService,
                                       TenantService tenantService,
                                       TransactionalProducer producer,
                                       UserAssertHelper userAssertHelper,
                                       UserC7nMapper userC7nMapper,
                                       UserC7nService userC7nService,
                                       UserMapper userMapper,
                                       UserPasswordService userPasswordService,
                                       UserRepository userRepository,
                                       MemberRoleService memberRoleService,
                                       RoleMemberService roleMemberService,
                                       UserService userService) {
        this.labelC7nMapper = labelC7nMapper;
        this.memberRoleC7nMapper = memberRoleC7nMapper;
        this.memberRoleService = memberRoleService;
        this.organizationAssertHelper = organizationAssertHelper;
        this.organizationResourceLimitService = organizationResourceLimitService;
        this.randomInfoGenerator = randomInfoGenerator;
        this.producer = producer;
        this.roleMemberService = roleMemberService;
        this.roleService = roleService;
        this.tenantService = tenantService;
        this.userAssertHelper = userAssertHelper;
        this.userC7nMapper = userC7nMapper;
        this.userC7nService = userC7nService;
        this.userMapper = userMapper;
        this.userPasswordService = userPasswordService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public Page<User> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, PageRequest pageable, String loginName, String realName,
                                                                   String roleName, Boolean enabled, Boolean locked, String params) {
        // todo 列表排序？？？
        Page<User> userPage = PageHelper.doPageAndSort(pageable, () -> userC7nMapper.listOrganizationUser(organizationId, loginName, realName, roleName, enabled, locked, params));
        List<User> userList = userPage.getContent();
        // 添加用户角色
        if (!CollectionUtils.isEmpty(userList)) {
            Set<Long> userIds = userList.stream().map(User::getId).collect(Collectors.toSet());
            List<MemberRole> memberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIds(organizationId, userIds, roleName, RoleLabelEnum.TENANT_ROLE.value());

            if (!CollectionUtils.isEmpty(memberRoles)) {
                Map<Long, List<MemberRole>> roleMap = memberRoles.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
                userList.forEach(user -> {
                    List<MemberRole> memberRoleList = roleMap.get(user.getId());
                    if (!CollectionUtils.isEmpty(memberRoleList)) {
                        user.setRoles(memberRoleList.stream().map(MemberRole::getRole).collect(Collectors.toList()));
                    }
                });
            }
        }
        return userPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    @OperateLog(type = "createUserOrg", content = "%s创建用户%s", level = {ResourceLevel.ORGANIZATION})
    public User createUserWithRoles(Long fromUserId, User user) {
        organizationResourceLimitService.checkEnableCreateUserOrThrowE(user.getOrganizationId(), 1);
        List<Role> userRoles = user.getRoles();
        // 将role转为memberRole， memberId不用给
        user.setMemberRoleList(role2MemberRole(user.getOrganizationId(), user.getRoles()));
        User result = userService.createUser(user);
        if (devopsMessage) {
            sendUserCreationSaga(fromUserId, result, userRoles, ResourceLevel.ORGANIZATION.value(), result.getOrganizationId());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    @OperateLog(type = "createUserOrg", content = "%s创建用户%s", level = {ResourceLevel.ORGANIZATION})
    public User createUserWithRoles(Long organizationId, User user, boolean checkPassword, boolean checkRoles) {
        organizationResourceLimitService.checkEnableCreateUserOrThrowE(organizationId, 1);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserValidator.validateCreateUserWithRoles(user, checkRoles);
        organizationAssertHelper.notExisted(organizationId);
        userAssertHelper.emailExisted(user.getEmail());
        user.setLoginName(randomInfoGenerator.randomLoginName());
        List<Role> userRoles = user.getRoles();
        // 将role转为memberRole， memberId不用给
        user.setMemberRoleList(role2MemberRole(user.getOrganizationId(), user.getRoles()));
        if (devopsMessage) {
            user = userService.createUser(user);
            createUserAndUpdateRole(userId, user, userRoles, ResourceLevel.ORGANIZATION.value(), organizationId);
        } else {
            user = userService.createUser(user);
        }
        return user;
    }

    private List<MemberRole> role2MemberRole(Long organizationId, List<Role> roles) {
        return roles.stream().map(role -> {
                    MemberRole memberRole = new MemberRole();
                    memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
                    memberRole.setAssignLevelValue(organizationId);
                    memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
                    memberRole.setSourceId(organizationId);
                    memberRole.setRoleId(role.getId());
                    memberRole.setMemberType(HiamMemberType.USER.value());
                    return memberRole;
                }
        ).collect(Collectors.toList());

    }

    @Override
    public void sendUserCreationSaga(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
        UserEventPayload userEventPayload = getUserEventPayload(userDTO);
        CreateAndUpdateUserEventPayload createAndUpdateUserEventPayload = new CreateAndUpdateUserEventPayload();
        createAndUpdateUserEventPayload.setUserEventPayload(userEventPayload);
        List<UserMemberEventPayload> userMemberEventPayloads = getListUserMemberEventPayload(fromUserId, userDTO, userRoles, value, organizationId);
        createAndUpdateUserEventPayload.setUserMemberEventPayloads(userMemberEventPayloads);
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(ORG_USER_CREAT),
                builder -> builder.withPayloadAndSerialize(createAndUpdateUserEventPayload)
                        .withRefId(createAndUpdateUserEventPayload.getUserEventPayload().getId())
                        .withSourceId(organizationId));
    }


    private UserEventPayload getUserEventPayload(User user) {
        UserEventPayload userEventPayload = new UserEventPayload();
        userEventPayload.setEmail(user.getEmail());
        userEventPayload.setId(user.getId().toString());
        userEventPayload.setName(user.getRealName());
        userEventPayload.setUsername(user.getLoginName());
        userEventPayload.setOrganizationId(user.getOrganizationId());
        return userEventPayload;
    }

    private List<UserMemberEventPayload> getListUserMemberEventPayload(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
        Long userId = userDTO.getId();
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoles)) {
            if (devopsMessage) {
                UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
                userMemberEventMsg.setResourceId(organizationId);
                userMemberEventMsg.setUserId(userId);
                userMemberEventMsg.setResourceType(value);
                userMemberEventMsg.setUsername(userDTO.getLoginName());
                List<Long> ownRoleIds = Optional.ofNullable(roleService.listRole(organizationId, userId)).map(r -> r.stream().map(Role::getId).collect(Collectors.toList())).orElse(Collections.emptyList());

                if (!ownRoleIds.isEmpty()) {
                    userMemberEventMsg.setRoleLabels(labelC7nMapper.selectLabelNamesInRoleIds(ownRoleIds));
                }
                userMemberEventPayloads.add(userMemberEventMsg);
            }
        }
        return userMemberEventPayloads;
    }

    private void generateUserEventPayload(List<UserEventPayload> payloads, User userDTO) {
        UserEventPayload payload = new UserEventPayload();
        payload.setEmail(userDTO.getEmail());
        payload.setId(userDTO.getId().toString());
        payload.setName(userDTO.getRealName());
        payload.setUsername(userDTO.getLoginName());
        payload.setOrganizationId(userDTO.getOrganizationId());
        payloads.add(payload);
    }


    private void sendBatchUserCreateEvent(List<UserEventPayload> payloads, Long orgId) {
        if (!payloads.isEmpty()) {
            try {
                String input = mapper.writeValueAsString(payloads);
                String refIds = payloads.stream().map(UserEventPayload::getId).collect(Collectors.joining(","));
                producer.apply(StartSagaBuilder.newBuilder()
                                .withSagaCode(USER_CREATE_BATCH)
                                .withJson(input)
                                .withRefType("user")
                                .withRefId(refIds)
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(orgId),
                        build -> {
                        });
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.batchCreateUser.event", e);
            } finally {
                payloads.clear();
            }
        }
    }


    @Override
    @Saga(code = USER_UPDATE, description = "iam更新用户", inputSchemaClass = UserEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(Long organizationId, User user) {
        // 1. 更新用户信息
        // ldap用户不能更新用户信息，只更新角色关系
        User userDetails = userRepository.selectByPrimaryKey(user.getId());
        if (Boolean.FALSE.equals(userDetails.ldapUser())) {
            userService.updateUserInternal(user);
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setEmail(user.getEmail());
            userEventPayload.setId(user.getId().toString());
            userEventPayload.setName(user.getRealName());
            userEventPayload.setUsername(user.getLoginName());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                producer.apply(StartSagaBuilder.newBuilder()
                                .withSagaCode(USER_UPDATE)
                                .withRefType("user")
                                .withRefId(userEventPayload.getId())
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(user.getOrganizationId())
                                .withJson(input),
                        builder -> {
                        });
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.updateUser.event", e);
            }
        }
        // 2. 更新用户角色
        // 查询用户拥有的组织层角色
        List<MemberRole> memberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(organizationId, user.getId(), RoleLabelEnum.TENANT_ROLE.value());
        List<Role> roles = user.getRoles();
        Set<Long> newIds = roles.stream().map(Role::getId).collect(Collectors.toSet());
        Set<Long> oldIds = memberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toSet());
        // 要添加的角色
        Set<Long> insertIds = newIds.stream().filter(id -> !oldIds.contains(id)).collect(Collectors.toSet());
        // 要删除的角色
        Set<Long> deleteIds = oldIds.stream().filter(id -> !newIds.contains(id)).collect(Collectors.toSet());

        List<MemberRole> insertMemberRoles = insertIds.stream().map(id -> {
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(user.getId());
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setRoleId(id);
            memberRole.setSourceId(organizationId);
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(organizationId);
            return memberRole;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(insertMemberRoles)) {
            memberRoleService.batchAssignMemberRoleInternal(insertMemberRoles);
        }

        List<MemberRole> deleteMemberRoles = deleteIds.stream().map(id -> {
            MemberRole memberRole = new MemberRole();
            memberRole.setMemberId(user.getId());
            memberRole.setMemberType(MemberType.USER.value());
            memberRole.setRoleId(id);
            memberRole.setSourceId(organizationId);
            memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRole.setAssignLevelValue(organizationId);
            return memberRole;
        }).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(deleteMemberRoles)) {
            memberRoleService.batchDeleteMemberRole(organizationId, deleteMemberRoles);
        }

        List<MemberRole> newMemberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIdAndRoleLable(organizationId, user.getId(), RoleLabelEnum.TENANT_ROLE.value());
        Set<String> labelNames = labelC7nMapper.selectLabelNamesInRoleIds(newMemberRoles.stream().map(MemberRole::getRoleId).collect(Collectors.toList()));

        // 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(user.getId());
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayload.setResourceId(organizationId);
        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
        userMemberEventPayloads.add(userMemberEventPayload);
        roleMemberService.updateMemberRole(user.getId(), userMemberEventPayloads, ResourceLevel.ORGANIZATION, organizationId);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "resetUserPassword", content = "%s重置%s的登录密码", level = {ResourceLevel.ORGANIZATION})
    public User resetUserPassword(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        User user = userAssertHelper.userNotExisted(userId);
        userAssertHelper.notExternalUser(organizationId, user.getOrganizationId());
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }

        userService.resetUserPassword(userId, organizationId);

        // send siteMsg
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userName", user.getRealName());
        paramsMap.put("defaultPassword", userPasswordService.getTenantDefaultPassword(organizationId));
        List<Long> userIds = Collections.singletonList(userId);
        userC7nService.sendNotice(userIds, "resetOrganizationUserPassword", paramsMap, organizationId, ResourceLevel.ORGANIZATION);

        return user;
    }


    @Override
    public User query(Long organizationId, Long id) {
        organizationAssertHelper.notExisted(organizationId);
        User user = userRepository.selectByPrimaryKey(id);
        user.setPassword(null);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "unlockUser", content = "%s解锁用户%s", level = {ResourceLevel.ORGANIZATION})
    public User unlock(Long organizationId, Long userId) {
        userService.unlockUser(userId, organizationId);
        return query(organizationId, userId);
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_ENABLE, description = "iam启用用户", inputSchemaClass = UserEventPayload.class)
    @OperateLog(type = "enableUser", content = "用户%s被%s启用", level = {ResourceLevel.ORGANIZATION})
    public User enableUser(Long organizationId, Long userId) {
        userService.unfrozenUser(userId, organizationId);
        User user = query(organizationId, userId);
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setUsername(user.getLoginName());
            userEventPayload.setId(userId.toString());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                producer.apply(StartSagaBuilder.newBuilder()
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(organizationId)
                                .withRefType("user")
                                .withRefId(userId.toString())
                                .withJson(input)
                                .withSagaCode(USER_ENABLE),
                        builder -> {
                        });
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.enableUser.event", e);
            }
        }
        return user;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_DISABLE, description = "iam停用用户", inputSchemaClass = UserEventPayload.class)
    @OperateLog(type = "disableUser", content = "用户%s已被%s停用", level = {ResourceLevel.ORGANIZATION})
    public User disableUser(Long organizationId, Long userId) {
        userService.frozenUser(userId, organizationId);
        User user = query(organizationId, userId);
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setUsername(user.getLoginName());
            userEventPayload.setId(userId.toString());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                producer.apply(StartSagaBuilder.newBuilder()
                                .withLevel(ResourceLevel.ORGANIZATION)
                                .withSourceId(organizationId)
                                .withRefType("user")
                                .withRefId(userId.toString())
                                .withJson(input)
                                .withSagaCode(USER_DISABLE),
                        builder -> {
                        });
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.disableUser.event", e);
            }
        }
        if (!Objects.isNull(user)) {
            // TODO 禁用成功后还要发送webhook json消息
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("loginName", user.getLoginName());
//            jsonObject.put("userName", user.getRealName());
//            jsonObject.put("enabled", user.getEnabled());
//            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                    SendSettingBaseEnum.STOP_USER.value(),
//                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.STOP_USER.value()),
//                    jsonObject,
//                    user.getLastUpdateDate(),
//                    userService.getWebHookUser(DetailsHelper.getUserDetails().getUserId())
//            );
//            Map<String, Object> params = new HashMap<>();
//
//            userService.sendNotice(DetailsHelper.getUserDetails().getUserId(), Arrays.asList(userId), SendSettingBaseEnum.STOP_USER.value(), params, organizationId, webHookJsonSendDTO);
        }
        return user;
    }

    @Override
    public List<ErrorUserVO> batchCreateUsersOnExcel(List<UserDTO> insertUsers, Long fromUserId, Long organizationId) {
        LOGGER.info("Start to batch insert {} users into tenant with id {}...", insertUsers.size(), organizationId);
        List<ErrorUserVO> errorUsers = new ArrayList<>();
        List<UserEventPayload> payloads = new ArrayList<>();
        boolean errorUserFlag = true;
        List<User> users = new ArrayList<>();
        for (User user : insertUsers) {
            User userDTO = null;
            try {
                user.setOrganizationId(organizationId);
                userDTO = ((OrganizationUserServiceImpl) AopContext.currentProxy()).createUserWithRoles(fromUserId, user);
            } catch (Exception e) {
                LOGGER.error("BatchCreateUsersOnExcel context", e);
                ErrorUserVO errorUser = new ErrorUserVO();
                BeanUtils.copyProperties(user, errorUser);
                if (e instanceof CommonException && ERROR_ORGANIZATION_USER_NUM_MAX.equals(((CommonException) e).getCode())) {
                    errorUser.setCause("组织用户数量已达上限：100，无法创建更多用户");
                } else {
                    errorUser.setCause("用户或角色插入异常, 异常code是: " + e.getMessage());
                }
                errorUsers.add(errorUser);
                errorUserFlag = false;
            }
            boolean userEnabled = userDTO != null && userDTO.getEnabled();
            if (devopsMessage && userEnabled) {
                generateUserEventPayload(payloads, userDTO);
            }
            if (errorUserFlag) {
                users.add(user);
            }
            errorUserFlag = true;
        }
        //导入成功过后，通知成员
        users.forEach(e -> {
            Map<String, String> params = new HashMap<>();
            Tenant organizationDTO = tenantService.queryTenant(e.getOrganizationId());
            params.put("organizationName", organizationDTO.getTenantName());
            params.put("roleName", e.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
            params.put("userList", JSON.toJSONString(insertUsers));
            params.put("organizationId", String.valueOf(organizationDTO.getTenantId()));
            params.put("addCount", String.valueOf(insertUsers.size()));
            userC7nService.sendNotice(Collections.singletonList(e.getId()), BUSINESS_TYPE_CODE, params, e.getOrganizationId(), ResourceLevel.ORGANIZATION);
        });

        sendBatchUserCreateEvent(payloads, insertUsers.get(0).getOrganizationId());
        LOGGER.info("Batch insert {} users into tenant with id {} processed...", insertUsers.size(), organizationId);
        return errorUsers;
    }

    @Override
    public void createUserAndUpdateRole(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
        UserEventPayload userEventPayload = getUserEventPayload(userDTO);
        CreateAndUpdateUserEventPayload createAndUpdateUserEventPayload = new CreateAndUpdateUserEventPayload();
        createAndUpdateUserEventPayload.setUserEventPayload(userEventPayload);
        List<UserMemberEventPayload> userMemberEventPayloads = getListUserMemberEventPayload(fromUserId, userDTO, userRoles, value, organizationId);
        createAndUpdateUserEventPayload.setUserMemberEventPayloads(userMemberEventPayloads);
        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(ORG_USER_CREAT)
                        .withPayloadAndSerialize(createAndUpdateUserEventPayload)
                        .withRefId(createAndUpdateUserEventPayload.getUserEventPayload().getId())
                        .withSourceId(organizationId),
                builder -> {
                });
    }
}
