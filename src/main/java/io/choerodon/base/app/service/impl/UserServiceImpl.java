package io.choerodon.base.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.base.api.dto.*;
import io.choerodon.base.api.dto.payload.UserEventPayload;
import io.choerodon.base.api.validator.ResourceLevelValidator;
import io.choerodon.base.api.validator.RoleValidator;
import io.choerodon.base.api.validator.UserPasswordValidator;
import io.choerodon.base.api.validator.UserValidator;
import io.choerodon.base.api.vo.AssignAdminVO;
import io.choerodon.base.api.vo.DeleteAdminVO;
import io.choerodon.base.api.vo.UserNumberVO;
import io.choerodon.base.api.vo.UserVO;
import io.choerodon.base.app.service.RoleMemberService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.asserts.*;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.enums.MemberType;
import io.choerodon.base.infra.enums.RoleEnum;
import io.choerodon.base.infra.feign.FileFeignClient;
import io.choerodon.base.infra.feign.NotifyFeignClient;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.base.infra.utils.ImageUtils;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.base.infra.utils.ParamUtils;
import io.choerodon.base.infra.utils.SagaTopic;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.notify.NoticeSendDTO;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.oauth.core.password.PasswordPolicyManager;
import io.choerodon.oauth.core.password.domain.BasePasswordPolicyDTO;
import io.choerodon.oauth.core.password.domain.BaseUserDTO;
import io.choerodon.oauth.core.password.mapper.BasePasswordPolicyMapper;
import io.choerodon.oauth.core.password.record.PasswordRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static io.choerodon.base.infra.asserts.UserAssertHelper.WhichColumn;
import static io.choerodon.base.infra.utils.SagaTopic.User.*;

/**
 * @author superlee
 */
