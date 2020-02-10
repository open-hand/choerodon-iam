package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.utils.SagaTopic.User.*;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.payload.CreateAndUpdateUserEventPayload;
import io.choerodon.base.api.dto.payload.UserMemberEventPayload;
import io.choerodon.base.api.validator.RoleValidator;
import io.choerodon.base.infra.asserts.RoleAssertHelper;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.enums.MemberType;
import io.choerodon.base.infra.mapper.LabelMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.ErrorUserDTO;
import io.choerodon.base.api.dto.payload.UserEventPayload;
import io.choerodon.base.api.validator.UserPasswordValidator;
import io.choerodon.base.api.validator.UserValidator;
import io.choerodon.base.api.vo.SysSettingVO;
import io.choerodon.base.app.service.OrganizationUserService;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.app.service.SystemSettingService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.base.infra.asserts.UserAssertHelper;
import io.choerodon.base.infra.enums.LdapErrorUserCause;
import io.choerodon.base.infra.feign.OauthTokenFeignClient;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.base.infra.utils.RandomInfoGenerator;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.oauth.core.password.PasswordPolicyManager;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDTO;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;
import io.choerodon.oauth.core.password.mapper.BasePasswordPolicyMapper;
import io.choerodon.oauth.core.password.record.PasswordRecord;

/**
 * @author superlee
 */
@Component
@RefreshScope
public class OrganizationUserServiceImpl implements OrganizationUserService {
    private static final String BUSINESS_TYPE_CODE = "addMember";
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;
    @Value("${spring.application.name:default}")
    private String serviceName;
    private PasswordRecord passwordRecord;
    private SagaClient sagaClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private PasswordPolicyManager passwordPolicyManager;
    private UserPasswordValidator userPasswordValidator;
    private OauthTokenFeignClient oauthTokenFeignClient;
    private BasePasswordPolicyMapper basePasswordPolicyMapper;
    @Value("${choerodon.site.default.password:abcd1234}")
    private String siteDefaultPassword;
    private SystemSettingService systemSettingService;

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private OrganizationAssertHelper organizationAssertHelper;

    private OrganizationMapper organizationMapper;

    private UserAssertHelper userAssertHelper;

    private UserMapper userMapper;

    private UserService userService;

    private TransactionalProducer producer;

    private RandomInfoGenerator randomInfoGenerator;

    private RoleMemberService roleMemberService;

    private LabelMapper labelMapper;

    private RoleAssertHelper roleAssertHelper;

