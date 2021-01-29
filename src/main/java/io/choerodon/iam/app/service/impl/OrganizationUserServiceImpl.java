package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.User.*;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.HiamMemberType;
import org.hzero.iam.saas.app.service.TenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.utils.ConvertUtils;
import io.choerodon.iam.api.validator.UserValidator;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.iam.api.vo.SagaInstanceDetails;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.constant.MessageCodeConstants;
import io.choerodon.iam.infra.dto.SysSettingDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.enums.SysSettingEnum;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.mapper.LabelC7nMapper;
import io.choerodon.iam.infra.mapper.MemberRoleC7nMapper;
import io.choerodon.iam.infra.mapper.SysSettingMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.CustomContextUtil;
import io.choerodon.iam.infra.utils.ExceptionUtil;
import io.choerodon.iam.infra.utils.RandomInfoGenerator;
import io.choerodon.iam.infra.utils.SagaInstanceUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

@Service
@RefreshScope
public class OrganizationUserServiceImpl implements OrganizationUserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationUserServiceImpl.class);
    private static final String BUSINESS_TYPE_CODE = "addMember";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String USER = "user";
    private static final String ERROR_ORGANIZATION_USER_NUM_MAX = "error.organization.user.num.max";
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
    private final TenantRepository tenantRepository;
    private final TransactionalProducer producer;
    private final UserAssertHelper userAssertHelper;
    private final UserC7nMapper userC7nMapper;
    private final UserC7nService userC7nService;
    private final UserPasswordService userPasswordService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MessageClient messageClient;
    private final MessageSendService messageSendService;
    private final PasswordPolicyRepository passwordPolicyRepository;
    private final SysSettingMapper sysSettingMapper;

    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;


    public OrganizationUserServiceImpl(LabelC7nMapper labelC7nMapper,
                                       MemberRoleC7nMapper memberRoleC7nMapper,
                                       OrganizationAssertHelper organizationAssertHelper,
                                       OrganizationResourceLimitService organizationResourceLimitService,
                                       RandomInfoGenerator randomInfoGenerator,
                                       RoleService roleService,
                                       TenantService tenantService,
                                       TenantRepository tenantRepository,
                                       TransactionalProducer producer,
                                       UserAssertHelper userAssertHelper,
                                       UserC7nMapper userC7nMapper,
                                       UserC7nService userC7nService,
                                       UserPasswordService userPasswordService,
                                       UserRepository userRepository,
                                       MemberRoleService memberRoleService,
                                       RoleMemberService roleMemberService,
                                       UserService userService,
                                       MessageClient messageClient,
                                       PasswordPolicyRepository passwordPolicyRepository,
                                       MessageSendService messageSendService,
                                       SysSettingMapper sysSettingMapper
    ) {
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
        this.tenantRepository = tenantRepository;
        this.userAssertHelper = userAssertHelper;
        this.userC7nMapper = userC7nMapper;
        this.userC7nService = userC7nService;
        this.userPasswordService = userPasswordService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.messageClient = messageClient;
        this.passwordPolicyRepository = passwordPolicyRepository;
        this.messageSendService = messageSendService;
        this.sysSettingMapper = sysSettingMapper;
    }

    @Override
    public Page<UserDTO> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, PageRequest pageable, String loginName, String realName,
                                                                      String roleName, Boolean enabled, Boolean locked, String params) {
        // todo 列表排序？？？
        Page<User> userPage = PageHelper.doPageAndSort(pageable, () -> userC7nMapper.listOrganizationUser(organizationId, loginName, realName, roleName, enabled, locked, params));
        Page<UserDTO> userDTOSPage = ConvertUtils.convertPage(userPage, UserDTO.class);
        List<UserDTO> userList = userDTOSPage.getContent();
        List<String> refIds = userList.stream().map(user -> String.valueOf(user.getId())).collect(Collectors.toList());
        Map<String, SagaInstanceDetails> stringSagaInstanceDetailsMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(refIds)) {
            stringSagaInstanceDetailsMap = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(USER, refIds, ORG_USER_CREAT));
        }
        // 添加用户角色
        if (!CollectionUtils.isEmpty(userList)) {
            Set<Long> userIds = userList.stream().map(User::getId).collect(Collectors.toSet());
            List<MemberRole> memberRoles = memberRoleC7nMapper.listMemberRoleByOrgIdAndUserIds(organizationId, userIds, roleName, RoleLabelEnum.TENANT_ROLE.value());

            if (!CollectionUtils.isEmpty(memberRoles)) {
                Map<Long, List<MemberRole>> roleMap = memberRoles.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
                Map<String, SagaInstanceDetails> finalStringSagaInstanceDetailsMap = stringSagaInstanceDetailsMap;
                userList.forEach(user -> {
                    List<MemberRole> memberRoleList = roleMap.get(user.getId());
                    if (!CollectionUtils.isEmpty(memberRoleList)) {
                        user.setRoles(memberRoleList.stream().map(MemberRole::getRole).collect(Collectors.toList()));
                    }
                    //用户状态启用
                    if (user.getEnabled()) {
                        user.setSagaInstanceId(SagaInstanceUtils.fillInstanceId(finalStringSagaInstanceDetailsMap, String.valueOf(user.getId())));
                    }
                });
            }
        }
        return userDTOSPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public User createUserWithRoles(Long fromUserId, User user) {
        organizationResourceLimitService.checkEnableCreateUserOrThrowE(user.getOrganizationId(), 1);
        List<Role> userRoles = user.getRoles();
        // 允许邮箱和手机号登录
        user.setEmailCheckFlag(BaseConstants.Flag.YES);
        user.setPhoneCheckFlag(BaseConstants.Flag.YES);

        List<Role> roles = user.getRoles();
        user.setMemberRoleList(role2MemberRole(user.getOrganizationId(), null, roles));
        User result = userService.createUserInternal(user);
        sendUserCreationSaga(fromUserId, result, userRoles, ResourceLevel.ORGANIZATION.value(), result.getOrganizationId());
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public User createUserWithRoles(Long organizationId, User user, boolean checkPassword, boolean checkRoles) {
        organizationResourceLimitService.checkEnableCreateUserOrThrowE(organizationId, 1);
        Long userId = DetailsHelper.getUserDetails().getUserId();
        UserValidator.validateCreateUserWithRoles(user, checkRoles);
        organizationAssertHelper.notExisted(organizationId);
        userAssertHelper.emailExisted(user.getEmail());
        user.setLoginName(randomInfoGenerator.randomLoginName());
        List<Role> userRoles = user.getRoles();
        user.setRoles(null);
        user.setMemberRoleList(role2MemberRole(user.getOrganizationId(), null, userRoles));
        user = userService.createUserInternal(user);

        sendCreateUserAndUpdateRoleSaga(userId, user, userRoles, ResourceLevel.ORGANIZATION.value(), organizationId);
        return user;
    }

    @Override
    public void sendUserCreationSaga(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
        UserEventPayload userEventPayload = getUserEventPayload(userDTO);
        CreateAndUpdateUserEventPayload createAndUpdateUserEventPayload = new CreateAndUpdateUserEventPayload();
        createAndUpdateUserEventPayload.setUserEventPayload(userEventPayload);
        List<UserMemberEventPayload> userMemberEventPayloads = getListUserMemberEventPayload(fromUserId, userDTO, userRoles, value, organizationId);
        createAndUpdateUserEventPayload.setUserMemberEventPayloads(userMemberEventPayloads);
        // ldap同步 用户没登陆问题
        CustomUserDetails userDetails = null;
        try {
            userDetails = UserUtils.getUserDetails();
        } catch (Exception e) {
            LOGGER.info("not login!");
        } finally {
            if (userDetails == null || userDetails.getUserId() == null) {
                CustomContextUtil.setUserContext(userDTO.getId());
            }
        }

        producer.apply(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(ORG_USER_CREAT)
                        .withSourceId(organizationId),
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
            UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
            userMemberEventMsg.setResourceId(organizationId);
            userMemberEventMsg.setUserId(userId);
            userMemberEventMsg.setResourceType(value);
            userMemberEventMsg.setUsername(userDTO.getLoginName());
            Set<Long> roleIds = userRoles.stream().map(Role::getId).collect(Collectors.toSet());
            userMemberEventMsg.setRoleLabels(labelC7nMapper.selectLabelNamesInRoleIds(roleIds));
            userMemberEventPayloads.add(userMemberEventMsg);
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
            user.setEmailCheckFlag(BaseConstants.Flag.YES);
            user.setPhoneCheckFlag(BaseConstants.Flag.YES);
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
        roleMemberService.updateOrganizationMemberRole(organizationId, user.getId(), user.getRoles());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public User resetUserPassword(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        User user = userAssertHelper.userNotExisted(userId);
        userAssertHelper.notExternalUser(organizationId, user.getOrganizationId());
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }
        String newPassword;
        PasswordPolicy passwordPolicy = passwordPolicyRepository.selectTenantPasswordPolicy(organizationId);
        if (passwordPolicy != null && passwordPolicy.getEnablePassword() && !StringUtils.isEmpty(passwordPolicy.getOriginalPassword())) {
            newPassword = passwordPolicy.getOriginalPassword();
        } else {
            SysSettingDTO sysSettingDTO = new SysSettingDTO();
            sysSettingDTO.setSettingKey(SysSettingEnum.DEFAULT_PASSWORD.value());
            newPassword = sysSettingMapper.selectOne(sysSettingDTO).getSettingValue();
        }

        userPasswordService.updateUserPassword(userId, newPassword, false);

        // 发送重置密码消息
        sendResetOrganizationUserPassword(organizationId, user);
        return user;
    }

    private void sendResetOrganizationUserPassword(Long organizationId, User user) {
        try {
            // 构建消息对象
            MessageSender messageSender = new MessageSender();
            // 消息code
            messageSender.setMessageCode(MessageCodeConstants.RESET_ORGANIZATION_USER_PASSWORD);
            // 默认为0L,都填0L,可不填写
            messageSender.setTenantId(0L);

            // 消息参数 消息模板中${projectName}
            Map<String, String> argsMap = new HashMap<>();
            argsMap.put(DEFAULT_PASSWORD, userPasswordService.getTenantDefaultPassword(organizationId));
            messageSender.setArgs(argsMap);

            //额外参数，用于逻辑过滤 包括项目id，环境id，devops的消息事件
            Map<String, Object> objectMap = new HashMap<>();
            //发送组织层和项目层消息时必填 当前组织id
            objectMap.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), organizationId);
            messageSender.setAdditionalInformation(objectMap);

            // 接收者
            List<Receiver> receiverList = new ArrayList<>();
            Receiver receiver = new Receiver();
            receiver.setUserId(user.getId());
            // 发送邮件消息时 必填
            receiver.setEmail(user.getEmail());
            // 发送短信消息 必填
            receiver.setPhone(user.getPhone());
            // 必填
            receiver.setTargetUserTenantId(organizationId);
            receiverList.add(receiver);
            messageSender.setReceiverAddressList(receiverList);
            messageClient.sendMessage(messageSender);
        } catch (Exception e) {
            LOGGER.info("Send Reset Organization User Password failed. userId : {}, loginName : {}", user.getId(), user.getLoginName());
        }
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
    public User unlock(Long organizationId, Long userId) {
        userService.unlockUser(userId, organizationId);
        return query(organizationId, userId);
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_ENABLE, description = "iam启用用户", inputSchemaClass = UserEventPayload.class)
    public User enableUser(Long organizationId, Long userId) {
        userService.unfrozenUser(userId, organizationId);
        User user = query(organizationId, userId);
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
        return user;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_DISABLE, description = "iam停用用户", inputSchemaClass = UserEventPayload.class)
    public User disableUser(Long organizationId, Long userId) {
        userService.frozenUser(userId, organizationId);
        User user = query(organizationId, userId);
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
        // 发送停用用户json
        messageSendService.sendDisableUserMsg(user, organizationId);
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
            if (userEnabled) {
                generateUserEventPayload(payloads, userDTO);
            }
            if (errorUserFlag) {
                users.add(user);
            }
            errorUserFlag = true;
        }

        // 发通知不影响业务逻辑
        ExceptionUtil.doWithTryCatchAndLog(
                LOGGER,
                () -> {
                    //导入成功过后，通知成员
                    users.forEach(e -> {
                        Map<String, String> params = new HashMap<>();
                        Tenant organizationDTO = tenantRepository.selectByPrimaryKey(e.getOrganizationId());
                        params.put("organizationName", organizationDTO.getTenantName());
                        params.put("roleName", e.getRoles().stream().map(Role::getName).collect(Collectors.joining(",")));
                        params.put("userList", JSON.toJSONString(insertUsers));
                        params.put("organizationId", String.valueOf(organizationDTO.getTenantId()));
                        params.put("addCount", String.valueOf(insertUsers.size()));
                        userC7nService.sendNotice(Collections.singletonList(e.getId()), BUSINESS_TYPE_CODE, params, e.getOrganizationId(), ResourceLevel.ORGANIZATION);
                    });
                },
                ex -> LOGGER.info("Failed to send notices after batchCreateUsersOnExcel due to the ex", ex)
        );

        sendBatchUserCreateEvent(payloads, insertUsers.get(0).getOrganizationId());
        LOGGER.info("Batch insert {} users into tenant with id {} processed...", insertUsers.size(), organizationId);
        return errorUsers;
    }

    @Override
    public void sendCreateUserAndUpdateRoleSaga(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
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

    public static List<MemberRole> role2MemberRole(Long organizationId, Long userId, List<Role> roles) {
        return role2MemberRole(organizationId, userId, roles, true);
    }

    public static List<MemberRole> role2MemberRole(Long organizationId, Long userId, List<Role> roles, Boolean addParams) {
        Map<String, Object> additionalParams = new HashMap<>();
        if (addParams == null || addParams) {
            additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
        }
        return roles.stream().map(role -> {
                    MemberRole memberRole = new MemberRole();
                    memberRole.setAssignLevel(ResourceLevel.ORGANIZATION.value());
                    memberRole.setAssignLevelValue(organizationId);
                    memberRole.setSourceType(ResourceLevel.ORGANIZATION.value());
                    memberRole.setSourceId(organizationId);
                    memberRole.setRoleId(role.getId());
                    memberRole.setMemberId(userId);
                    memberRole.setMemberType(HiamMemberType.USER.value());
                    memberRole.setAdditionalParams(additionalParams);
                    return memberRole;
                }
        ).collect(Collectors.toList());
    }
}