@Component
@RefreshScope
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    private static final String USER_NOT_LOGIN_EXCEPTION = "error.user.not.login";
    private static final String USER_NOT_FOUND_EXCEPTION = "error.user.not.found";
    private static final String USER_ID_NOT_EQUAL_EXCEPTION = "error.user.id.not.equals";
    private static final String ROOT_BUSINESS_TYPE_CODE = "siteAddRoot";
    private static final String SITE_ROOT = "role/site/default/administrator";
    @Value("${choerodon.category.enabled:false}")
    private boolean enableCategory;
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;
    @Value("${spring.application.name:default}")
    private String serviceName;
    private PasswordRecord passwordRecord;
    private FileFeignClient fileFeignClient;
    private BasePasswordPolicyMapper basePasswordPolicyMapper;
    private PasswordPolicyManager passwordPolicyManager;
    private UserPasswordValidator userPasswordValidator;
    private SagaClient sagaClient;
    private MemberRoleMapper memberRoleMapper;
    private final ObjectMapper mapper = new ObjectMapper();
    private NotifyFeignClient notifyFeignClient;
    private UserMapper userMapper;
    private RouteMemberRuleMapper routeMemberRuleMapper;

    private UserAssertHelper userAssertHelper;

    private OrganizationAssertHelper organizationAssertHelper;

    private ProjectMapper projectMapper;

    private OrganizationMapper organizationMapper;

    private ProjectAssertHelper projectAssertHelper;

    private RoleAssertHelper roleAssertHelper;

    private RoleMemberService roleMemberService;

    private TransactionalProducer producer;

    private RoleMapper roleMapper;

    public UserServiceImpl(PasswordRecord passwordRecord,
                           FileFeignClient fileFeignClient,
                           SagaClient sagaClient,
                           BasePasswordPolicyMapper basePasswordPolicyMapper,
                           UserPasswordValidator userPasswordValidator,
                           PasswordPolicyManager passwordPolicyManager,
                           MemberRoleMapper memberRoleMapper,
                           NotifyFeignClient notifyFeignClient,
                           UserMapper userMapper,
                           UserAssertHelper userAssertHelper,
                           OrganizationAssertHelper organizationAssertHelper,
                           ProjectMapper projectMapper,
                           OrganizationMapper organizationMapper,
                           ProjectAssertHelper projectAssertHelper,
                           RoleAssertHelper roleAssertHelper,
                           RoleMemberService roleMemberService,
                           TransactionalProducer producer,
                           RouteMemberRuleMapper routeMemberRuleMapper,
                           RoleMapper roleMapper) {
        this.passwordRecord = passwordRecord;
        this.fileFeignClient = fileFeignClient;
        this.sagaClient = sagaClient;
        this.basePasswordPolicyMapper = basePasswordPolicyMapper;
        this.passwordPolicyManager = passwordPolicyManager;
        this.userPasswordValidator = userPasswordValidator;
        this.memberRoleMapper = memberRoleMapper;
        this.notifyFeignClient = notifyFeignClient;
        this.userMapper = userMapper;
        this.userAssertHelper = userAssertHelper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectMapper = projectMapper;
        this.organizationMapper = organizationMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.roleAssertHelper = roleAssertHelper;
        this.roleMemberService = roleMemberService;
        this.producer = producer;
        this.routeMemberRuleMapper = routeMemberRuleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDTO querySelf() {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        Long userId = customUserDetails.getUserId();
        UserDTO userDTO = userMapper.selectByPrimaryKey(userId);
        if (userDTO != null) {
            OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(userDTO.getOrganizationId());
            userDTO.setOrganizationName(organizationDTO.getName());
            userDTO.setOrganizationCode(organizationDTO.getCode());
            if (userDTO.getPhone() == null || userDTO.getPhone().isEmpty()) {
                userDTO.setInternationalTelCode("");
            }
        }
        return userDTO;
    }

    @Override
    public Set<OrganizationDTO> queryOrganizations(Long userId, Boolean includedDisabled) {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        if (!userId.equals(customUserDetails.getUserId())) {
            throw new CommonException(USER_ID_NOT_EQUAL_EXCEPTION);
        }
        boolean isAdmin = false;
        if (customUserDetails.getAdmin() != null) {
            isAdmin = customUserDetails.getAdmin();
        }
        return getOwnedOrganizations(userId, includedDisabled, isAdmin);
    }

    private CustomUserDetails checkLoginUser(Long id) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            throw new CommonException(USER_NOT_LOGIN_EXCEPTION);
        }
        if (!id.equals(customUserDetails.getUserId())) {
            throw new CommonException(USER_ID_NOT_EQUAL_EXCEPTION);
        }
        return customUserDetails;
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersWithRoles(Pageable pageable, RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                                       Long sourceId, ResourceType resourceType) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result = new Page<>(page, size);
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = userMapper.selectCountUsers(roleAssignmentSearchDTO, sourceId, resourceType.value(),
                    ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam()));
            List<UserDTO> users =
                    userMapper.selectUserWithRolesByOption(
                            roleAssignmentSearchDTO, sourceId, resourceType.value(), start, size,
                            ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam()));
            result.setTotal(count);
            result.addAll(users);
        } else {
            List<UserDTO> users =
                    userMapper.selectUserWithRolesByOption(roleAssignmentSearchDTO, sourceId, resourceType.value(), null, null,
                            ParamUtils.arrToStr(roleAssignmentSearchDTO.getParam()));
            result.setTotal(users.size());
            result.addAll(users);
        }
        return result.toPageInfo();
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersByRoleIdOnSiteLevel(Pageable pageable, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, boolean doPage) {
        return pagingQueryUsersByRoleIdAndLevel(pageable, roleAssignmentSearchDTO, roleId, 0L, ResourceLevel.SITE.value(), doPage);
    }

    private PageInfo<UserDTO> pagingQueryUsersByRoleIdAndLevel(Pageable pageable, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long sourceId, String level, boolean doPage) {
        String param = Optional.ofNullable(roleAssignmentSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        if (!doPage) {
            Page<UserDTO> result = new Page<>();
            result.addAll(userMapper.selectUsersFromMemberRoleByOptions(roleId, "user", sourceId, level, roleAssignmentSearchDTO, param));
            result.setTotal(result.size());
            return result.toPageInfo();
        }
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() -> userMapper.selectUsersFromMemberRoleByOptions(roleId, "user", sourceId,
                level, roleAssignmentSearchDTO, param));
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersByRoleIdOnOrganizationLevel(Pageable pageable, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long sourceId, boolean doPage) {
        return pagingQueryUsersByRoleIdAndLevel(pageable, roleAssignmentSearchDTO, roleId, sourceId, ResourceLevel.ORGANIZATION.value(), doPage);
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(Pageable pageable, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long sourceId, boolean doPage) {
        return pagingQueryUsersByRoleIdAndLevel(pageable, roleAssignmentSearchDTO, roleId, sourceId, ResourceLevel.PROJECT.value(), doPage);
    }

    @Override
    public List<UserVO> listUsersWithGitlabLabel(Long projectId, String labelName, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        String param = Optional.ofNullable(roleAssignmentSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        return userMapper.listUsersWithGitlabLabel(projectId, labelName, roleAssignmentSearchDTO, param)
                .stream().map(t -> {
                    UserVO userVO = new UserVO();
                    BeanUtils.copyProperties(t, userVO);
                    return userVO;
                }).collect(Collectors.toList());
    }

    @Override
    public String uploadPhoto(Long id, MultipartFile file) {
        checkLoginUser(id);
        return fileFeignClient.uploadFile("iam-service", file.getOriginalFilename(), file).getBody();
    }

    @Override
    public String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        checkLoginUser(id);
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return fileFeignClient.uploadFile("iam-service", file.getOriginalFilename(), file).getBody();
        } catch (Exception e) {
            LOGGER.warn("error happened when save photo {}", e.getMessage());
            throw new CommonException("error.user.photo.save");
        }
    }

    private Set<OrganizationDTO> getOwnedOrganizations(Long userId, Boolean includedDisabled, boolean isAdmin) {
        Set<OrganizationDTO> resultOrganizations = organizationMapper.selectFromMemberRoleByMemberId(userId, includedDisabled);
        Set<OrganizationDTO> notIntoOrganizations = organizationMapper.selectOrgByUserAndPros(userId, includedDisabled);
        OrganizationDTO ownOrg = organizationMapper.selectOwnOrgByUserId(userId);
        if (ownOrg != null) {
            notIntoOrganizations.add(ownOrg);
        }
        if (!isAdmin) {
            notIntoOrganizations.forEach(i -> i.setInto(false));
        }
        resultOrganizations.addAll(notIntoOrganizations);
        return resultOrganizations;
    }

    @Override
    public void selfUpdatePassword(Long userId, UserPasswordDTO userPasswordDTO, Boolean checkPassword, Boolean checkLogin) {
        if (checkLogin) {
            checkLoginUser(userId);
        }
        UserDTO user = userAssertHelper.userNotExisted(userId);
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }
        if (!ENCODER.matches(userPasswordDTO.getOriginalPassword(), user.getPassword())) {
            throw new CommonException("error.password.originalPassword");
        }
        //密码策略
        if (checkPassword) {
            BaseUserDTO baseUserDTO = new BaseUserDTO();
            BeanUtils.copyProperties(user, baseUserDTO);
            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(user.getOrganizationId());
            if (organizationDTO != null) {
                BasePasswordPolicyDTO example = new BasePasswordPolicyDTO();
                example.setOrganizationId(organizationDTO.getId());
                BasePasswordPolicyDTO basePasswordPolicyDO = basePasswordPolicyMapper.selectOne(example);
                if (userPasswordDTO.getPassword() != null) {
                    passwordPolicyManager.passwordValidate(userPasswordDTO.getPassword(), baseUserDTO, basePasswordPolicyDO);
                }
                // 校验用户密码
                userPasswordValidator.validate(userPasswordDTO.getPassword(), organizationDTO.getId(), true);
            }
        }
        user.setPassword(ENCODER.encode(userPasswordDTO.getPassword()));
        updateSelective(user);
        passwordRecord.updatePassword(user.getId(), user.getPassword());

        // send siteMsg
        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("userName", user.getRealName());
        List<Long> userIds = new ArrayList<>();
        userIds.add(user.getId());
        sendNotice(user.getId(), userIds, "modifyPassword", paramsMap, 0L);
    }

    private UserDTO updateSelective(UserDTO userDTO) {
        userAssertHelper.objectVersionNumberNotNull(userDTO.getObjectVersionNumber());
        if (userMapper.updateByPrimaryKeySelective(userDTO) != 1) {
            throw new UpdateException("error.user.update");
        }
        return userMapper.selectByPrimaryKey(userDTO);
    }

    @Override
    public UserDTO queryInfo(Long userId) {
        checkLoginUser(userId);
        UserDTO user = userAssertHelper.userNotExisted(userId);
        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(user.getOrganizationId());
        user.setOrganizationName(organizationDTO.getName());
        user.setOrganizationCode(organizationDTO.getCode());
        return user;
    }

    @Override
    public RegistrantInfoDTO queryRegistrantInfoAndAdmin(String orgCode) {
        OrganizationDTO organizationDTO = organizationAssertHelper.organizationNotExisted(OrganizationAssertHelper.WhichColumn.CODE, orgCode);
        Long userId = organizationDTO.getUserId();
        UserDTO user = userAssertHelper.userNotExisted(userId);
        UserDTO admin = userAssertHelper.userNotExisted(WhichColumn.LOGIN_NAME, "admin");
        RegistrantInfoDTO registrantInfoDTO = new RegistrantInfoDTO();
        registrantInfoDTO.setUser(user);
        registrantInfoDTO.setOrganizationName(organizationDTO.getName());
        registrantInfoDTO.setAdminId(admin.getId());
        return registrantInfoDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateInfo(UserDTO userDTO, Boolean checkLogin) {
        if (checkLogin) {
            checkLoginUser(userDTO.getId());
        }
        UserDTO dto;
        if (devopsMessage) {
            UserEventPayload userEventPayload = new UserEventPayload();
            dto = updateSelective(userDTO);
            userEventPayload.setEmail(dto.getEmail());
            userEventPayload.setId(dto.getId().toString());
            userEventPayload.setName(dto.getRealName());
            userEventPayload.setUsername(dto.getLoginName());
            BeanUtils.copyProperties(dto, dto);
            try {
                String input = mapper.writeValueAsString(userEventPayload);
                sagaClient.startSaga(USER_UPDATE, new StartInstanceDTO(input, "user", "" + dto.getId()));
            } catch (Exception e) {
                throw new CommonException("error.UserService.updateInfo.event", e);
            }
        } else {
            dto = updateSelective(userDTO);
        }
        OrganizationDTO organizationDTO = organizationAssertHelper.notExisted(dto.getOrganizationId());
        dto.setOrganizationName(organizationDTO.getName());
        dto.setOrganizationCode(organizationDTO.getCode());
        return dto;
    }

    @Override
    public void check(UserDTO user) {
        boolean checkEmail = !StringUtils.isEmpty(user.getEmail());
        boolean checkPhone = !StringUtils.isEmpty(user.getPhone());

        if (!checkEmail && !checkPhone) {
            throw new CommonException("error.user.validation.fields.empty");
        }
        if (checkEmail) {
            checkEmail(user);
        }
        if (checkPhone) {
            checkPhone(user);
        }
    }

    /**
     * 校验在启用用户中手机号唯一
     *
     * @param user 用户信息
     */
    private void checkPhone(UserDTO user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String phone = user.getPhone();
        UserDTO userDTO = new UserDTO();
        userDTO.setPhone(phone);
        userDTO.setEnabled(true);
        if (createCheck) {
            List<UserDTO> select = userMapper.select(userDTO);
            boolean existed = select != null && select.size() != 0;
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        } else {
            Long id = user.getId();
            UserDTO dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        }
    }

    private void checkEmail(UserDTO user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String email = user.getEmail();
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        if (createCheck) {
            boolean existed = userMapper.selectOne(userDTO) != null;
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        } else {
            Long id = user.getId();
            UserDTO dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        }
    }

    @Override
    public UserDTO queryByLoginName(String loginName) {
        UserDTO dto = new UserDTO();
        dto.setLoginName(loginName);
        return userMapper.selectOne(dto);
    }

    @Override
    public UserDTO lockUser(Long userId, Integer lockExpireTime) {
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userDTO.setLocked(true);
        userDTO.setLockedUntilAt(new Date(System.currentTimeMillis() + lockExpireTime * 1000));
        return updateSelective(userDTO);
    }

    @Override
    public PageInfo<UserDTO> pagingQueryAdminUsers(Pageable pageable, String loginName, String realName, String params) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> userMapper.selectAdminUserPage(loginName, realName, params));
    }

    @Saga(code = SagaTopic.User.ASSIGN_ADMIN, description = "分配Root权限同步事件", inputSchemaClass = AssignAdminVO.class)
    @Override
    @Transactional
    @OperateLog(type = "addAdminUsers", content = "用户%s已被%s分配平台Root权限", level = {ResourceType.SITE})
    public void addAdminUsers(long[] ids) {
        List<Long> adminUserIds = new ArrayList<>();
        for (long id : ids) {
            UserDTO dto = userMapper.selectByPrimaryKey(id);
            if (dto != null && !dto.getAdmin()) {
                dto.setAdmin(true);
                adminUserIds.add(id);
                updateSelective(dto);
            }
        }
        //添加成功后发送站内信和邮件通知被添加者
        Long fromUserId = DetailsHelper.getUserDetails().getUserId();
        if (!adminUserIds.isEmpty()) {
            ((UserServiceImpl) AopContext.currentProxy()).sendNotice(fromUserId, adminUserIds, ROOT_BUSINESS_TYPE_CODE, Collections.EMPTY_MAP, 0L);
        }
        if (!adminUserIds.isEmpty()) {
            AssignAdminVO assignAdminVO = new AssignAdminVO(adminUserIds);
            producer.apply(StartSagaBuilder.newBuilder()
                    .withRefId(adminUserIds.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .withRefType("user")
                    .withSourceId(0L)
                    .withLevel(ResourceLevel.SITE)
                    .withSagaCode(SagaTopic.User.ASSIGN_ADMIN)
                    .withPayloadAndSerialize(assignAdminVO), builder -> {
            });
        }
    }

    @Saga(code = SagaTopic.User.DELETE_ADMIN, description = "用户Root权限被删除事件同步", inputSchemaClass = DeleteAdminVO.class)
    @Override
    public void deleteAdminUser(long id) {
        UserDTO dto = userAssertHelper.userNotExisted(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setAdmin(true);
        if (userMapper.selectCount(userDTO) > 1) {
            if (dto.getAdmin()) {
                dto.setAdmin(false);
                //删除member-role的关系
                RoleDTO roleDTO = new RoleDTO();
                roleDTO.setCode(SITE_ROOT);
                roleMapper.selectOne(roleDTO);
                MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
                memberRoleDTO.setRoleId(roleDTO.getId());
                memberRoleDTO.setMemberId(id);
                memberRoleMapper.delete(memberRoleDTO);
                producer.apply(StartSagaBuilder.newBuilder()
                                .withRefId(String.valueOf(id))
                                .withRefType("user")
                                .withSourceId(0L)
                                .withLevel(ResourceLevel.SITE)
                                .withSagaCode(SagaTopic.User.DELETE_ADMIN)
                                .withPayloadAndSerialize(new DeleteAdminVO(id)),
                        builder -> updateSelective(dto));
            }
        } else {
            throw new CommonException("error.user.admin.size");
        }
    }

    @Override
    public List<UserDTO> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(ids)) {
            return new ArrayList<>();
        } else {
            return userMapper.listUsersByIds(ids, onlyEnabled);
        }
    }

    @Override
    public List<UserDTO> listUsersByEmails(String[] emails) {
        if (ObjectUtils.isEmpty(emails)) {
            return new ArrayList<>();
        } else {
            return userMapper.listUsersByEmails(emails);
        }
    }

    @Override
    public List<UserDTO> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(loginNames)) {
            return new ArrayList<>();
        } else {
            return userMapper.listUsersByLoginNames(loginNames, onlyEnabled);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoDTO updateUserInfo(Long id, UserInfoDTO userInfoDTO) {
        // 更新用户密码
        UserPasswordDTO passwordDTO = new UserPasswordDTO();
        passwordDTO.setOriginalPassword(userInfoDTO.getOriginalPassword());
        passwordDTO.setPassword(userInfoDTO.getPassword());
        selfUpdatePassword(id, passwordDTO, true, false);
        // 更新用户名
        String userName = userInfoDTO.getUserName();
        if (!StringUtils.isEmpty(userName)) {
            UserDTO user = userMapper.selectByPrimaryKey(id);
            user.setRealName(userName);
            updateInfo(user, false);
        }
        return userInfoDTO;
    }

    @Override
    public UserDTO updateUserRoles(Long userId, String sourceType, Long sourceId, List<RoleDTO> roleDTOList) {
        return updateUserRoles(userId, sourceType, sourceId, roleDTOList, false);
    }

    @Override
    public UserDTO updateUserRoles(Long userId, String sourceType, Long sourceId, List<RoleDTO> roleDTOList, Boolean syncAll) {
        UserValidator.validateUseRoles(roleDTOList, true);
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        validateSourceNotExisted(sourceType, sourceId);
        createUserRoles(userDTO, roleDTOList, sourceType, sourceId, true, true, true, syncAll);
        return userDTO;
    }

    @Override
    public List<MemberRoleDTO> createUserRoles(UserDTO userDTO, List<RoleDTO> roleDTOList, String sourceType, Long sourceId, boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable) {
        return createUserRoles(userDTO, roleDTOList, sourceType, sourceId, isEdit, allowRoleEmpty, false);
    }


    @Override
    public List<MemberRoleDTO> createUserRoles(UserDTO userDTO, List<RoleDTO> roleDTOList, String sourceType, Long sourceId, boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable, Boolean syncAll) {
        Long userId = userDTO.getId();
        List<MemberRoleDTO> memberRoleDTOS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleDTOList)) {
            List<RoleDTO> resultRoles = new ArrayList<>();
            for (RoleDTO roleDTO : roleDTOList) {
                RoleDTO role = roleAssertHelper.roleNotExisted(roleDTO.getId());
                RoleValidator.validateRole(sourceType, sourceId, role, allowRoleDisable);
                resultRoles.add(role);
                MemberRoleDTO memberRoleDTO = new MemberRoleDTO();
                memberRoleDTO.setMemberId(userId);
                memberRoleDTO.setMemberType(ResourceLevel.USER.value());
                memberRoleDTO.setSourceId(sourceId);
                memberRoleDTO.setSourceType(sourceType);
                memberRoleDTO.setRoleId(role.getId());
                memberRoleDTOS.add(memberRoleDTO);
            }
            userDTO.setRoles(resultRoles);
            memberRoleDTOS = roleMemberService.insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, userId, memberRoleDTOS, sourceType, syncAll);
        } else {
            // 如果允许用户角色为空 则清空当前用户角色
            if (allowRoleEmpty) {
                memberRoleDTOS = roleMemberService.insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, userId, new ArrayList<>(), sourceType, syncAll);
            }
        }
        return memberRoleDTOS;
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersWithRolesOnSiteLevel(Pageable pageable, String orgName, String loginName, String realName,
                                                                  String roleName, Boolean enabled, Boolean locked, String params) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result = new Page<>(page, size);
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = userMapper.selectCountUsersOnSiteLevel(ResourceLevel.SITE.value(), 0L, orgName, loginName, realName,
                    roleName, enabled, locked, params);
            List<UserDTO> users = userMapper.selectUserWithRolesOnSiteLevel(start, size, ResourceLevel.SITE.value(), 0L, orgName,
                    loginName, realName, roleName, enabled, locked, params);
            result.setTotal(count);
            result.addAll(users);
        } else {
            List<UserDTO> users = userMapper.selectUserWithRolesOnSiteLevel(null, null, ResourceLevel.SITE.value(), 0L, orgName,
                    loginName, realName, roleName, enabled, locked, params);
            result.setTotal(users.size());
            result.addAll(users);
        }
        return result.toPageInfo();
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, Pageable pageable, String loginName, String realName,
                                                                     String roleName, Boolean enabled, String params) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        boolean doPage = (size != 0);
        Page<UserDTO> result = new Page<>(page, size);
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = userMapper.selectCountUsersOnProjectLevel(ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            List<UserDTO> users = userMapper.selectUserWithRolesOnProjectLevel(
                    start, size, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result.setTotal(count);
            result.addAll(users);
        } else {
            List<UserDTO> users = userMapper.selectUserWithRolesOnProjectLevel(
                    null, null, ResourceLevel.PROJECT.value(), projectId, loginName, realName, roleName, enabled, params);
            result.setTotal(users.size());
            result.addAll(users);
        }
        return result.toPageInfo();
    }

    @Override
    public List<MemberRoleDTO> assignUsersRoles(String sourceType, Long sourceId, List<MemberRoleDTO> memberRoleDTOList) {
        return assignUsersRoles(sourceType, sourceId, memberRoleDTOList, false);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperateLog(type = "assignUsersRoles", content = "用户%s被%s分配【%s】角色", level = {ResourceType.SITE, ResourceType.ORGANIZATION})
    public List<MemberRoleDTO> assignUsersRoles(String sourceType, Long sourceId, List<MemberRoleDTO> memberRoleDTOList, Boolean syncAll) {
        validateSourceNotExisted(sourceType, sourceId);
        memberRoleDTOList.forEach(memberRoleDTO -> {
            if (memberRoleDTO.getRoleId() == null || memberRoleDTO.getMemberId() == null) {
                throw new EmptyParamException("error.memberRole.insert.empty");
            }
            memberRoleDTO.setMemberType(MemberType.USER.value());
            memberRoleDTO.setSourceType(sourceType);
            memberRoleDTO.setSourceId(sourceId);
        });
        Map<Long, List<MemberRoleDTO>> memberRolesMap = memberRoleDTOList.stream().collect(Collectors.groupingBy(MemberRoleDTO::getMemberId));
        List<MemberRoleDTO> result = new ArrayList<>();
        memberRolesMap.forEach((memberId, memberRoleDTOS) -> result.addAll(roleMemberService.insertOrUpdateRolesOfUserByMemberId(false, sourceId, memberId, memberRoleDTOS, sourceType, syncAll)));
        return result;
    }

    @Override
    public List<UserDTO> listEnableUsersByName(String sourceType, Long sourceId, String userName) {
        validateSourceNotExisted(sourceType, sourceId);
        return userMapper.listEnableUsersByName(sourceType, sourceId, userName);
    }

    @Override
    public List<ProjectDTO> listProjectsByUserId(Long organizationId, Long userId, ProjectDTO projectDTO, String params) {
        CustomUserDetails customUserDetails = checkLoginUser(userId);
        boolean isAdmin = false;
        if (customUserDetails.getAdmin() != null) {
            isAdmin = customUserDetails.getAdmin();
        }
        boolean isOrgAdmin = userMapper.isOrgAdministrator(organizationId, userId);
        List<ProjectDTO> projects = new ArrayList<>();
        // 普通用户只能查到启用的项目
        if (!isAdmin && !isOrgAdmin) {
            if (projectDTO.getEnabled() != null && !projectDTO.getEnabled()) {
                return projects;
            } else {
                projectDTO.setEnabled(true);
            }
        }
        projects = projectMapper.selectProjectsByUserIdOrAdmin(organizationId, userId, projectDTO, isAdmin, isOrgAdmin, params);
        setProjectsInto(projects, isAdmin, isOrgAdmin);
        return projects;
    }

    private void setProjectsInto(List<ProjectDTO> projects, boolean isAdmin, boolean isOrgAdmin) {
        if (!CollectionUtils.isEmpty(projects)) {
            projects.forEach(p -> {
                p.setCategory(p.getCategories().get(0).getCode());
                // 如果项目为禁用 不可进入
                if (p.getEnabled() == null || !p.getEnabled()) {
                    p.setInto(false);
                    return;
                }
                // 如果不是admin用户和组织管理员且未分配项目角色 不可进入
                if (!isAdmin && !isOrgAdmin && CollectionUtils.isEmpty(p.getRoles())) {
                    p.setInto(false);
                }

            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(UserDTO userDTO) {
        List<RoleDTO> roleDTOList = userDTO.getRoles();
        UserValidator.validateUseRoles(roleDTOList, true);
        if (devopsMessage) {
            userDTO = sendUpdateUserEvent(userDTO);
        } else {
            userDTO = updateSelective(userDTO);
        }
        createUserRoles(userDTO, roleDTOList, ResourceType.SITE.value(), 0L, true, true, true);
        return userDTO;
    }

    private UserDTO sendUpdateUserEvent(UserDTO user) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("user")
                        .withSagaCode(USER_UPDATE),
                builder -> {
                    UserDTO userDTO = updateSelective(user);
                    UserEventPayload userEventPayload = new UserEventPayload();
                    userEventPayload.setEmail(userDTO.getEmail());
                    userEventPayload.setId(userDTO.getId().toString());
                    userEventPayload.setName(userDTO.getRealName());
                    userEventPayload.setUsername(userDTO.getLoginName());
                    builder
                            .withPayloadAndSerialize(userEventPayload)
                            .withRefId(userEventPayload.getId())
                            .withSourceId(0L);
                    return userDTO;
                });
    }

    @Override
    public UserDTO enableUser(Long userId) {
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userDTO.setEnabled(true);
        if (devopsMessage) {
            return sendEnableOrDisableUserEvent(userDTO, USER_DISABLE);
        } else {
            return updateSelective(userDTO);
        }
    }

    @Override
    public UserDTO disableUser(Long userId) {
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userDTO.setEnabled(false);
        if (devopsMessage) {
            return sendEnableOrDisableUserEvent(userDTO, USER_ENABLE);
        } else {
            return updateSelective(userDTO);
        }
    }

    private UserDTO sendEnableOrDisableUserEvent(UserDTO user, String sagaCode) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.SITE)
                        .withRefType("user")
                        .withSagaCode(sagaCode),
                builder -> {
                    UserDTO userDTO = updateSelective(user);
                    UserEventPayload userEventPayload = new UserEventPayload();
                    userEventPayload.setUsername(userDTO.getLoginName());
                    userEventPayload.setId(userDTO.getId().toString());
                    builder
                            .withPayloadAndSerialize(userEventPayload)
                            .withRefId(userEventPayload.getId())
                            .withSourceId(0L);
                    return userDTO;
                });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO unlockUser(Long userId) {
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userDTO.setLocked(false);
        passwordRecord.unLockUser(userDTO.getId());
        return updateSelective(userDTO);
    }

    @Override
    public List<ProjectDTO> queryProjects(Long userId, Boolean includedDisabled) {
        CustomUserDetails customUserDetails = checkLoginUser(userId);
        boolean isAdmin = false;
        if (customUserDetails.getAdmin() != null) {
            isAdmin = customUserDetails.getAdmin();
        }
        ProjectDTO project = new ProjectDTO();
        if (!isAdmin && includedDisabled != null && !includedDisabled) {
            project.setEnabled(true);
        }
        List<ProjectDTO> projects = projectMapper.selectAllProjectsByUserIdOrAdmin(userId, project, isAdmin);
        projects.forEach(p -> p.setCategory(p.getCategories().get(0).getCode()));
        return projects;
    }

    private void validateSourceNotExisted(String sourceType, Long sourceId) {
        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
            organizationAssertHelper.notExisted(sourceId);
        }
        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
            projectAssertHelper.projectNotExisted(sourceId);
        }
    }

    @Override
    public PageInfo<OrganizationDTO> pagingQueryOrganizationsWithRoles(Pageable pageable, Long id, String params) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        Page<OrganizationDTO> result = new Page<>(page, size);
        int start = PageUtils.getBegin(page, size);
        int count = memberRoleMapper.selectCountBySourceId(id, "organization");
        result.setTotal(count);
        result.addAll(organizationMapper.selectOrganizationsWithRoles(id, start, size, params));
        return result.toPageInfo();
    }

    @Override
    public PageInfo<ProjectDTO> pagingQueryProjectAndRolesById(Pageable pageable, Long id, String params) {
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        Page<ProjectDTO> result = new Page<>(page, size);
        if (size == 0) {
            List<ProjectDTO> projectList = projectMapper.selectProjectsWithRoles(id, null, null, params);
            result.setTotal(projectList.size());
            result.addAll(projectList);
        } else {
            int start = PageUtils.getBegin(page, size);
            int count = memberRoleMapper.selectCountBySourceId(id, "project");
            result.setTotal(count);
            List<ProjectDTO> projectList = projectMapper.selectProjectsWithRoles(id, start, size, params);
            result.addAll(projectList);
        }
        return result.toPageInfo();
    }

    @Override
    @Transactional
    public UserDTO createUserAndAssignRoles(final CreateUserWithRolesDTO userWithRoles) {
        List<RoleDTO> roles = validateRoles(userWithRoles);
        UserDTO user = validateUser(userWithRoles);
        if (userMapper.insertSelective(user) != 1) {
            throw new CommonException("error.user.create");
        }
        UserDTO userDTO = userMapper.selectByPrimaryKey(user);
        Long userId = userDTO.getId();
        roles.forEach(r -> {
            MemberRoleDTO memberRole = new MemberRoleDTO();
            memberRole.setMemberId(userId);
            memberRole.setMemberType(userWithRoles.getMemberType());
            memberRole.setRoleId(r.getId());
            memberRole.setSourceId(userWithRoles.getSourceId());
            memberRole.setSourceType(userWithRoles.getSourceType());
            if (memberRoleMapper.selectOne(memberRole) == null
                    && memberRoleMapper.insertSelective(memberRole) != 1) {
                throw new CommonException("error.memberRole.insert");
            }
        });
        return userDTO;
    }

    @Override
    public Long[] listUserIds() {
        return userMapper.listUserIds();
    }

    private UserDTO validateUser(CreateUserWithRolesDTO userWithRoles) {
        UserDTO user = userWithRoles.getUser();
        String loginName = user.getLoginName();
        String email = user.getEmail();
        if (StringUtils.isEmpty(loginName)) {
            throw new CommonException("error.user.loginName.empty");
        }
        if (StringUtils.isEmpty(email)) {
            throw new CommonException("error.user.email.empty");
        }
        userAssertHelper.loginNameExisted(loginName);
        userAssertHelper.emailExisted(email);
        validatePassword(user);
        user.setPassword(ENCODER.encode(user.getPassword()));
        user.setEnabled(true);
        user.setLdap(false);
        if (user.getLanguage() == null) {
            user.setLanguage("zh_CN");
        }
        if (user.getTimeZone() == null) {
            user.setTimeZone("CTT");
        }
        user.setLastPasswordUpdatedAt(new Date(System.currentTimeMillis()));
        user.setLocked(false);
        user.setAdmin(false);
        return user;
    }

    private void validatePassword(UserDTO user) {
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            throw new CommonException("error.user.password.empty");
        }
        Long organizationId = user.getOrganizationId();
        BaseUserDTO userDO = new BaseUserDTO();
        BeanUtils.copyProperties(user, userDO);
        BasePasswordPolicyDTO example = new BasePasswordPolicyDTO();
        example.setOrganizationId(organizationId);
        Optional.ofNullable(basePasswordPolicyMapper.selectOne(example))
                .ifPresent(passwordPolicy -> {
                    if (!password.equals(passwordPolicy.getOriginalPassword())) {
                        passwordPolicyManager.passwordValidate(password, userDO, passwordPolicy);
                    }
                });
    }

    private List<RoleDTO> validateRoles(CreateUserWithRolesDTO userWithRoles) {
        UserDTO user = userWithRoles.getUser();
        if (user == null) {
            throw new CommonException("error.user.null");
        }
        Long sourceId = userWithRoles.getSourceId();
        String sourceType = userWithRoles.getSourceType();
        validateSourceType(user, sourceId, sourceType);
        if (userWithRoles.getMemberType() == null) {
            userWithRoles.setMemberType("user");
        }
        Set<String> roleCodes = userWithRoles.getRoleCode();
        List<RoleDTO> roles = new ArrayList<>();
        if (roleCodes == null) {
            throw new CommonException("error.roleCode.null");
        } else {
            roleCodes.forEach(code -> {
                RoleDTO role = roleAssertHelper.roleNotExisted(code);
                if (!role.getResourceLevel().equals(sourceType)) {
                    throw new CommonException("error.illegal.role.level");
                }
                roles.add(role);
            });
        }
        return roles;
    }

    private void validateSourceType(UserDTO user, Long sourceId, String sourceType) {
        ResourceLevelValidator.validate(sourceType);
        if (ResourceLevel.SITE.value().equals(sourceType)
                || ResourceLevel.USER.value().equals(sourceType)) {
            throw new CommonException("error.illegal.sourceType");
        } else if (ResourceLevel.PROJECT.value().equals(sourceType)) {
            ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(sourceId);
            Long organizationId = projectDTO.getOrganizationId();
            user.setOrganizationId(organizationId);
        } else {
            //organization level
            organizationAssertHelper.notExisted(sourceId);
            user.setOrganizationId(sourceId);
        }
    }

    @Override
    public Long queryOrgIdByEmail(String email) {
        return userAssertHelper.userNotExisted(WhichColumn.EMAIL, email).getOrganizationId();
    }

    @Override
    public PageInfo<SimplifiedUserDTO> pagingQueryAllUser(Pageable pageable, String param, Long organizationId) {
        if (StringUtils.isEmpty(param) && Long.valueOf(0).equals(organizationId)) {
            Page<SimplifiedUserDTO> result = new Page<>(0, 20);
            result.setTotal(0);
            return result.toPageInfo();
        }
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        if (organizationId.equals(0L)) {
            return PageMethod.startPage(page, size).doSelectPageInfo(() -> userMapper.selectAllUsersSimplifiedInfo(param));
        } else {
            return PageMethod.startPage(page, size).doSelectPageInfo(() -> userMapper.selectUsersOptional(param, organizationId));
        }
    }

    @Override
    public PageInfo<UserDTO> pagingQueryUsersOnSiteLevel(Long userId, String email, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> userMapper.selectUsersByLevelAndOptions(ResourceLevel.SITE.value(), 0L, userId, email, param));
    }

    @Override
    public Map<String, Object> queryAllAndNewUsers() {
        Map<String, Object> map = new HashMap<>();
        UserDTO dto = new UserDTO();
        map.put("allUsers", userMapper.selectCount(dto));
        LocalDate localDate = LocalDate.now();
        String begin = localDate.toString();
        String end = localDate.plusDays(1).toString();
        map.put("newUsers", userMapper.newUsersByDate(begin, end));
        return map;
    }

    @Override
    public PageInfo<UserRoleDTO> pagingQueryRole(Pageable pageable, Long userId, String name, String level, String params) {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        Long id = customUserDetails.getUserId();
        if (!id.equals(userId)) {
            throw new CommonException("error.permission.id.notMatch");
        }
        PageInfo<UserRoleDTO> result = PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() ->
                userMapper.selectRoles(userId, name, level, params));
        result.getList().forEach(i -> {
            String[] roles = i.getRoleNames().split(",");
            List<RoleNameAndEnabledDTO> list = new ArrayList<>(roles.length);
            for (int j = 0; j < roles.length; j++) {
                String[] nameAndEnabled = roles[j].split("\\|");
                boolean roleEnabled = true;
                if (nameAndEnabled[2].equals("0")) {
                    roleEnabled = false;
                }
                list.add(new RoleNameAndEnabledDTO(nameAndEnabled[0], nameAndEnabled[1], roleEnabled));
            }
            i.setRoles(list);
            if (ResourceLevel.PROJECT.value().equals(i.getLevel())) {
                i.setOrganizationId(projectMapper.selectByPrimaryKey(i.getId()).getOrganizationId());
            }
        });
        return result;
    }

    @Override
    @Async("notify-executor")
    public Future<String> sendNotice(Long fromUserId, List<Long> userIds, String code,
                                     Map<String, Object> params, Long sourceId) {
        return sendNotice(fromUserId, userIds, code, params, sourceId, false);
    }

    @Override
    @Async("notify-executor")
    public Future<String> sendNotice(Long fromUserId, List<Long> userIds, String code, Map<String, Object> params, Long sourceId, boolean sendAll) {
        LOGGER.info("ready : send Notice to {} users", userIds.size());
        if (CollectionUtils.isEmpty(userIds)) {
            return new AsyncResult<>("userId is null");
        }
        long beginTime = System.currentTimeMillis();
        NoticeSendDTO noticeSendDTO = new NoticeSendDTO();
        noticeSendDTO.setCode(code);
        NoticeSendDTO.User currentUser = new NoticeSendDTO.User();
        currentUser.setId(fromUserId);
        noticeSendDTO.setFromUser(currentUser);
        noticeSendDTO.setParams(params);
        noticeSendDTO.setSourceId(sourceId);
        List<NoticeSendDTO.User> users = new LinkedList<>();
        userIds.forEach(id -> {
            NoticeSendDTO.User user = new NoticeSendDTO.User();
            user.setId(id);
            //如果是发送给所有人，我们无需查看是否有角色分配，全部发送，避免查表
            if (!sendAll) {
                UserDTO userDTO = userMapper.selectByPrimaryKey(id);
                if (userDTO != null) {
                    //有角色分配，但是角色已经删除
                    user.setEmail(userDTO.getEmail());
                    users.add(user);
                }
            } else {
                users.add(user);
            }
        });
        noticeSendDTO.setTargetUsers(users);
        LOGGER.info("start : send Notice to {} users", userIds.size());
        notifyFeignClient.postNotice(noticeSendDTO);
        LOGGER.info("end : send Notice to {} users", userIds.size());
        return new AsyncResult<>((System.currentTimeMillis() - beginTime) / 1000 + "s");
    }

    @Override
    @Async("notify-executor")
    public Future<String> sendNotice(Long fromUserId, Map<Long, Set<Long>> longSetMap, String code, Map<String, Object> params, Long sourceId) {
        long beginTime = System.currentTimeMillis();
        for (Map.Entry<Long, Set<Long>> longSetEntry : longSetMap.entrySet()) {
            if (!CollectionUtils.isEmpty(longSetEntry.getValue())) {
                //封装消息内容参数
                String roleName = longSetEntry.getValue().stream().map(e -> roleMapper.selectByPrimaryKey(e).getName()).collect(Collectors.joining(","));
                params.put("roleName", roleName);
                sendNotice(fromUserId, Arrays.asList(longSetEntry.getKey()), code, params, sourceId);
            }
        }
        return new AsyncResult<>((System.currentTimeMillis() - beginTime) / 1000 + "s");
    }

    @Override
    public UserDTO updateUserDisabled(Long userId) {
        UserDTO userDTO = userAssertHelper.userNotExisted(userId);
        userDTO.setEnabled(false);
        return updateSelective(userDTO);
    }

    @Override
    public OrganizationProjectDTO queryOrganizationProjectByUserId(Long userId) {
        OrganizationProjectDTO organizationProjectDTO = new OrganizationProjectDTO();
        organizationProjectDTO.setOrganizationList(organizationMapper.selectFromMemberRoleByMemberId(userId, false).stream().map(organizationDO ->
                OrganizationProjectDTO.newInstanceOrganization(organizationDO.getId(), organizationDO.getName(), organizationDO.getCode())).collect(Collectors.toList()));
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setEnabled(true);
        organizationProjectDTO.setProjectList(projectMapper.selectProjectsByUserId(userId, projectDTO)
                .stream().map(p -> OrganizationProjectDTO.newInstanceProject(p.getId(), p.getName(), p.getCode())).collect(Collectors.toList()));
        return organizationProjectDTO;
    }

    @Override
    public List<UserDTO> listEnableUsersByRouteRuleCode(String userName) {
        List<UserDTO> userDTOS = listEnableUsersByName(ResourceLevel.SITE.value(), 0L, userName);
        List<Long> userIds = new ArrayList<>();
        routeMemberRuleMapper.selectAll().forEach(v -> userIds.add(v.getUserId()));

        return userDTOS.stream().filter(v -> !userIds.contains(v.getId())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO queryProjectById(Long id, Long projectId) {
        return userMapper.selectProjectByUidAndProjectId(id, projectId);
    }

    @Override
    public Boolean checkIsProjectOwner(Long id, Long projectId) {
        List<RoleDTO> roleDTOList = userMapper.selectRolesByUidAndProjectId(id, projectId);
        return CollectionUtils.isEmpty(roleDTOList) ? false : roleDTOList.stream().anyMatch(v -> RoleEnum.PROJECT_OWNER.value().equals(v.getCode()));
    }

    @Override
    public Boolean checkIsGitlabOrgOwner(Long id, Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        Long organizationId = projectDTO.getOrganizationId();
        List<RoleDTO> roleDTOList = userMapper.selectRolesByUidAndProjectIdOnOrg(id, organizationId);
        return CollectionUtils.isEmpty(roleDTOList) ? false : roleDTOList.stream().anyMatch(v -> RoleEnum.ORG_ADMINISTRATOR.value().equals(v.getCode()));
    }

    @Override
    public Boolean checkIsGitlabProjectOwner(Long id, Long projectId) {
        return userMapper.checkIsGitlabProjectOwner(id, projectId) != 0;
    }

    @Override
    public List<UserDTO> listProjectUsersByProjectIdAndRoleLable(Long projectId, String roleLable) {
        return userMapper.listProjectUsersByProjectIdAndRoleLable(projectId, roleLable);
    }

    @Override
    public List<UserDTO> listUsersByName(Long projectId, String param) {
        return userMapper.listUsersByName(projectId, param);
    }


    @Override
    public List<UserDTO> queryAllAdminUsers() {
        UserDTO searchCondition = new UserDTO();
        searchCondition.setAdmin(Boolean.TRUE);
        return userMapper.select(searchCondition);
    }

    @Override
    public List<UserDTO> queryAllOrgAdmin() {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setCode(RoleEnum.ORG_ADMINISTRATOR.value());
        RoleDTO reDto = roleMapper.selectOne(roleDTO);
        return userMapper.queryAllOrgAdmin(reDto.getId());
    }

    @Override
    public UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime) {
        UserNumberVO userNumberVO = new UserNumberVO();
        long previousNumber = userMapper.countPreviousNumberByOrgIdAndDate(organizationId, new java.sql.Date(startTime.getTime()));
        List<UserDTO> userDTOS = userMapper.selectByOrgIdAndDate(organizationId,
                new java.sql.Date(startTime.getTime()),
                new java.sql.Date(endTime.getTime()));
        // 按日期分组
        Map<String, List<UserDTO>> userMap = userDTOS.stream()
                .collect(Collectors.groupingBy(t -> new java.sql.Date(t.getCreationDate().getTime()).toString()));

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();
        long totalNumber = previousNumber;
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            long newUserNumber = 0;
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<UserDTO> userList = userMap.get(date);
            if (!CollectionUtils.isEmpty(userList)) {
                newUserNumber = userList.size();
                totalNumber += newUserNumber;
            }

            userNumberVO.getDateList().add(date);
            userNumberVO.getTotalUserNumberList().add(totalNumber);
            userNumberVO.getNewUserNumberList().add(newUserNumber);

            startDate = startDate.plusDays(1);
        }

        return userNumberVO;
    }

    @Override
    public Boolean checkIsRoot(Long id) {
        UserDTO userDTO = userMapper.selectByPrimaryKey(id);
        if (userDTO == null) {
            throw new CommonException(USER_NOT_FOUND_EXCEPTION);
        }
        return userDTO.getAdmin();
    }

    @Override
    public Boolean checkIsOrgRoot(Long organizationId, Long userId) {
        return userMapper.isOrgAdministrator(organizationId, userId);
    }

    @Override
    public List<UserDTO> listProjectOwnerById(Long projectId) {
        return userMapper.listProjectOwnerById(projectId);
    }
}