    public OrganizationUserServiceImpl(PasswordRecord passwordRecord,
                                       PasswordPolicyManager passwordPolicyManager,
                                       BasePasswordPolicyMapper basePasswordPolicyMapper,
                                       OauthTokenFeignClient oauthTokenFeignClient,
                                       UserPasswordValidator userPasswordValidator,
                                       SystemSettingService systemSettingService,
                                       SagaClient sagaClient,
                                       OrganizationAssertHelper organizationAssertHelper,
                                       OrganizationMapper organizationMapper,
                                       UserAssertHelper userAssertHelper,
                                       UserMapper userMapper,
                                       UserService userService,
                                       TransactionalProducer producer,
                                       RandomInfoGenerator randomInfoGenerator,
                                       RoleMemberService roleMemberService,
                                       LabelMapper labelMapper,
                                       RoleAssertHelper roleAssertHelper) {
        this.passwordPolicyManager = passwordPolicyManager;
        this.basePasswordPolicyMapper = basePasswordPolicyMapper;
        this.sagaClient = sagaClient;
        this.userPasswordValidator = userPasswordValidator;
        this.passwordRecord = passwordRecord;
        this.systemSettingService = systemSettingService;
        this.oauthTokenFeignClient = oauthTokenFeignClient;
        this.organizationAssertHelper = organizationAssertHelper;
        this.organizationMapper = organizationMapper;
        this.userAssertHelper = userAssertHelper;
        this.userMapper = userMapper;
        this.userService = userService;
        this.producer = producer;
        this.randomInfoGenerator = randomInfoGenerator;
        this.roleMemberService = roleMemberService;
        this.labelMapper = labelMapper;
        this.roleAssertHelper = roleAssertHelper;
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, Pageable pageable, String loginName, String realName,
                                                                          String roleName, Boolean enabled, Boolean locked, String params) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result = new Page<>(page, size);
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = userMapper.selectCountUsersOnOrganizationLevel(ResourceLevel.ORGANIZATION.value(), organizationId,
                    loginName, realName, roleName, enabled, locked, params);
            List<UserDTO> users = userMapper.selectUserWithRolesOnOrganizationLevel(start, size, ResourceLevel.ORGANIZATION.value(),
                    organizationId, loginName, realName, roleName, enabled, locked, params);
            result.setTotal(count);
            result.addAll(users);
        } else {
            List<UserDTO> users = userMapper.selectUserWithRolesOnOrganizationLevel(null, null, ResourceLevel.ORGANIZATION.value(),
                    organizationId, loginName, realName, roleName, enabled, locked, params);
            result.setTotal(users.size());
            result.addAll(users);
        }
        return result.toPageInfo();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public UserDTO createUserWithRoles(Long organizationId, UserDTO userDTO, boolean checkPassword, boolean checkRoles) {
        UserValidator.validateCreateUserWithRoles(userDTO, checkRoles);
        organizationAssertHelper.notExisted(organizationId);
        userAssertHelper.emailExisted(userDTO.getEmail());
        checkPassword(userDTO, organizationId, checkPassword);
        userDTO.setLoginName(randomInfoGenerator.randomLoginName());
        List<RoleDTO> userRoles = userDTO.getRoles();
        if (devopsMessage) {
            userDTO = createUserAndUpdateRole(userDTO, userRoles, ResourceLevel.ORGANIZATION.value(), organizationId);
        } else {
            userDTO = createUser(userDTO);
        }
        return userDTO;
    }

    public UserDTO createUserAndUpdateRole(UserDTO userDTO, List<RoleDTO> userRoles, String value, Long organizationId) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(ORG_USER_CREAT),
                builder -> {
                    UserDTO user = createUser(userDTO);
                    UserEventPayload userEventPayload = getUserEventPayload(user);
                    CreateAndUpdateUserEventPayload createAndUpdateUserEventPayload = new CreateAndUpdateUserEventPayload();
                    createAndUpdateUserEventPayload.setUserEventPayload(userEventPayload);
                    List<UserMemberEventPayload> userMemberEventPayloads = getListUserMemberEventPayload(userDTO, userRoles, value, organizationId);
                    createAndUpdateUserEventPayload.setUserMemberEventPayloads(userMemberEventPayloads);
                    builder
                            .withPayloadAndSerialize(createAndUpdateUserEventPayload)
                            .withRefId(createAndUpdateUserEventPayload.getUserEventPayload().getId())
                            .withSourceId(organizationId);
                    return user;
                });
    }

    private UserEventPayload getUserEventPayload(UserDTO user) {
        UserEventPayload userEventPayload = new UserEventPayload();
        userEventPayload.setEmail(user.getEmail());
        userEventPayload.setId(user.getId().toString());
        userEventPayload.setName(user.getRealName());
        userEventPayload.setUsername(user.getLoginName());
        userEventPayload.setFromUserId(DetailsHelper.getUserDetails().getUserId());
        userEventPayload.setOrganizationId(user.getOrganizationId());
        return userEventPayload;
    }

    private List<UserMemberEventPayload> getListUserMemberEventPayload(UserDTO userDTO, List<RoleDTO> userRoles, String value, Long organizationId) {
        Long userId = userDTO.getId();
        List<MemberRoleDTO> memberRoleDTOS = new ArrayList<>();
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userRoles)) {
            List<RoleDTO> resultRoles = new ArrayList<>();
            for (RoleDTO roleDTO : userRoles) {
                RoleDTO role = roleAssertHelper.roleNotExisted(roleDTO.getId());
                RoleValidator.validateRole(value, organizationId, role, false);
                resultRoles.add(role);
                MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
                memberRoleDTO.setMemberId(userId);
                memberRoleDTO.setMemberType(ResourceLevel.USER.value());
                memberRoleDTO.setSourceId(organizationId);
                memberRoleDTO.setSourceType(value);
                memberRoleDTO.setRoleId(role.getId());
                memberRoleDTOS.add(memberRoleDTO);
            }
            userDTO.setRoles(resultRoles);
            UserDTO dto = userAssertHelper.userNotExisted(userId);
            List<MemberRoleDTO> returnList = new ArrayList<>();
            if (devopsMessage) {
                UserMemberEventPayload userMemberEventMsg = new UserMemberEventPayload();
                userMemberEventMsg.setResourceId(organizationId);
                userMemberEventMsg.setUserId(userId);
                userMemberEventMsg.setResourceType(value);
                userMemberEventMsg.setUsername(userDTO.getLoginName());
                List<Long> ownRoleIds = roleMemberService.insertOrUpdateRolesByMemberIdExecute(
                        false, organizationId, userId, value, memberRoleDTOS, returnList, MemberType.USER.value());
                if (!ownRoleIds.isEmpty()) {
                    userMemberEventMsg.setRoleLabels(labelMapper.selectLabelNamesInRoleIds(ownRoleIds));
                }
                userMemberEventPayloads.add(userMemberEventMsg);
            } else {
                roleMemberService.insertOrUpdateRolesByMemberIdExecute(false,
                        organizationId,
                        userId,
                        value,
                        memberRoleDTOS,
                        returnList, MemberType.USER.value());
            }
        }
        return userMemberEventPayloads;
    }

    /**
     * 校验用户密码策略(开启时校验).
     *
     * @param userDTO        用户DTO
     * @param organizationId 组织Id
     * @param checkPassword  是否校验
     */
    private void checkPassword(UserDTO userDTO, Long organizationId, boolean checkPassword) {
        String password = userDTO.getPassword();
        if (checkPassword) {
            validatePasswordPolicy(userDTO, password, organizationId);
            userPasswordValidator.validate(password, organizationId, true);
        }
    }

    /**
     * 发送创建用户事件.
     *
     * @param organizationId 组织Id
     * @param userDTO        用户DTO
     * @return 用户DTO
     */
    private UserDTO sendCreateUserEvent(Long organizationId, UserDTO userDTO) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType("user")
                        .withSagaCode(USER_CREATE),
                builder -> {
                    UserDTO user = createUser(userDTO);
                    UserEventPayload userEventPayload = new UserEventPayload();
                    userEventPayload.setEmail(user.getEmail());
                    userEventPayload.setId(user.getId().toString());
                    userEventPayload.setName(user.getRealName());
                    userEventPayload.setUsername(user.getLoginName());
                    userEventPayload.setFromUserId(DetailsHelper.getUserDetails().getUserId());
                    userEventPayload.setOrganizationId(user.getOrganizationId());
                    builder
                            .withPayloadAndSerialize(Collections.singletonList(userEventPayload))
                            .withRefId(userEventPayload.getId())
                            .withSourceId(organizationId);
                    return user;
                });
    }

    /**
     * 创建用户
     *
     * @param userDTO 用户DTO
     * @return 用户DTO
     */
    public UserDTO createUser(UserDTO userDTO) {
        userDTO.setLocked(false);
        userDTO.setEnabled(true);
        userDTO.setPassword(ENCODER.encode(userDTO.getPassword()));
        if (userMapper.insertSelective(userDTO) != 1) {
            throw new InsertException("error.user.create");
        }
        passwordRecord.updatePassword(userDTO.getId(), userDTO.getPassword());
        return userMapper.selectByPrimaryKey(userDTO.getId());
    }

    private UserDTO insertSelective(UserDTO user) {
        if (userMapper.insertSelective(user) != 1) {
            throw new InsertException("error.user.create");
        }
        return userMapper.selectByPrimaryKey(user.getId());
    }

    @Override
    @Transactional(rollbackFor = CommonException.class)
    @Saga(code = USER_CREATE_BATCH, description = "iam批量创建用户", inputSchemaClass = List.class)
    public List<LdapErrorUserDTO> batchCreateUsers(List<UserDTO> insertUsers) {
        List<LdapErrorUserDTO> errorUsers = new ArrayList<>();
        List<UserEventPayload> payloads = new ArrayList<>();
        insertUsers.forEach(user -> {
            UserDTO userDTO = null;
            try {
                userDTO = insertSelective(user);
            } catch (Exception e) {
                LdapErrorUserDTO errorUser = new LdapErrorUserDTO();
                errorUser.setUuid(user.getUuid());
                errorUser.setLoginName(user.getLoginName());
                errorUser.setEmail(user.getEmail());
                errorUser.setRealName(user.getRealName());
                errorUser.setPhone(user.getPhone());
                errorUser.setCause(LdapErrorUserCause.USER_INSERT_ERROR.value());
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

    private void generateUserEventPayload(List<UserEventPayload> payloads, UserDTO userDTO) {
        UserEventPayload payload = new UserEventPayload();
        payload.setEmail(userDTO.getEmail());
        payload.setId(userDTO.getId().toString());
        payload.setName(userDTO.getRealName());
        payload.setUsername(userDTO.getLoginName());
        payload.setOrganizationId(userDTO.getOrganizationId());
        payloads.add(payload);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Saga(code = ORG_USER_CREAT, description = "组织层创建用户", inputSchemaClass = CreateAndUpdateUserEventPayload.class)
    public UserDTO createUserWithRoles(UserDTO insertUser, Long organizationId) {
        List<RoleDTO> roleDTOList = insertUser.getRoles();
        UserDTO resultUser = insertSelective(insertUser);
//       createUserRoles(resultUser, roleDTOList);
        createUserAndUpdateRole(insertUser, roleDTOList, ResourceLevel.ORGANIZATION.value(), organizationId);
        return resultUser;
    }

    private void createUserRoles(UserDTO userDTO, List<RoleDTO> roleDTOList) {
        List<MemberRoleDTO> memberRoleDTOS = new ArrayList<>();
        Long orgId = userDTO.getOrganizationId();
        Long userId = userDTO.getId();
        roleDTOList.forEach(
                roleDTO -> {
                    MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
                    memberRoleDTO.setMemberId(userId);
                    memberRoleDTO.setMemberType(ResourceLevel.USER.value());
                    memberRoleDTO.setSourceId(orgId);
                    memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
                    memberRoleDTO.setRoleId(roleDTO.getId());
                    memberRoleDTOS.add(memberRoleDTO);
                }
        );
        roleMemberService.insertOrUpdateRolesOfUserByMemberId(false, orgId, userId, memberRoleDTOS, ResourceLevel.ORGANIZATION.value());
    }


    private void sendBatchUserCreateEvent(List<UserEventPayload> payloads, Long orgId) {
        if (!payloads.isEmpty()) {
            try {
                String input = mapper.writeValueAsString(payloads);
                String refIds = payloads.stream().map(UserEventPayload::getId).collect(Collectors.joining(","));
                sagaClient.startSaga(USER_CREATE_BATCH, new StartInstanceDTO(input, "users", refIds, ResourceLevel.ORGANIZATION.value(), orgId));
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.batchCreateUser.event", e);
            } finally {
                payloads.clear();
            }
        }
    }

    private void validatePasswordPolicy(UserDTO userDTO, String password, Long organizationId) {
        BaseUserDTO baseUserDTO = new BaseUserDTO();
        BeanUtils.copyProperties(userDTO, baseUserDTO);
        BasePasswordPolicyDTO example = new BasePasswordPolicyDTO();
        example.setOrganizationId(organizationId);
        BasePasswordPolicyDTO basePasswordPolicyDTO = basePasswordPolicyMapper.selectOne(example);
        Optional.ofNullable(basePasswordPolicyDTO)
                .map(passwordPolicy -> {
                    if (StringUtils.isEmpty(passwordPolicy.getRegularExpression())) {
                        passwordPolicy.setRegularExpression(null);
                    }
                    return passwordPolicy;
                })
                .map(passwordPolicy -> {
                    if (!password.equals(passwordPolicy.getOriginalPassword())) {
                        passwordPolicyManager.passwordValidate(password, baseUserDTO, passwordPolicy);
                    }
                    return null;
                });
    }


    @Override
    @Saga(code = USER_UPDATE, description = "iam更新用户", inputSchemaClass = UserEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public UserDTO update(Long organizationId, UserDTO userDTO) {
        List<RoleDTO> userRoles = userDTO.getRoles();
        UserValidator.validateUseRoles(userRoles, true);
        organizationAssertHelper.notExisted(organizationId);
        UserDTO currentUser = userAssertHelper.userNotExisted(userDTO.getId());
        userAssertHelper.notExternalUser(organizationId, currentUser.getOrganizationId());
        userDTO.setOrganizationId(currentUser.getOrganizationId());
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userDTO = updateUser(userDTO);
            userEventPayload.setEmail(userDTO.getEmail());
            userEventPayload.setId(userDTO.getId().toString());
            userEventPayload.setName(userDTO.getRealName());
            userEventPayload.setUsername(userDTO.getLoginName());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                sagaClient.startSaga(USER_UPDATE, new StartInstanceDTO(input, "user", userEventPayload.getId(), ResourceLevel.ORGANIZATION.value(), userDTO.getOrganizationId()));
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.updateUser.event", e);
            }
        } else {
            userDTO = updateUser(userDTO);
        }
        userService.createUserRoles(userDTO, userRoles, ResourceLevel.ORGANIZATION.value(), organizationId, true, true, true);
        return userDTO;
    }

    private UserDTO updateUser(UserDTO userDTO) {
        if (userDTO.getPassword() != null) {
            userDTO.setPassword(ENCODER.encode(userDTO.getPassword()));
        }
        return updateSelective(userDTO);
    }

    private UserDTO updateSelective(UserDTO userDTO) {
        userAssertHelper.objectVersionNumberNotNull(userDTO.getObjectVersionNumber());
        if (userMapper.updateByPrimaryKeySelective(userDTO) != 1) {
            throw new UpdateException("error.user.update");
        }
        return userMapper.selectByPrimaryKey(userDTO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO resetUserPassword(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        UserDTO user = userAssertHelper.userNotExisted(userId);
        userAssertHelper.notExternalUser(organizationId, user.getOrganizationId());
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }

        String defaultPassword = getDefaultPassword(organizationId);
        user.setPassword(ENCODER.encode(defaultPassword));
        updateSelective(user);
        passwordRecord.updatePassword(user.getId(), user.getPassword());

        // delete access tokens, refresh tokens and sessions of the user after resetting his password
        oauthTokenFeignClient.deleteTokens(user.getLoginName());

        // send siteMsg
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userName", user.getRealName());
        paramsMap.put("defaultPassword", defaultPassword);
        List<Long> userIds = Collections.singletonList(userId);
        userService.sendNotice(userId, userIds, "resetOrganizationUserPassword", paramsMap, organizationId);

        return user;
    }

    /**
     * get password to reset
     *
     * @param organizationId organization id
     * @return the password
     */
    private String getDefaultPassword(Long organizationId) {
        BasePasswordPolicyDTO basePasswordPolicyDTO = new BasePasswordPolicyDTO();
        basePasswordPolicyDTO.setOrganizationId(organizationId);
        basePasswordPolicyDTO = basePasswordPolicyMapper.selectOne(basePasswordPolicyDTO);
        if (basePasswordPolicyDTO != null && basePasswordPolicyDTO.getEnablePassword() && !StringUtils.isEmpty(basePasswordPolicyDTO.getOriginalPassword())) {
            return basePasswordPolicyDTO.getOriginalPassword();
        }

        SysSettingVO setting = systemSettingService.getSetting();
        if (setting != null && !StringUtils.isEmpty(setting.getDefaultPassword())) {
            return setting.getDefaultPassword();
        }

        return siteDefaultPassword;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_DELETE, description = "iam删除用户", inputSchemaClass = UserEventPayload.class)
    public void delete(Long organizationId, Long id) {
        organizationAssertHelper.notExisted(organizationId);
        UserDTO user = userAssertHelper.userNotExisted(id);
        UserEventPayload userEventPayload = new UserEventPayload();
        userEventPayload.setUsername(user.getLoginName());
        userMapper.deleteByPrimaryKey(id);
        if (devopsMessage) {
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                sagaClient.startSaga(USER_DELETE, new StartInstanceDTO(input, "user", userEventPayload.getId()));
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.deleteUser.event", e);
            }

        }
    }

    @Override
    public UserDTO query(Long organizationId, Long id) {
        organizationAssertHelper.notExisted(organizationId);
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO unlock(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userAssertHelper.notExternalUser(organizationId, userDTO.getOrganizationId());
        userDTO.setLocked(false);
        passwordRecord.unLockUser(userDTO.getId());
        return updateSelective(userDTO);
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_ENABLE, description = "iam启用用户", inputSchemaClass = UserEventPayload.class)
    public UserDTO enableUser(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        UserDTO user = updateStatus(organizationId, userId, true);
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setUsername(user.getLoginName());
            userEventPayload.setId(userId.toString());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                sagaClient.startSaga(USER_ENABLE, new StartInstanceDTO(input, "user", userEventPayload.getId(), ResourceLevel.ORGANIZATION.value(), organizationId));
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.enableUser.event", e);
            }
        }
        return user;
    }

    private UserDTO updateStatus(Long organizationId, Long userId, boolean enabled) {
        UserDTO dto = userAssertHelper.userNotExisted(userId);
        userAssertHelper.notExternalUser(organizationId, dto.getOrganizationId());
        dto.setEnabled(enabled);
        if (userMapper.updateByPrimaryKeySelective(dto) != 1) {
            throw new UpdateException("error.user.update");
        }
        return dto;
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = USER_DISABLE, description = "iam停用用户", inputSchemaClass = UserEventPayload.class)
    public UserDTO disableUser(Long organizationId, Long userId) {
        organizationAssertHelper.notExisted(organizationId);
        UserDTO user = updateStatus(organizationId, userId, false);
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            userEventPayload.setUsername(user.getLoginName());
            userEventPayload.setId(userId.toString());
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                sagaClient.startSaga(USER_DISABLE, new StartInstanceDTO(input, "user", userEventPayload.getId(), ResourceLevel.ORGANIZATION.value(), organizationId));
            } catch (Exception e) {
                throw new CommonException("error.organizationUserService.disableUser.event", e);
            }
        }
        return user;
    }

    @Override
    public List<Long> listUserIds(Long organizationId) {
        return organizationMapper.listMemberIds(organizationId, "organization");
    }

    @Override
    public List<ErrorUserDTO> batchCreateUsersOnExcel(List<UserDTO> insertUsers, Long fromUserId, Long organizationId) {
        List<ErrorUserDTO> errorUsers = new ArrayList<>();
        List<UserEventPayload> payloads = new ArrayList<>();
        boolean errorUserFlag = true;
        List<UserDTO> userDTOS = new ArrayList<>();
        for (UserDTO user : insertUsers) {
            UserDTO userDTO = null;
            try {
                userDTO = ((OrganizationUserServiceImpl) AopContext.currentProxy()).createUserWithRoles(user, organizationId);
            } catch (Exception e) {
                ErrorUserDTO errorUser = new ErrorUserDTO();
                BeanUtils.copyProperties(user, errorUser);
                errorUser.setCause("用户或角色插入异常");
                errorUsers.add(errorUser);
                errorUserFlag = false;
            }
            boolean userEnabled = userDTO != null && userDTO.getEnabled();
            if (devopsMessage && userEnabled) {
                generateUserEventPayload(payloads, userDTO);
            }
            if (errorUserFlag) {
                userDTOS.add(user);
            }
            errorUserFlag = true;
        }
        //导入成功过后，通知成员
        userDTOS.stream().forEach(e -> {
            Map<String, Object> params = new HashMap<>();
            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(e.getOrganizationId());
            params.put("organizationName", organizationDTO.getName());
            params.put("roleName", e.getRoles().stream().map(v -> v.getName()).collect(Collectors.joining(",")));
            userService.sendNotice(fromUserId, Arrays.asList(e.getId()), BUSINESS_TYPE_CODE, params, e.getOrganizationId());
        });

        sendBatchUserCreateEvent(payloads, insertUsers.get(0).getOrganizationId());
        return errorUsers;
    }
}
