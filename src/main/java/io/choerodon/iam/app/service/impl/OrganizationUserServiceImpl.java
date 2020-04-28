package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.User.*;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.iam.app.service.RoleService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
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
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.infra.annotation.OperateLog;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.mapper.C7nLabelMapper;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.payload.UserEventPayload;
import io.choerodon.iam.infra.utils.IamPageUtils;
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
    @Value("${choerodon.site.default.password:abcd1234}")
    private String siteDefaultPassword;

    @Value("${choerodon.organization.resourceLimit.userMaxNumber:100}")
    private Integer userMaxNumber;

    private final ObjectMapper mapper = new ObjectMapper();

    private OrganizationAssertHelper organizationAssertHelper;

    private UserAssertHelper userAssertHelper;

    private UserService userService;

    private TransactionalProducer producer;

    private RoleService roleService;

    private C7nLabelMapper c7nLabelMapper;

    private UserC7nMapper userC7nMapper;

    private UserRepository userRepository;

    public OrganizationUserServiceImpl(OrganizationAssertHelper organizationAssertHelper,
                                       UserAssertHelper userAssertHelper,
                                       UserService userService,
                                       TransactionalProducer producer,
                                       UserC7nMapper userC7nMapper,
                                       UserRepository userRepository,
                                       C7nLabelMapper c7nLabelMapper,
                                       RoleService roleService) {
        this.organizationAssertHelper = organizationAssertHelper;
        this.userAssertHelper = userAssertHelper;
        this.userService = userService;
        this.producer = producer;
        this.userC7nMapper = userC7nMapper;
        this.userRepository = userRepository;
        this.c7nLabelMapper = c7nLabelMapper;
        this.roleService = roleService;
    }

    @Override
    public Page<User> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, PageRequest pageable, String loginName, String realName,
                                                                   String roleName, Boolean enabled, Boolean locked, String params) {
        int page = pageable.getPage();
        int size = pageable.getSize();
        boolean doPage = (size != 0);
        Page<User> result = IamPageUtils.createEmptyPage(page, size);
        if (doPage) {
            int start = IamPageUtils.getBegin(page, size);
            int count = userC7nMapper.selectCountUsersOnOrganizationLevel(ResourceLevel.ORGANIZATION.value(), organizationId,
                    loginName, realName, roleName, enabled, locked, params);
            List<User> users = userC7nMapper.selectUserWithRolesOnOrganizationLevel(start, size, ResourceLevel.ORGANIZATION.value(),
                    organizationId, loginName, realName, roleName, enabled, locked, params);
            result.setTotalElements(count);
            result.addAll(users);
        } else {
            List<User> users = userC7nMapper.selectUserWithRolesOnOrganizationLevel(null, null, ResourceLevel.ORGANIZATION.value(),
                    organizationId, loginName, realName, roleName, enabled, locked, params);
            result.setTotalElements(users.size());
            result.addAll(users);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    @OperateLog(type = "createUserOrg", content = "%s创建用户%s", level = {ResourceLevel.ORGANIZATION})
    public User createUserWithRoles(User user) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        List<Role> userRoles = user.getRoles();
        User result = userService.createUser(user);
        if (devopsMessage) {
            sendUserCreationSaga(userId, user, userRoles, ResourceLevel.ORGANIZATION.value(), user.getOrganizationId());
        }
        return result;
    }

    @Override
    public void sendUserCreationSaga(Long fromUserId, User userDTO, List<Role> userRoles, String value, Long organizationId) {
        producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(ORG_USER_CREAT),
                builder -> {
                    UserEventPayload userEventPayload = getUserEventPayload(userDTO);
                    CreateAndUpdateUserEventPayload createAndUpdateUserEventPayload = new CreateAndUpdateUserEventPayload();
                    createAndUpdateUserEventPayload.setUserEventPayload(userEventPayload);
                    List<UserMemberEventPayload> userMemberEventPayloads = getListUserMemberEventPayload(fromUserId, userDTO, userRoles, value, organizationId);
                    createAndUpdateUserEventPayload.setUserMemberEventPayloads(userMemberEventPayloads);
                    builder
                            .withPayloadAndSerialize(createAndUpdateUserEventPayload)
                            .withRefId(createAndUpdateUserEventPayload.getUserEventPayload().getId())
                            .withSourceId(organizationId);
                    return userDTO;
                });
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
                    userMemberEventMsg.setRoleLabels(c7nLabelMapper.selectLabelNamesInRoleIds(ownRoleIds));
                }
                userMemberEventPayloads.add(userMemberEventMsg);
            }
        }
        return userMemberEventPayloads;
    }


    @Override
    @Transactional(rollbackFor = CommonException.class)
    @Saga(code = USER_CREATE_BATCH, description = "iam批量创建用户", inputSchemaClass = List.class)
    public List<LdapErrorUser> batchCreateUsers(List<User> insertUsers) {
        List<LdapErrorUser> errorUsers = new ArrayList<>();
        List<UserEventPayload> payloads = new ArrayList<>();
        insertUsers.forEach(user -> {
            User userDTO = null;
            try {
                userDTO = insertSelective(user);
            } catch (Exception e) {
                LdapErrorUser errorUser = new LdapErrorUser();
                errorUser.setUuid(user.getUuid());
                errorUser.setLoginName(user.getLoginName());
                errorUser.setEmail(user.getEmail());
                errorUser.setRealName(user.getRealName());
                errorUser.setPhone(user.getPhone());
                // TODO 设置原因
//                errorUser.setCause(LdapErrorUserVOCause.USER_INSERT_ERROR.value());
                errorUsers.add(errorUser);
            }
            boolean userEnabled = userDTO != null && userDTO.getEnabled();
            if (devopsMessage && userEnabled) {
                generateUserEventPayload(payloads, userDTO);
            }
        });
        sendBatchUserCreateEvent(payloads, insertUsers.get(0).getOrganizationId());
        return errorUsers;
    }

    private User insertSelective(User user) {
        if (userRepository.insertSelective(user) != 1) {
            throw new InsertException("error.user.create");
        }
        return userRepository.selectByPrimaryKey(user.getId());
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
    public User updateUser(User user) {
        User result = userService.updateUser(user);
        if (devopsMessage) {
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
        return result;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "resetUserPassword", content = "%s重置%s的登录密码", level = {ResourceLevel.ORGANIZATION})
    public User resetUserPassword(Long organizationId, Long userId) {
        userService.resetUserPassword(userId, organizationId);
        organizationAssertHelper.notExisted(organizationId);
        User user = userAssertHelper.userNotExisted(userId);
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }

        // TODO 密码纪录
        // passwordRecord.updatePassword(user.getId(), user.getPassword());

        // delete access tokens, refresh tokens and sessions of the user after resetting his password
        // TODO 删除token
        // oauthTokenFeignClient.deleteTokens(user.getLoginName());

        // send siteMsg
        // TODO 发送站内信
//        Map<String, Object> paramsMap = new HashMap<>();
//        paramsMap.put("userName", user.getRealName());
//        paramsMap.put("defaultPassword", defaultPassword);
//        List<Long> userIds = Collections.singletonList(userId);
//        userService.sendNotice(userId, userIds, "resetOrganizationUserPassword", paramsMap, organizationId);

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
    public List<ErrorUserVO> batchCreateUsersOnExcel(List<User> insertUsers, Long fromUserId, Long organizationId) {
        List<ErrorUserVO> errorUsers = new ArrayList<>();
        List<UserEventPayload> payloads = new ArrayList<>();
        boolean errorUserFlag = true;
        List<User> users = new ArrayList<>();
        for (User user : insertUsers) {
            User userDTO = null;
            try {
                user.setOrganizationId(organizationId);
                userDTO = ((OrganizationUserServiceImpl) AopContext.currentProxy()).createUserWithRoles(user);
            } catch (Exception e) {
                LOGGER.error("context", e);
                ErrorUserVO errorUser = new ErrorUserVO();
                BeanUtils.copyProperties(user, errorUser);
                if (e instanceof CommonException && ERROR_ORGANIZATION_USER_NUM_MAX.equals(((CommonException) e).getCode())) {
                    errorUser.setCause("组织用户数量已达上限：100，无法创建更多用户");
                } else {
                    errorUser.setCause("用户或角色插入异常");
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
            // TODO 导入成功过后，通知成员
//            Map<String, Object> params = new HashMap<>();
//            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(e.getOrganizationId());
//            params.put("organizationName", organizationDTO.getName());
//            params.put("roleName", e.getRoles().stream().map(v -> v.getName()).collect(Collectors.joining(",")));
//
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("organizationId", organizationDTO.getId());
//            jsonObject.put("addCount", insertUsers.size());
//            List<WebHookJsonSendDTO.User> userList = new ArrayList<>();
//            if (!CollectionUtils.isEmpty(userList)) {
//                for (User userDTO : insertUsers) {
//                    userList.add(userService.getWebHookUser(userDTO.getId()));
//                }
//            }
//
//            jsonObject.put("userList", JSON.toJSONString(userList));
//            WebHookJsonSendDTO webHookJsonSendDTO = new WebHookJsonSendDTO(
//                    SendSettingBaseEnum.ADD_MEMBER.value(),
//                    SendSettingBaseEnum.map.get(SendSettingBaseEnum.ADD_MEMBER.value()),
//                    jsonObject,
//                    new Date(),
//                    userService.getWebHookUser(fromUserId)
//            );
//            userService.sendNotice(fromUserId, Arrays.asList(e.getId()), BUSINESS_TYPE_CODE, params, e.getOrganizationId(), webHookJsonSendDTO);
        });

        sendBatchUserCreateEvent(payloads, insertUsers.get(0).getOrganizationId());
        return errorUsers;
    }

    @Override
    public Boolean checkEnableCreateUser(Long organizationId) {
        // TODO
//        if (organizationService.checkOrganizationIsNew(organizationId)) {
//            int num = organizationService.countUserNum(organizationId);
//            return num < userMaxNumber;
//        }
        return true;
    }
}
