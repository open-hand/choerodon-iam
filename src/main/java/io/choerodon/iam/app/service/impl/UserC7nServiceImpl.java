package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.constant.TenantConstants.BACKETNAME;
import static io.choerodon.iam.infra.utils.SagaTopic.MemberRole.MEMBER_ROLE_UPDATE;
import static java.util.stream.Collectors.mapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.boot.message.MessageClient;
import org.hzero.boot.message.entity.MessageSender;
import org.hzero.boot.message.entity.Receiver;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.service.UserPasswordService;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.AssertUtils;
import org.hzero.core.util.TokenUtils;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.app.service.MemberRoleService;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.domain.repository.UserSelfRepository;
import org.hzero.iam.domain.service.user.UserCaptchaService;
import org.hzero.iam.domain.service.user.UserDetailsService;
import org.hzero.iam.domain.vo.RoleVO;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.hzero.iam.infra.mapper.*;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.IEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.MessageAdditionalType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.validator.UserValidator;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.*;
import io.choerodon.iam.infra.constant.MemberRoleConstants;
import io.choerodon.iam.infra.constant.RedisCacheKeyConstants;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.constant.TenantConstants;
import io.choerodon.iam.infra.dto.*;
import io.choerodon.iam.infra.dto.payload.UserMemberEventPayload;
import io.choerodon.iam.infra.dto.payload.WebHookUser;
import io.choerodon.iam.infra.enums.*;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.feign.operator.DevopsFeignClientOperator;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.utils.*;
import io.choerodon.iam.infra.valitador.RoleValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * @author scp
 * @since 2020/4/1
 */
@Service
public class UserC7nServiceImpl implements UserC7nService {
    private static final String ROOT_BUSINESS_TYPE_CODE = "SITEADDROOT";

    private static final String USER_NOT_LOGIN_EXCEPTION = "error.user.not.login";
    private static final String USER_ID_NOT_EQUAL_EXCEPTION = "error.user.id.not.equals";
    private static final String PROJECT = "project";
    private static final String ORGANIZATION = "organization";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserC7nServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    private TransactionalProducer producer;
    @Autowired
    private FileClient fileClient;
    @Autowired
    private MemberRoleService memberRoleService;
    @Autowired
    @Lazy
    private RoleMemberService roleMemberService;
    @Autowired
    private TenantC7nMapper tenantC7nMapper;
    @Autowired
    private MessageClient messageClient;
    @Autowired
    private MemberRoleC7nMapper memberRoleC7nMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private RoleAssertHelper roleAssertHelper;
    @Autowired
    @Lazy
    private UserPasswordService userPasswordService;
    @Autowired
    @Lazy
    private UserDetailsService userDetailsService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TenantConfigMapper tenantConfigMapper;

    @Autowired
    private OrganizationAssertHelper organizationAssertHelper;
    @Autowired
    private ProjectAssertHelper projectAssertHelper;
    @Autowired
    private UserAssertHelper userAssertHelper;
    @Autowired
    private DevopsFeignClientOperator devopsFeignClientOperator;
    @Autowired
    private TenantRepository tenantRepository;
    @Autowired
    private RoleC7nService roleC7nService;
    @Autowired
    private MemberRoleMapper memberRoleMapper;
    @Autowired
    private TenantMapper tenantMapper;
    @Autowired
    private LabelC7nMapper labelC7nMapper;
    @Autowired
    @Lazy
    private MessageSendService messageSendService;
    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;
    @Autowired
    @Lazy
    private ProjectCategoryC7nService projectCategoryC7nService;
    @Autowired
    @Lazy
    private OrganizationUserService organizationUserService;
    @Autowired
    private StarProjectMapper starProjectMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    @Lazy
    private ProjectC7nService projectC7nService;
    @Autowired
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    @Autowired
    private PasswordPolicyManager passwordPolicyManager;
    @Autowired
    private EncryptClient encryptClient;
    @Autowired
    private UserSelfRepository userSelfRepository;
    @Autowired
    private OauthAdminFeignClient oauthAdminFeignClient;
    @Autowired
    @Lazy
    protected UserCaptchaService userCaptchaService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    @Lazy
    private TenantC7nService tenantC7nService;
    @Autowired
    private IEncryptionService encryptionService;
    @Autowired(required = false)
    private BusinessService businessService;
    @Autowired
    private ProjectMapCategoryService projectMapCategoryService;

    @Override
    public User queryInfo(Long userId) {
        User user = userAssertHelper.userNotExisted(userId);
        Tenant tenant = organizationAssertHelper.notExisted(user.getOrganizationId());
        user.setTenantName(tenant.getTenantName());
        user.setTenantNum(tenant.getTenantName());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User updateInfo(User user, Boolean checkLogin) {
        if (checkLogin) {
            checkLoginUser(user.getId());
        }
        User userDetails = userRepository.selectByPrimaryKey(user.getId());
        if (Boolean.FALSE.equals(userDetails.ldapUser())) {
            user.setEmailCheckFlag(BaseConstants.Flag.YES);
            user.setPhoneCheckFlag(BaseConstants.Flag.YES);
        }
        // hzero没有用户名变化参数 用phone参数代替
        if (!userDetails.getRealName().equals(user.getRealName())) {
            user.setPhoneChanged(true);
        }
        //设置用户info信息
        organizationUserService.setUserInfo(user);
        userService.updateUserInternal(user);
        // 因为hzero的方法不更新头像 不能在updateUserInternal 更新用户
        userRepository.updateOptional(user, User.FIELD_IMAGE_URL);
        return userRepository.selectByPrimaryKey(user.getId());
    }

    private void keepEmailCheckFlag(Long userId) {
        UserInfo record = new UserInfo();
        record.setUserId(userId);
        UserInfo userInfo = userInfoMapper.selectOne(record);
        if (userInfo != null) {
            userInfo.setEmailCheckFlag(BaseConstants.Flag.YES);
            userInfoMapper.updateByPrimaryKey(userInfo);
        }
    }

    @Override
    public String uploadPhoto(Long id, MultipartFile file) {
        checkLoginUser(id);
        return fileClient.uploadFile(0L, BACKETNAME, null, file.getOriginalFilename(), file);
    }

    @Override
    public String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {
        checkLoginUser(id);
        try {
            file = ImageUtils.cutImage(file, rotate, axisX, axisY, width, height);
            return fileClient.uploadFile(0L, BACKETNAME, null, file.getOriginalFilename(), file);
        } catch (Exception e) {
            LOGGER.warn("error happened when save photo {}", e.getMessage());
            throw new CommonException("error.user.photo.save", e);
        }
    }

    @Override
    public void check(User user) {
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


    @Override
    public CustomUserDetails checkLoginUser(Long id) {
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
    public List<User> listUsersByIds(Long[] ids, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(ids)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByIds(ids, onlyEnabled);
        }
    }

    @Override
    public List<UserWithGitlabIdVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        } else {
            List<User> users = userC7nMapper.listUsersByIds(ids.toArray(new Long[0]), onlyEnabled);
            List<UserAttrVO> userAttrVOS = devopsFeignClientOperator.listByUserIds(ids);
            if (userAttrVOS == null) {
                userAttrVOS = new ArrayList<>();
            }
            Map<Long, Long> userIdMap = userAttrVOS.stream().collect(Collectors.toMap(UserAttrVO::getIamUserId, UserAttrVO::getGitlabUserId));
            // 填充gitlabUserId
            return users.stream().map(user -> toUserWithGitlabIdDTO(user, userIdMap.get(user.getId()))).collect(Collectors.toList());
        }
    }

    @Override
    public List<User> listUsersByEmails(String[] emails) {
        if (ObjectUtils.isEmpty(emails)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByEmails(emails);
        }
    }

    @Override
    public List<User> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(loginNames)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByLoginNames(loginNames, onlyEnabled);
        }
    }

    @Override
    public Long queryOrgIdByEmail(String email) {
        return userAssertHelper.userNotExisted(UserAssertHelper.WhichColumn.EMAIL, email).getOrganizationId();
    }

    @Override
    public Map<String, Object> queryAllAndNewUsers() {
        Map<String, Object> map = new HashMap<>();
        User dto = new User();
        map.put("allUsers", userMapper.selectCount(dto));
        LocalDate localDate = LocalDate.now();
        String begin = localDate.toString();
        String end = localDate.plusDays(1).toString();
        map.put("newUsers", userC7nMapper.newUsersByDate(begin, end));
        return map;
    }

    @Override
    public UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime) {
        UserNumberVO userNumberVO = new UserNumberVO();
        long previousNumber = userC7nMapper.countPreviousNumberByOrgIdAndDate(organizationId, new java.sql.Date(startTime.getTime()));
        List<User> userDTOS = userC7nMapper.selectByOrgIdAndDate(organizationId,
                new java.sql.Date(startTime.getTime()),
                new java.sql.Date(endTime.getTime()));
        // 按日期分组
        Map<String, List<User>> userMap = userDTOS.stream()
                .collect(Collectors.groupingBy(t -> new java.sql.Date(t.getCreationDate().getTime()).toString()));

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();
        long totalNumber = previousNumber;
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            long newUserNumber = 0;
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            List<User> userList = userMap.get(date);
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
    public Page<User> pagingQueryAdminUsers(PageRequest pageRequest, String loginName, String realName, String params) {
        return PageHelper.doPageAndSort(pageRequest, () -> userC7nMapper.selectAdminUserPage(loginName, realName, params, null));
    }

    /**
     * root用户=拥有租户管理角色+平台管理员角色
     *
     * @param ids
     */
    @Saga(code = SagaTopic.User.ASSIGN_ADMIN, description = "分配Root权限同步事件", inputSchemaClass = AssignAdminVO.class)
    @Override
    @Transactional
    public void addAdminUsers(Long[] ids) {
        List<Long> adminUserIds = new ArrayList<>();
        for (long id : ids) {
            User dto = userRepository.selectByPrimaryKey(id);
            if (dto != null && !dto.getAdmin()) {
                dto.setAdmin(true);
                adminUserIds.add(id);
                updateSelective(dto);
            }
        }
        //添加成功后发送站内信和邮件通知被添加者
        if (!adminUserIds.isEmpty()) {
            ((UserC7nServiceImpl) AopContext.currentProxy()).sendNotice(adminUserIds, ROOT_BUSINESS_TYPE_CODE, Collections.emptyMap(), 0L, ResourceLevel.SITE);
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


    @Override
    @Async
    public void sendNotice(List<Long> userIds, String code,
                           Map<String, String> params, Long sourceId, ResourceLevel resourceLevel) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }
        LOGGER.info("ready : send Notice to {} users", userIds.size());

        ExceptionUtil.doWithTryCatchAndLog(LOGGER,
                () -> doSendNotice(userIds, code, params, sourceId, resourceLevel),
                ex -> LOGGER.info("Failed to send notices. The code is {}, and the users are: {}", code, userIds));
    }

    /**
     * 给指定用户发送消息
     *
     * @param userIds       接收者的id
     * @param code          消息的编码
     * @param params        消息所需的参数
     * @param sourceId      消息的级别对应的id，如组织id，项目id
     * @param resourceLevel 消息的级别
     */
    private void doSendNotice(List<Long> userIds, String code, Map<String, String> params, Long sourceId, ResourceLevel resourceLevel) {
        MessageSender messageSender = new MessageSender();
        messageSender.setTenantId(0L);
        Map<String, Object> additionalParams = new HashMap<>();
        messageSender.setTenantId(0L);
        if (ResourceLevel.ORGANIZATION == resourceLevel) {
            messageSender.setTenantId(sourceId);
            additionalParams.put(MessageAdditionalType.PARAM_TENANT_ID.getTypeName(), sourceId);
        } else if (ResourceLevel.PROJECT == resourceLevel) {
            additionalParams.put(MessageAdditionalType.PARAM_PROJECT_ID.getTypeName(), sourceId);
        }
        messageSender.setReceiverAddressList(constructUsersByIds(userIds));
        messageSender.setArgs(params);
        messageSender.setMessageCode(code);
        messageSender.setAdditionalInformation(additionalParams);

        messageClient.async().sendMessage(messageSender);
    }

    private List<Receiver> constructUsersByIds(List<Long> userIds) {
        List<User> users = userRepository.selectByIds(org.apache.commons.lang3.StringUtils.join(userIds, ","));
        if (!CollectionUtils.isEmpty(users)) {
            return users.stream().map(u -> {
                Receiver receiver = new Receiver();
                receiver.setUserId(u.getId());
                receiver.setEmail(u.getEmail());
                receiver.setPhone(u.getPhone());
                receiver.setTargetUserTenantId(Objects.requireNonNull(u.getOrganizationId(), "receiver tenant id can't be null"));
                return receiver;
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    @Saga(code = SagaTopic.User.DELETE_ADMIN, description = "用户Root权限被删除事件同步", inputSchemaClass = DeleteAdminVO.class)
    @Override
    public void deleteAdminUser(long id) {
        User userDTO = new User();
        userDTO.setAdmin(true);
        if (userMapper.selectCount(userDTO) > 1) {
            User dto = userAssertHelper.userNotExisted(id);
            if (dto.getAdmin()) {
                dto.setAdmin(false);
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
    public Page<TenantVO> pagingQueryOrganizationsWithRoles(PageRequest pageRequest, Long id, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        Page<TenantVO> result = new Page<>();
        int start = PageUtils.getBegin(page, size);
        int count = memberRoleC7nMapper.selectCountBySourceId(id, "organization");
        result.setSize(count);
        result.getContent().addAll(tenantC7nMapper.selectOrganizationsWithRoles(id, start, size, params));
        return result;
    }


    @Override
    public Page<ProjectDTO> pagingQueryProjectAndRolesById(PageRequest pageRequest, Long id, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        Page<ProjectDTO> result = new Page<>();

        if (size == 0) {
            List<ProjectDTO> projectList = projectMapper.selectProjectsWithRoles(id, null, null, params);
            result.setTotalElements(projectList.size());
            result.getContent().addAll(projectList);
        } else {
            int start = PageUtils.getBegin(page, size);
            result.setTotalElements(projectMapper.countProjectsWithRolesSize(id, params));
            List<ProjectDTO> projectList = projectMapper.selectProjectsWithRoles(id, start, size, params);
            result.getContent().addAll(projectList);
        }
        result.setNumber(page);
        result.setSize(size);
        return result;
    }


    @Override
    public Boolean isRoot(Long id) {
        User result = userRepository.selectByPrimaryKey(id);
        return result != null && result.getAdmin();
    }

    @Override
    public List<ProjectDTO> queryProjects(Long userId, Boolean includedDisabled) {
        boolean isAdmin = isRoot(userId);
        ProjectDTO project = new ProjectDTO();
        if (!isAdmin && includedDisabled != null && !includedDisabled) {
            project.setEnabled(true);
        }
        List<ProjectDTO> projects = projectMapper.selectAllProjectsByUserIdOrAdmin(userId, project, isAdmin);
        projects.forEach(p -> p.setCategory(p.getCategories().get(0).getCode()));
        return projects;
    }


    @Override
    public Page<User> pagingQueryUsersWithRolesOnSiteLevel(PageRequest pageRequest, String orgName, String loginName, String realName,
                                                           String roleName, Boolean enabled, Boolean locked, String params) {
        int page = pageRequest.getPage();
        int size = pageRequest.getSize();
        boolean doPage = (size != 0);
        Page<User> result = new Page<>();
        if (doPage) {
            int start = PageUtils.getBegin(page, size);
            int count = userC7nMapper.selectCountUsersOnSiteLevel(ResourceLevel.SITE.value(), 0L, orgName, loginName, realName,
                    roleName, enabled, locked, params);
            List<User> users = userC7nMapper.selectUserWithRolesOnSiteLevel(start, size, ResourceLevel.SITE.value(), 0L, orgName,
                    loginName, realName, roleName, enabled, locked, params);
            return PageUtils.buildPage(page, size, count, users);
        } else {
            List<User> users = userC7nMapper.selectUserWithRolesOnSiteLevel(null, null, ResourceLevel.SITE.value(), 0L, orgName,
                    loginName, realName, roleName, enabled, locked, params);
            result.setTotalElements(users.size());
            result.getContent().addAll(users);
        }
        return result;
    }

    @Override
    public OrganizationProjectVO queryOrganizationProjectByUserId(Long userId, String projectName) {
        OrganizationProjectVO organizationProjectDTO = new OrganizationProjectVO();
        Map<Long, TenantVO> tenants = tenantC7nMapper.selectFromMemberRoleByMemberId(userId, false)
                .stream()
                .distinct()
                .collect(Collectors.toMap(Tenant::getTenantId, t -> t));

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(projectName);
        projectDTO.setEnabled(true);
        List<ProjectDTO> projectDTOS;
        if (businessService != null) {
            projectDTOS = businessService.selectProjectsByUserId(userId, projectDTO);
        } else {
            projectDTOS = projectMapper.selectProjectsByUserId(userId, projectDTO);
        }
        organizationProjectDTO.setProjectList(projectDTOS.stream()
                .filter(p -> tenants.get(p.getOrganizationId()) != null)
                .map(p -> OrganizationProjectVO.newInstanceProject(p.getId(), p.getName(), p.getCode(), tenants.get(p.getOrganizationId()).getTenantName(), p.getDtEditEnable()))
                .collect(Collectors.toList()));

        return organizationProjectDTO;
    }

    @Override
    public List<UserWithGitlabIdVO> listUsersWithRolesAndGitlabUserIdByIdsInOrg(Long organizationId, Set<Long> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        List<User> userDTOS = userC7nMapper.listUserWithRolesOnOrganizationLevelByIds(organizationId, userIds);
        List<UserAttrVO> userAttrVOS = devopsFeignClientOperator.listByUserIds(userIds);
        if (userAttrVOS == null) {
            userAttrVOS = new ArrayList<>();
        }
        Map<Long, Long> userIdMap = userAttrVOS.stream().collect(Collectors.toMap(UserAttrVO::getIamUserId, UserAttrVO::getGitlabUserId));
        // 填充gitlabUserId
        return userDTOS.stream().map(user -> toUserWithGitlabIdDTO(user, userIdMap.get(user.getId()))).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listProjectsByUserId(Long organizationId, Long userId, ProjectDTO projectDTO, String params) {
        List<ProjectDTO> projects = new ArrayList<>();
        boolean isAdmin = isRoot(userId);
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        // 普通用户只能查到启用的项目
        if (!isAdmin && !isOrgAdmin) {
            if (projectDTO.getEnabled() != null && !projectDTO.getEnabled()) {
                return projects;
            } else {
                projectDTO.setEnabled(true);
            }
        }
        projects = projectMapper.selectProjectsWithCategoryAndRoleByUserIdOrAdmin(organizationId, userId, projectDTO, isAdmin, isOrgAdmin, params);
        if (CollectionUtils.isEmpty(projects)) {
            return projects;
        }
        // 添加额外信息
        Set<Long> pids = projectMapper.listUserManagedProjectInOrg(organizationId, userId);
        setProjectsIntoAndEditFlag(projects, isAdmin, isOrgAdmin, pids);
        return projects;
    }

    @Override
    public List<ProjectDTO> listProjectsByUserIdForSimple(Long organizationId, Long userId, ProjectDTO projectDTO, String params) {
        List<ProjectDTO> projects = new ArrayList<>();
        boolean isAdmin = isRoot(userId);
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        // 普通用户只能查到启用的项目
        return projectMapper.listProjectsByUserIdForSimple(organizationId, userId, projectDTO, isAdmin, isOrgAdmin, params);
    }

    @Override
    public void addExtraInformation(List<ProjectDTO> projects,
                                    boolean isAdmin,
                                    boolean isOrgAdmin,
                                    Long organizationId,
                                    Long userId) {
        if (!CollectionUtils.isEmpty(projects)) {

            Set<Long> projectIdList = projects.stream().map(ProjectDTO::getId).collect(Collectors.toSet());

            // 查询项目类型
            List<ProjectMapCategoryVO> projectMapCategoryVOS = projectMapper.listProjectCategory(projectIdList);
            Map<Long, List<ProjectMapCategoryVO>> projectMapCategoryMap = projectMapCategoryVOS
                    .stream()
                    .collect(Collectors.groupingBy(ProjectMapCategoryVO::getProjectId));

            // 查询用户拥有项目所有者角色的项目
            Set<Long> pids = projectMapper.listUserManagedProjectInOrg(organizationId, userId);

            // 查询用户star的项目
            List<ProjectDTO> starProjects = starProjectMapper.query(projectIdList, userId);
            Set<Long> starIds = new HashSet<>();
            if (!CollectionUtils.isEmpty(starProjects)) {
                starIds = starProjects.stream().map(ProjectDTO::getId).collect(Collectors.toSet());
            }

            // 遍历项目,计算信息
            Set<Long> finalStarIds = starIds;
            //区分创建项目和修改的项目
            List<String> createIds = projects.stream()
                    .filter(projectDTO -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(projectDTO.getOperateType(), ProjectOperatorTypeEnum.CREATE.value()))
                    .map(projectDTO -> String.valueOf(projectDTO.getId())).collect(Collectors.toList());
            List<String> updateIds = projects.stream()
                    .filter(projectDTO -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(projectDTO.getOperateType(), ProjectOperatorTypeEnum.UPDATE.value()))
                    .map(projectDTO -> String.valueOf(projectDTO.getId())).collect(Collectors.toList());
            //创建项目的saga
            Map<String, SagaInstanceDetails> instanceDetailsMapRepoCreate = new HashMap<>();
            Map<String, SagaInstanceDetails> instanceDetailsMapIamCreate = new HashMap<>();
            if (!CollectionUtils.isEmpty(createIds)) {
                instanceDetailsMapIamCreate = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, createIds, SagaTopic.Project.PROJECT_CREATE));
                instanceDetailsMapRepoCreate = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, createIds, SagaTopic.Project.REPO_CREATE));

            }
            //修改项目的saga
            Map<String, SagaInstanceDetails> instanceDetailsMapIamUpdate = new HashMap<>();
            Map<String, SagaInstanceDetails> instanceDetailsMapRepoUpdate = new HashMap<>();
            if (!CollectionUtils.isEmpty(updateIds)) {
                instanceDetailsMapIamUpdate = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, updateIds, SagaTopic.Project.PROJECT_UPDATE));
                instanceDetailsMapRepoUpdate = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, updateIds, SagaTopic.Project.REPO_CREATE));
            }

            //只需要判断项目需要重试，拿到重试的任务id集合即可
            Map<String, SagaInstanceDetails> finalInstanceDetailsMapIamCreate = instanceDetailsMapIamCreate;
            Map<String, SagaInstanceDetails> finalInstanceDetailsMapRepoCreate = instanceDetailsMapRepoCreate;
            Map<String, SagaInstanceDetails> finalInstanceDetailsMapIamUpdate = instanceDetailsMapIamUpdate;
            Map<String, SagaInstanceDetails> finalInstanceDetailsMapRepoUpdate = instanceDetailsMapRepoUpdate;
            projects.forEach(p -> {
                // 如果项目为禁用 不可进入
                if (p.getEnabled() == null || !p.getEnabled()) {
                    p.setInto(false);
                } else {
                    // 如果不是admin用户和组织管理员且未分配项目角色 不可进入
                    if (!isAdmin && !isOrgAdmin && CollectionUtils.isEmpty(p.getRoles())) {
                        p.setInto(false);
                    }

                    //判断该项目当前的操作是创建还是修改
                    List<SagaInstanceDetails> sagaInstanceDetails = new ArrayList<>();

                    //如果是创建
                    if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(p.getOperateType(), ProjectOperatorTypeEnum.CREATE.value())) {
                        SagaInstanceDetails instanceDetailsIam = finalInstanceDetailsMapIamCreate.get(String.valueOf(p.getId()));
                        SagaInstanceDetails instanceDetailsRepo = finalInstanceDetailsMapRepoCreate.get(String.valueOf(p.getId()));
                        if (!Objects.isNull(instanceDetailsIam)) {
                            sagaInstanceDetails.add(instanceDetailsIam);
                        }
                        if (!Objects.isNull(instanceDetailsRepo)) {
                            sagaInstanceDetails.add(instanceDetailsRepo);
                        }
                        //根据事务实例获取当前项目状态 修改中，成功，失败
                        String sagaStatus = SagaInstanceUtils.getSagaStatus(sagaInstanceDetails);
                        //获取需要重试的任务id集合
                        List<Long> sagaIds = SagaInstanceUtils.getSagaIds(sagaInstanceDetails);
                        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.RUNNING.getValue())) {
                            p.setProjectStatus(ProjectStatusEnum.CREATING.value());
                        } else if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.FAILED.getValue())) {
                            p.setProjectStatus(ProjectStatusEnum.FAILED.value());
                            p.setSagaInstanceIds(sagaIds);
                        } else {
                            p.setProjectStatus(ProjectStatusEnum.SUCCESS.value());
                        }
                    }
                    //如果是修改
                    if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(p.getOperateType(), ProjectOperatorTypeEnum.UPDATE.value())) {
                        SagaInstanceDetails instanceDetailsIam = finalInstanceDetailsMapIamUpdate.get(String.valueOf(p.getId()));
                        SagaInstanceDetails instanceDetailsRepo = finalInstanceDetailsMapRepoUpdate.get(String.valueOf(p.getId()));
                        if (!Objects.isNull(instanceDetailsIam)) {
                            sagaInstanceDetails.add(instanceDetailsIam);
                        }
                        if (!Objects.isNull(instanceDetailsRepo)) {
                            sagaInstanceDetails.add(instanceDetailsRepo);
                        }
                        //根据事务实例获取当前项目状态 修改中，成功，失败
                        String sagaStatus = SagaInstanceUtils.getSagaStatus(sagaInstanceDetails);
                        //获取需要重试的任务id集合
                        List<Long> sagaIds = SagaInstanceUtils.getSagaIds(sagaInstanceDetails);
                        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.RUNNING.getValue())) {
                            p.setProjectStatus(ProjectStatusEnum.UPDATING.value());
                        } else if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.FAILED.getValue())) {
                            p.setProjectStatus(ProjectStatusEnum.FAILED.value());
                            p.setSagaInstanceIds(sagaIds);
                        } else {
                            p.setProjectStatus(ProjectStatusEnum.SUCCESS.value());
                        }
                    }
                }

                // 添加项目类型
                if (projectMapCategoryMap.get(p.getId()) != null) {
                    p.setCategories(projectMapCategoryMap.get(p.getId()).stream().map(ProjectMapCategoryVO::getProjectCategoryDTO).filter(v -> ProjectCategoryEnum.listNewCategories().contains(v.getCode())).collect(Collectors.toList()));
                }

                // 计算用户是否有编辑权限
                if (isAdmin || isOrgAdmin || pids.contains(p.getId())) {
                    p.setEditFlag(true);
                }

                // 计算是否star项目
                if (finalStarIds.contains(p.getId())) {
                    p.setStarFlag(true);
                }
            });
        }

    }

    @Override
    public Boolean platformAdministratorOrAuditor(Long userId) {
        if (userId == null) {
            userId = DetailsHelper.getUserDetails().getUserId();
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (user.getAdmin()) {
            return Boolean.TRUE;
        } else {
            return userC7nMapper.platformAdministrator(userId);
        }
    }

    @Override
    public void checkUserPhoneOccupied(String phone, Long userId) {
        AssertUtils.notNull(phone, "error.user.phone.not.empty");
        User userDTO = new User();
        userDTO.setPhone(phone);
        List<User> users = userMapper.select(userDTO);
        boolean existed = users != null && users.size() != 0;
        if (existed) {
            users.forEach(user -> {
                if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(phone, user.getPhone())
                        && userId.longValue() != user.getId().longValue()) {
                    throw new CommonException("error.user.phone.exist");
                }
            });
        }
    }

    @Override
    public Page<ProjectDTO> pagingProjectsByOptions(Long organizationId, Long userId, ProjectSearchVO projectSearchVO, String params, PageRequest pageable, Boolean onlySucceed) {
        Page<ProjectDTO> page = new Page<>();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        boolean isAdmin = userDetails == null ? isRoot(userId) : Boolean.TRUE.equals(userDetails.getAdmin());
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        Long currentUserId = userDetails == null ? userId : userDetails.getUserId();
        //查询到的项目包括已启用和未启用的
        page = PageHelper.doPage(pageable, () -> projectMapper.selectProjectsByOption(organizationId, userId, projectSearchVO, isAdmin, isOrgAdmin, params));
        Page<ProjectDTO> projectDTOS = handlePageProject(page, organizationId, onlySucceed, isAdmin, isOrgAdmin, currentUserId, userId, pageable);
        // 将忽略的项目放到最前面
        List<ProjectDTO> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(projectSearchVO.getFilterProjectIds())) {
            result.addAll(projectC7nService.queryByIds(new HashSet<>(projectSearchVO.getFilterProjectIds())));
        }
        result.addAll(projectDTOS.getContent());
        projectDTOS.setContent(result);
        return projectDTOS;
    }

    @Override
    public CheckEmailVO checkUserEmail(Long organizationId, String email) {
        CheckEmailVO result = new CheckEmailVO();
        if (org.apache.commons.lang3.StringUtils.isEmpty(email)) {
            result.setExist(false);
            result.setMember(false);
            return result;
        }
        User userDTO = new User();
        userDTO.setEmail(email);
        boolean existed = userMapper.selectOne(userDTO) != null;
        if (existed) {
            result.setExist(true);
        }
        //判断是不是在组织下
        Page<User> userPage = tenantC7nService.pagingQueryUsersInOrganization(organizationId, null, email, new PageRequest(0, 0), null);
        if (userPage != null && !CollectionUtils.isEmpty(userPage.getContent())) {
            result.setMember(true);
        } else {
            result.setMember(false);
        }
        return result;
    }

    @Override
    public Boolean checkLoginName(Long organizationId, Long userId, String loginName) {
        User user = userRepository.selectByLoginName(loginName);
        return user == null || !user.getId().equals(userId);
    }

    @Override
    public Page<User> listUsersOnProjectLevelPage(Long projectId, String userName, PageRequest pageRequest) {
        if (org.apache.commons.lang3.StringUtils.isBlank(userName)) {
            return new Page<>();
        }
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        if (projectDTO == null) {
            return new Page<>();
        }
        Page<User> userPage = PageHelper.doPage(pageRequest, () -> userC7nMapper.listUsersOnProjectLevelPage(projectDTO.getOrganizationId(), userName));
        return userPage;
    }

    private Page<ProjectDTO> handlePageProject(Page<ProjectDTO> page,
                                               Long organizationId,
                                               Boolean onlySucceed,
                                               Boolean isAdmin,
                                               Boolean isOrgAdmin,
                                               Long currentUserId,
                                               Long userId,
                                               PageRequest pageRequest) {
        if (CollectionUtils.isEmpty(page.getContent())) {
            return page;
        }
        //项目成员看不到未启用的
        List<ProjectDTO> projectDTOS = page.getContent().stream().filter(projectDTOVO -> {
            //如果是停用的项目
            if (!projectDTOVO.getEnabled()) {
                //如果用户的权限是项目成员，则过滤掉
                if (!isAdmin && !isOrgAdmin && !checkIsProjectOwner(currentUserId, projectDTOVO.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(projectDTOS)) {
            return new Page<>();
        }
        page.setContent(projectDTOS);
        // 添加额外信息
        addExtraInformation(projectDTOS, isAdmin, isOrgAdmin, organizationId, userId);
        if (onlySucceed) {
            List<ProjectDTO> dtos = projectDTOS.stream().filter(projectDTO1 -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(projectDTO1.getProjectStatus(), ProjectStatusEnum.SUCCESS.value())).collect(Collectors.toList());
            page.setContent(dtos);
        }
        if (!isAdmin && !isOrgAdmin) {
            page.setTotalElements(page.getTotalElements() - getDisableProjectByProjectMember(organizationId, currentUserId));
        }
        if (pageRequest.getSize() != 0) {
            int rawPage = (int) page.getTotalElements() / pageRequest.getSize();
            int remains = (int) page.getTotalElements() % pageRequest.getSize();
            page.setTotalPages(remains == 0 ? rawPage : rawPage + 1);
        } else {
            page.setTotalPages(1);
        }
        return page;
    }

    @Override
    public Boolean checkIsGitlabOwner(Long id, Long projectId, String level) {
        List<Role> roleC7nDTOList;
        if (ResourceLevel.PROJECT.value().equals(level)) {
            roleC7nDTOList = ConvertUtils.convertList(userC7nMapper.selectRolesByUidAndProjectId(id, projectId), Role.class);
        } else {
            ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
            roleC7nDTOList = roleC7nMapper.queryRolesInfoByUser(level, projectDTO.getOrganizationId(), id);
        }
        if (!CollectionUtils.isEmpty(roleC7nDTOList)) {
            List<String> labelNameLists = new ArrayList<>(labelC7nMapper.selectLabelNamesInRoleIds(roleC7nDTOList.stream().map(Role::getId).collect(Collectors.toSet())));
            if (ResourceLevel.PROJECT.value().equals(level)) {
                return labelNameLists.contains(RoleLabelEnum.GITLAB_OWNER.value());
            }
            if (ResourceLevel.ORGANIZATION.value().equals(level)) {
                return labelNameLists.contains(RoleLabelEnum.TENANT_ADMIN.value());
            }

        }
        return false;
    }

    @Override
    public Map<Long, Boolean> checkUsersAreGitlabProjectOwner(Set<Long> ids, Long projectId, String level) {
        if (CollectionUtils.isEmpty(ids)) {
            return new HashMap<>();
        }
        Map<Long, List<io.choerodon.iam.api.vo.RoleVO>> c7nRoleMap;
        if (ResourceLevel.PROJECT.value().equals(level)) {
            c7nRoleMap = userC7nMapper.selectRolesByUidsAndProjectId(ids, projectId).stream().collect(Collectors.groupingBy(io.choerodon.iam.api.vo.RoleVO::getUserId));
        } else {
            ProjectDTO projectDTO = projectAssertHelper.projectNotExisted(projectId);
            c7nRoleMap = roleC7nMapper.queryRolesInfoByUserIds(level, projectDTO.getOrganizationId(), ids).stream().collect(Collectors.groupingBy(io.choerodon.iam.api.vo.RoleVO::getUserId));
        }
        Set<Long> roleIds = c7nRoleMap.values().stream().flatMap(Collection::stream)
                .map(io.choerodon.iam.api.vo.RoleVO::getId)
                .collect(Collectors.toSet());
        Map<Long, List<String>> roleLabelsMap = labelC7nMapper.selectLabelNamesMapInRoleIds(roleIds).stream().collect(Collectors.groupingBy(LabelDTO::getRoleId, mapping(LabelDTO::getName, Collectors.toList())));
        Map<Long, Boolean> result = new HashMap<>();
        if (!CollectionUtils.isEmpty(c7nRoleMap)) {
            Set<Map.Entry<Long, List<io.choerodon.iam.api.vo.RoleVO>>> entries = c7nRoleMap.entrySet();
            for (Map.Entry<Long, List<io.choerodon.iam.api.vo.RoleVO>> entry : entries) {
                Set<Long> userRelatedRoleIds = entry.getValue().stream().map(io.choerodon.iam.api.vo.RoleVO::getId).collect(Collectors.toSet());
                List<String> labels = new ArrayList<>();
                userRelatedRoleIds.forEach(roleId -> labels.addAll(roleLabelsMap.get(roleId)));
                switch (level) {
                    case PROJECT:
                        result.put(entry.getKey(), labels.contains(RoleLabelEnum.GITLAB_OWNER.value()));
                        break;
                    case ORGANIZATION:
                        result.put(entry.getKey(), labels.contains(RoleLabelEnum.TENANT_ADMIN.value()));
                        break;
                }
            }
        }
        return result;
    }

    @Override
    public Boolean checkIsGitlabOwnerInOrgOrProject(Long projectId, Long userId) {
        String cacheKey = String.format(RedisCacheKeyConstants.IS_GITLAB_OWNER, projectId, userId);
        String cacheValue = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cacheValue != null) {
            return Boolean.valueOf(cacheValue);
        } else {
            ImmutableProjectInfoVO projectInfoVO = projectC7nService.queryImmutableProjectInfoById(projectId);
            // 查出用户在项目层和组织层的所有角色标签
            Set<String> labels = labelC7nMapper.selectRoleLabelsForUserInProjectAndOrg(userId, projectId, projectInfoVO.getTenantId());
            // 是否包含owner标签或者admin标签
            boolean result = labels.contains(RoleLabelEnum.GITLAB_OWNER.value()) || labels.contains(RoleLabelEnum.TENANT_ADMIN.value());
            // 10s过期
            stringRedisTemplate.opsForValue().set(cacheKey, String.valueOf(result), 10, TimeUnit.SECONDS);
            return result;
        }
    }

    @Override
    public Boolean checkIsProjectOwner(Long id, Long projectId) {
        Assert.notNull(projectId, ResourceCheckConstants.ERROR_PROJECT_IS_NULL);
        return userC7nMapper.doesUserHaveLabelInProject(id, RoleLabelEnum.PROJECT_ADMIN.value(), projectId);
    }

    @Override
    public Page<OrgAdministratorVO> pagingQueryOrgAdministrator(PageRequest pageable, Long organizationId,
                                                                String realName, String loginName, String params) {
        Page<UserDTO> userDTOPageInfo = PageHelper.doPageAndSort(pageable, () -> userC7nMapper.listOrgAdministrator(organizationId, realName, loginName, params));
        List<UserDTO> userDTOList = userDTOPageInfo.getContent();
        List<OrgAdministratorVO> orgAdministratorVOS = new ArrayList<>();
        Page<OrgAdministratorVO> pageInfo = new Page<>();
        BeanUtils.copyProperties(userDTOPageInfo, pageInfo);
        if (!CollectionUtils.isEmpty(userDTOList)) {
            userDTOList.forEach(user -> {
                OrgAdministratorVO orgAdministratorVO = new OrgAdministratorVO();
                orgAdministratorVO.setEnabled(user.getEnabled());
                orgAdministratorVO.setLocked(user.getLocked());
                orgAdministratorVO.setUserName(user.getRealName());
                orgAdministratorVO.setId(user.getId());
                orgAdministratorVO.setLoginName(user.getLoginName());
                orgAdministratorVO.setCreationDate(user.getCreationDate());
                orgAdministratorVO.setExternalUser(!organizationId.equals(user.getOrganizationId()));
                orgAdministratorVOS.add(orgAdministratorVO);
            });
            pageInfo.setContent(orgAdministratorVOS);
        }
        return pageInfo;
    }

    /**
     * 校验在启用用户中手机号唯一
     *
     * @param user 用户信息
     */
    private void checkPhone(User user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String phone = user.getPhone();
        User userDTO = new User();
        userDTO.setPhone(phone);
        userDTO.setEnabled(true);
        if (createCheck) {
            List<User> select = userMapper.select(userDTO);
            boolean existed = select != null && select.size() != 0;
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        } else {
            Long id = user.getId();
            User dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.phone.exist");
            }
        }
    }

    @Override
    public void checkEmail(User user) {
        boolean createCheck = StringUtils.isEmpty(user.getId());
        String email = user.getEmail();
        User userDTO = new User();
        userDTO.setEmail(email);
        if (createCheck) {
            boolean existed = userMapper.selectOne(userDTO) != null;
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        } else {
            Long id = user.getId();
            User dto = userMapper.selectOne(userDTO);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                throw new CommonException("error.user.email.existed");
            }
        }
    }

    @Override
    public List<User> listMarketAuditor() {
        return userC7nMapper.listMarketAuditor();
    }

    @Override
    public List<User> listRoot() {
        User user = new User();
        user.setAdmin(Boolean.TRUE);
        return userMapper.select(user);
    }

    private UserWithGitlabIdVO toUserWithGitlabIdDTO(User user, @Nullable Long gitlabUserId) {
        if (user == null) {
            return null;
        }
        UserWithGitlabIdVO userWithGitlabIdVO = new UserWithGitlabIdVO();
        BeanUtils.copyProperties(user, userWithGitlabIdVO);
        userWithGitlabIdVO.setGitlabUserId(gitlabUserId);
        return userWithGitlabIdVO;
    }

    @Override
    public Boolean checkIsOrgRoot(Long organizationId, Long userId) {
        return userC7nMapper.isOrgAdministrator(organizationId, userId);
    }


    @Override
    public User updateUserRoles(Long userId, String sourceType, Long sourceId, List<Role> roleList) {
        return updateUserRoles(userId, sourceType, sourceId, roleList, false);
    }

    @Override
    public User updateUserRoles(Long userId, String sourceType, Long sourceId, List<Role> roleList, Boolean syncAll) {
        UserValidator.validateUseRoles(roleList, true);
        User user = userAssertHelper.userNotExisted(userId);
        validateSourceNotExisted(sourceType, sourceId);
        createUserRoles(user, roleList, sourceType, sourceId, true, true, true, syncAll);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MemberRole> assignUsersRoles(String sourceType, Long sourceId, List<MemberRole> memberRoleDTOList) {
        validateSourceNotExisted(sourceType, sourceId);
//        // 校验组织人数是否已达上限
//        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
//            Set<Long> userIds = memberRoleDTOList.stream().map(MemberRole::getMemberId).collect(Collectors.toSet());
//            organizationResourceLimitService.checkEnableCreateUserOrThrowE(sourceId, userIds.size());
//        }
//        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
//            Set<Long> userIds = memberRoleDTOList.stream().map(MemberRole::getMemberId).collect(Collectors.toSet());
//            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(sourceId);
//            organizationResourceLimitService.checkEnableCreateUserOrThrowE(projectDTO.getOrganizationId(), userIds.size());
//        }
        memberRoleDTOList.forEach(memberRoleDTO -> {
            if (memberRoleDTO.getRoleId() == null || memberRoleDTO.getMemberId() == null) {
                throw new EmptyParamException("error.memberRole.insert.empty");
            }
            memberRoleDTO.setMemberType(MemberType.USER.value());
            memberRoleDTO.setSourceType(sourceType);
            memberRoleDTO.setSourceId(sourceId);
        });
        Map<Long, List<MemberRole>> memberRolesMap = memberRoleDTOList.stream().collect(Collectors.groupingBy(MemberRole::getMemberId));
        List<MemberRole> result = new ArrayList<>();
        memberRolesMap.forEach((memberId, memberRoleDTOS) -> result.addAll(roleMemberService.insertOrUpdateRolesOfUserByMemberId(false, sourceId, memberId, memberRoleDTOS, sourceType)));
        return result;
    }

    @Override
    public List<MemberRole> createUserRoles(User userDTO, List<Role> roleList, String sourceType, Long sourceId, boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable) {
        return createUserRoles(userDTO, roleList, sourceType, sourceId, isEdit, allowRoleEmpty, allowRoleDisable, false);
    }

    @Override
    public List<MemberRole> createUserRoles(User user, List<Role> roleDTOList, String sourceType, Long sourceId, boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable, Boolean syncAll) {
        Long userId = user.getId();
        List<MemberRole> memberRoleS = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleDTOList)) {
            List<Role> resultRoles = new ArrayList<>();
            for (Role role : roleDTOList) {
                Role existRole = roleAssertHelper.roleNotExisted(role.getId());
                RoleValidator.validateRole(sourceType, sourceId, role, allowRoleDisable);
                resultRoles.add(existRole);
                MemberRole memberRole = new MemberRole();
                memberRole.setMemberId(userId);
                memberRole.setMemberType(ResourceLevel.USER.value());
                memberRole.setSourceId(sourceId);
                memberRole.setSourceType(sourceType);
                memberRole.setRoleId(role.getId());
                memberRoleS.add(memberRole);
            }
            user.setRoles(resultRoles);
            memberRoleS = roleMemberService.insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, userId, memberRoleS, sourceType, syncAll);
        } else {
            // 如果允许用户角色为空 则清空当前用户角色
            if (allowRoleEmpty) {
                memberRoleS = roleMemberService.insertOrUpdateRolesOfUserByMemberId(isEdit, sourceId, userId, new ArrayList<>(), sourceType, syncAll);
            }
        }
        return memberRoleS;
    }


    public Long validateSourceNotExisted(String sourceType, Long sourceId) {
        Long tenantId = null;
        if (ResourceLevel.ORGANIZATION.value().equals(sourceType)) {
            organizationAssertHelper.notExisted(sourceId);
            tenantId = sourceId;
        }
        if (ResourceLevel.PROJECT.value().equals(sourceType)) {
            tenantId = projectAssertHelper.projectNotExisted(sourceId).getOrganizationId();
        }
        return tenantId;
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
            User user = userMapper.selectByPrimaryKey(id);
            user.setRealName(userName);
            updateInfo(user, false);
        }
        return userInfoDTO;
    }

    @Override
    public void selfUpdatePassword(Long userId, UserPasswordDTO userPasswordDTO, Boolean checkPassword, Boolean checkLogin) {
        if (checkLogin) {
            checkLoginUser(userId);
        }
        User user = userAssertHelper.userNotExisted(userId);
        if (user.getLdap()) {
            throw new CommonException("error.ldap.user.can.not.update.password");
        }
        if (!user.comparePassword(userPasswordDTO.getOriginalPassword())) {
            throw new CommonException("error.password.originalPassword");
        }

        String decryptPassword = encryptClient.decrypt(userPasswordDTO.getPassword());
        BasePasswordPolicy passwordPolicy = basePasswordPolicyRepository.selectPasswordPolicy(user.getOrganizationId());
        Long tenantId = passwordPolicy.getEnablePassword() ? user.getOrganizationId() : 0L;
        BaseUser baseUser = new BaseUser();
        BeanUtils.copyProperties(user, baseUser);
        baseUser.setPassword(decryptPassword);
        passwordPolicyManager.passwordValidate(decryptPassword, tenantId, baseUser);
        // 处理强制使用手机验证码校验 todo 先暂时跳过
//        userCaptchaService.validateForceCodeVerify(passwordPolicy, user, userPasswordDTO);
        userPasswordService.updateUserPassword(userId, userPasswordDTO.getPassword(), false);
        userSelfRepository.clearErrorTimes(user.getId());

        // send siteMsg
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userName", user.getRealName());
        List<Long> userIds = new ArrayList<>();
        userIds.add(user.getId());
        sendNotice(userIds, "modifyPassword", paramsMap, 0L, ResourceLevel.SITE);
        // 检查是否需要重新登录
        String accessToken = TokenUtils.getToken();
        if (BooleanUtils.isTrue(passwordPolicy.getEnablePassword())
                && BooleanUtils.isTrue(passwordPolicy.getLoginAgain())
                && org.apache.commons.lang3.StringUtils.isNotBlank(accessToken)) {
            oauthAdminFeignClient.invalidByToken(accessToken);
        }
    }


    @Override
    public User updateUserDisabled(Long userId) {
        User user = userAssertHelper.userNotExisted(userId);
        user.setEnabled(false);
        return updateSelective(user);
    }

    @Override
    public UserDTO queryByLoginName(String loginName) {
        return userC7nMapper.queryUserByLoginName(Objects.requireNonNull(loginName));
    }

    @Override
    public List<UserDTO> listUsersWithGitlabLabel(Long projectId, String labelName, RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        String param = Optional.ofNullable(roleAssignmentSearchDTO).map(dto -> ParamUtils.arrToStr(dto.getParam())).orElse(null);
        return ConvertUtils.convertList(userC7nMapper.listUsersWithGitlabLabel(projectId, labelName, roleAssignmentSearchDTO, param), UserDTO.class);
    }

    private void setProjectsIntoAndEditFlag(List<ProjectDTO> projects, boolean isAdmin, boolean isOrgAdmin, Set<Long> pids) {

        if (!CollectionUtils.isEmpty(projects)) {
            projects.forEach(p -> {
                p.setCategory(p.getCategories().get(0).getCode());
                // 如果项目为禁用 不可进入
                if (p.getEnabled() == null || !p.getEnabled()) {
                    p.setInto(false);
                } else {
                    // 如果不是admin用户和组织管理员且未分配项目角色 不可进入
                    if (!isAdmin && !isOrgAdmin && CollectionUtils.isEmpty(p.getRoles())) {
                        p.setInto(false);
                    }
                }

                // 计算用户是否有编辑权限
                if (isAdmin || isOrgAdmin || pids.contains(p.getId())) {
                    p.setEditFlag(true);
                }
            });
        }
    }

    private User updateSelective(User user) {
        userAssertHelper.objectVersionNumberNotNull(user.getObjectVersionNumber());
        if (userMapper.updateByPrimaryKeySelective(user) != 1) {
            throw new UpdateException("error.user.update");
        }
        return userMapper.selectByPrimaryKey(user);
    }

    @Override
    public UserVO selectSelf() {
        UserVO userVO;
        try {
            userVO = userRepository.selectSelf();
        } catch (CommonException e) {
            // 组织被禁用 清除token
            if (e.getCode().equals("hiam.warn.user.selfError")) {
                CustomUserDetails self = UserUtils.getUserDetails();
                User user = userRepository.selectByPrimaryKey(self.getUserId());
                oauthAdminFeignClient.invalidByUsername(user.getOrganizationId(), user.getLoginName());
            }
            throw e;
        }

        User user = userRepository.selectByPrimaryKey(userVO.getId());
        userVO.setObjectVersionNumber(user.getObjectVersionNumber());
        userVO.setAdmin(user.getAdmin());
        userVO.setLdap(user.getLdap());
        if (!user.getAdmin()) {
            List<Tenant> list = ConvertUtils.convertList(tenantRepository.selectSelfTenants(new TenantDTO(), new PageRequest()), Tenant.class);
            if (CollectionUtils.isEmpty(list)) {
                throw new CommonException("error.get.user.tenants");
            }
            userVO.setRecentAccessTenantList(list);
        }
        if (userVO.getLdap()) {
            userVO.setChangePasswordFlag(BaseConstants.Flag.NO);
        }
        return userVO;
    }

    @Override
    public List<User> listEnableUsersByName(String sourceType, Long sourceId, String userName, Boolean exactMatchFlag, Boolean organizationFlag) {
        Long tenantId = validateSourceNotExisted(sourceType, sourceId);
        List<Long> tenantIds = new ArrayList<>();
        tenantIds.add(tenantId);
        return userC7nMapper.listEnableUsersByName(sourceType, sourceId, userName, exactMatchFlag, tenantId, tenantIds);
    }

    @Override
    @Transactional
    @Saga(code = MEMBER_ROLE_UPDATE, description = "iam更新用户角色", inputSchemaClass = List.class)
    public void createOrgAdministrator(List<Long> userIds, Long organizationId) {
        Role tenantAdminRole = roleC7nService.getTenantAdminRole(organizationId);
        Tenant tenant = tenantMapper.selectByPrimaryKey(organizationId);
        List<User> notifyUserList = new ArrayList<>();

        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        Set<String> labelNames = new HashSet<>();
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
        userIds.forEach(id -> {
            Set<String> previousRoleLabels = roleC7nMapper.listLabelByTenantIdAndUserId(id, organizationId);
            labelNames.addAll(previousRoleLabels);
            List<MemberRole> memberRoleList = new ArrayList<>();
            MemberRole memberRoleDTO = new MemberRole();
            memberRoleDTO.setRoleId(tenantAdminRole.getId());
            memberRoleDTO.setMemberId(id);
            memberRoleDTO.setMemberType(MemberType.USER.value());
            memberRoleDTO.setSourceId(organizationId);
            memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRoleDTO.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRoleDTO.setAssignLevelValue(organizationId);
            memberRoleDTO.setAdditionalParams(additionalParams);
            memberRoleList.add(memberRoleDTO);

            memberRoleService.batchAssignMemberRoleInternal(memberRoleList);

            // 构建saga对象
            labelNames.add(RoleLabelEnum.TENANT_ADMIN.value());
            UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
            userMemberEventPayload.setUserId(id);
            userMemberEventPayload.setResourceId(organizationId);
            userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
            userMemberEventPayload.setRoleLabels(labelNames);
            userMemberEventPayload.setPreviousRoleLabels(previousRoleLabels);
            userMemberEventPayloads.add(userMemberEventPayload);
            // 构建消息对象
            User user = userMapper.selectByPrimaryKey(id);
            notifyUserList.add(user);

        });

        // 发送saga同步角色
        producer.apply(StartSagaBuilder.newBuilder()
                        .withRefId(String.valueOf(DetailsHelper.getUserDetails().getUserId()))
                        .withRefType("user")
                        .withSourceId(organizationId)
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withSagaCode(MEMBER_ROLE_UPDATE)
                        .withPayloadAndSerialize(userMemberEventPayloads),
                builder -> {
                });

        messageSendService.sendAddMemberMsg(tenant, "组织管理员", notifyUserList, DetailsHelper.getUserDetails().getUserId());
    }

    @Override
    public Page<SimplifiedUserVO> pagingQueryAllUser(PageRequest pageRequest, String param, Long organizationId) {
        if (StringUtils.isEmpty(param) && Long.valueOf(0).equals(organizationId)) {
            return new Page<>();
        }
        if (organizationId.equals(0L)) {
            return PageHelper.doPage(pageRequest, () -> userC7nMapper.selectAllUsersSimplifiedInfo(param));
        } else {
            return PageHelper.doPage(pageRequest, () -> userC7nMapper.selectUsersOptional(param, organizationId));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrgAdministrator(Long organizationId, Long userId) {
        Role tenantAdminRole = roleC7nService.getTenantAdminRole(organizationId);
        Long roleId = tenantAdminRole.getId();
        MemberRole memberRoleDTO = new MemberRole();
        memberRoleDTO.setRoleId(roleId);
        memberRoleDTO.setMemberId(userId);
        memberRoleDTO.setMemberType(MemberType.USER.value());
        memberRoleDTO.setSourceId(organizationId);
        memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
        if (CollectionUtils.isEmpty(memberRoleMapper.select(memberRoleDTO))) {
            throw new CommonException("error.memberRole.not.exist", roleId, userId);
        }
        if (memberRoleMapper.delete(memberRoleDTO) != 1) {
            throw new CommonException("error.memberRole.delete");
        }
        //删除组织管理员成功后也要发saga删除gitlab相应的权限。
        List<UserMemberEventPayload> userMemberEventPayloadList = new ArrayList<>();
        Set<String> labelNames = new HashSet<>();
        labelNames.add(RoleLabelEnum.TENANT_ADMIN.value());
        UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
        userMemberEventPayload.setUserId(userId);
        userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
        userMemberEventPayload.setResourceId(organizationId);
        userMemberEventPayload.setRoleLabels(labelNames);
        userMemberEventPayloadList.add(userMemberEventPayload);
        roleMemberService.deleteMemberRoleForSaga(userId, userMemberEventPayloadList, ResourceLevel.ORGANIZATION, organizationId);
    }

    @Override
    public void assignUsersRolesOnOrganizationLevel(Long organizationId, List<MemberRole> memberRoleDTOS) {
        Map<Long, Set<String>> userRoleLabelsMap = new HashMap<>();
        Map<Long, Set<String>> userOldRoleLabelsMap = new HashMap<>();
        Map<Long, List<User>> roleUsersMap = new HashMap<>();
        Map<Long, String> roleNameMap = new HashMap<>();
        Tenant tenant = tenantMapper.selectByPrimaryKey(organizationId);
        Map<String, Object> additionalParams = new HashMap<>();
        additionalParams.put(MemberRoleConstants.MEMBER_TYPE, MemberRoleConstants.MEMBER_TYPE_CHOERODON);
        memberRoleDTOS.forEach(memberRoleDTO -> {
            Set<String> previousRoleLabels = roleC7nMapper.listLabelByTenantIdAndUserId(memberRoleDTO.getMemberId(), organizationId);
            userOldRoleLabelsMap.put(memberRoleDTO.getMemberId(), previousRoleLabels);
            User user = userMapper.selectByPrimaryKey(memberRoleDTO.getMemberId());
            Role role = roleMapper.selectByPrimaryKey(memberRoleDTO.getRoleId());

            // 下面发送消息时需要
            if (roleNameMap.get(role.getId()) == null) {
                roleNameMap.put(role.getId(), role.getName());
            }
            if (user == null || role == null) {
                throw new EmptyParamException("error.memberRole.insert.empty");
            }

            // 构建saga对象
            List<LabelDTO> labelDTOS = labelC7nMapper.selectByRoleId(role.getId());
            if (!CollectionUtils.isEmpty(labelDTOS)) {
                Set<String> labelNames = labelDTOS.stream().map(Label::getName).collect(Collectors.toSet());
                Set<String> roleLabels = userRoleLabelsMap.get(memberRoleDTO.getMemberId());
                if (roleLabels != null) {
                    roleLabels.addAll(labelNames);
                } else {
                    userRoleLabelsMap.put(memberRoleDTO.getMemberId(), labelNames);
                }
            }
            // 构建消息对象
            List<User> userList = roleUsersMap.get(memberRoleDTO.getRoleId());
            if (userList != null) {
                userList.add(user);
            } else {
                userList = new ArrayList<>();
                userList.add(user);
                roleUsersMap.put(memberRoleDTO.getRoleId(), userList);
            }

            memberRoleDTO.setMemberType(MemberType.USER.value());
            memberRoleDTO.setSourceType(ResourceLevel.ORGANIZATION.value());
            memberRoleDTO.setSourceId(organizationId);
            memberRoleDTO.setAssignLevel(ResourceLevel.ORGANIZATION.value());
            memberRoleDTO.setAssignLevelValue(organizationId);
            memberRoleDTO.setAdditionalParams(additionalParams);
        });
        memberRoleService.batchAssignMemberRoleInternal(memberRoleDTOS);


        // 发送saga
        List<UserMemberEventPayload> userMemberEventPayloads = new ArrayList<>();
        userRoleLabelsMap.forEach((k, v) -> {
            UserMemberEventPayload userMemberEventPayload = new UserMemberEventPayload();
            userMemberEventPayload.setUserId(k);
            userMemberEventPayload.setRoleLabels(v);
            userMemberEventPayload.setPreviousRoleLabels(userOldRoleLabelsMap.get(k));
            userMemberEventPayload.setResourceId(organizationId);
            userMemberEventPayload.setResourceType(ResourceLevel.ORGANIZATION.value());
            userMemberEventPayloads.add(userMemberEventPayload);

        });
        roleMemberService.updateMemberRole(DetailsHelper.getUserDetails().getUserId(), userMemberEventPayloads, ResourceLevel.ORGANIZATION, organizationId);
        Long operatorId = DetailsHelper.getUserDetails().getUserId();
        // 发送消息
        roleUsersMap.forEach((k, v) -> messageSendService.sendAddMemberMsg(tenant, roleNameMap.get(k), v, operatorId));

    }

    @Override
    public Boolean switchSite() {
        userDetailsService.storeUserTenant(TenantConstants.DEFAULT_TENANT_TD);
        UserVO userVO = userRepository.selectSelf();
        LOGGER.info("==========================switch site user {}", userVO.toString());
        if (userVO.getCurrentRoleLevel().equals(ResourceLevel.SITE.value())) {
            return true;
        }
        List<org.hzero.iam.domain.vo.RoleVO> roles = roleRepository.selectSelfCurrentTenantRoles(true);
        LOGGER.info("==========================switch site roles {}", roles.toString());
        List<String> rolesStr = roles.stream().map(RoleVO::getLevel).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(roles) || !rolesStr.contains(ResourceLevel.SITE.value())) {
            LOGGER.warn("error.without.site.role");
            return false;
        }
        for (RoleVO t : roles) {
            if (t.getLevel().equals(ResourceLevel.SITE.value())) {
                userDetailsService.storeUserRole(t.getId());
                break;
            }
        }
        return true;
    }

    @Override
    public List<User> listUsersByRealNames(Set<String> realNames, Boolean onlyEnabled) {
        if (ObjectUtils.isEmpty(realNames)) {
            return new ArrayList<>();
        } else {
            return userC7nMapper.listUsersByRealNames(realNames, onlyEnabled);
        }
    }

    @Override
    public List<UserDTO> pagingQueryUsersByRoleIdOnSiteLevel(Long roleId) {
        return userC7nMapper.listSiteUsersByRoleId(roleId);
    }

    @Override
    public List<UserDTO> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long sourceId) {

        return userC7nMapper.listOrgUsersByRoleId(roleId, sourceId);
    }

    @Override
    public List<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long sourceId) {
        return userC7nMapper.listProjectUsersByRoleId(roleId, sourceId);
    }

    @Override
    public List<UserDTO> listUsersUnderRoleByIds(Long projectId, String roleIdString) {
        if (ObjectUtils.isEmpty(roleIdString)) {
            return Collections.emptyList();
        }
        String[] ids = roleIdString.split(",");
        if (ids.length == 0) {
            return Collections.emptyList();
        }
        List<Long> roleIds = new ArrayList<>();
        for (String str : ids) {
            if (org.apache.commons.lang3.StringUtils.isNumeric(str)) {
                roleIds.add(Long.parseLong(str));
            } else {
                if (EncryptContext.isEncrypt()) {
                    roleIds.add(Long.parseLong(encryptionService.decrypt(str, "")));
                } else {
                    throw new CommonException("error.illegal.role.id");
                }
            }
        }
        return userC7nMapper.listUsersUnderRoleByIds(projectId, roleIds);
    }

    @Override
    public RegistrantInfoDTO queryRegistrantInfoAndAdmin(String orgCode) {
        Tenant record = new Tenant();
        record.setTenantNum(orgCode);
        Tenant tenant = tenantMapper.selectOne(record);

        TenantConfig confiRecord = new TenantConfig();
        confiRecord.setTenantId(tenant.getTenantId());
        Long userId = null;
        List<TenantConfig> tenantConfigs = tenantConfigMapper.select(confiRecord);

        for (TenantConfig tenantConfig : tenantConfigs) {
            if ("userId".equalsIgnoreCase(tenantConfig.getConfigKey())) {
                userId = Long.valueOf(tenantConfig.getConfigValue());
            }
        }

        User user = userMapper.selectByPrimaryKey(userId);

        User adminUser = new User();
        adminUser.setLoginName("admin");
        adminUser = userMapper.selectOne(adminUser);

        RegistrantInfoDTO registrantInfoDTO = new RegistrantInfoDTO();
        registrantInfoDTO.setUser(user);
        registrantInfoDTO.setOrganizationName(tenant.getTenantName());
        registrantInfoDTO.setAdminId(adminUser.getId());
        return registrantInfoDTO;
    }

    @Override
    public UserDTO queryPersonalInfo() {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        Long userId = customUserDetails.getUserId();
        UserDTO userDTO = userC7nMapper.queryPersonalInfo(userId);
        return userDTO;
    }

    @Override
    public WebHookUser getWebHookUser(Long userId) {
        User userDTO = userRepository.selectByPrimaryKey(userId);
        if (ObjectUtils.isEmpty(userDTO) || StringUtils.isEmpty(userDTO.getLoginName())) {
            return new WebHookUser("0", "unknown");
        } else {
            return new WebHookUser(userDTO.getLoginName(), userDTO.getRealName());
        }
    }

    @Override
    public List<UserProjectLabelVO> listRoleLabelsForUserInTheProject(Long userId, Set<Long> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return Collections.emptyList();
        }
        return userC7nMapper.listRoleLabelsForUserInTheProject(userId, projectIds);
    }

    @Override
    public Page<ProjectDTO> pagingProjectsByUserId(Long organizationId, Long userId, ProjectSearchVO projectSearchVO, PageRequest pageable, Boolean onlySucceed) {
        Page<ProjectDTO> page = new Page<>();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        boolean isAdmin = userDetails == null ? isRoot(userId) : Boolean.TRUE.equals(userDetails.getAdmin());
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        Long currentUserId = userDetails == null ? userId : userDetails.getUserId();

        List<String> sortOrderStrings = getSortOrderStrings(pageable);

        //查询到的项目包括已启用和未启用的
        page = PageHelper.doPage(pageable, () -> projectMapper.selectProjectsByUserIdOrAdmin(organizationId, userId, projectSearchVO, isAdmin, isOrgAdmin,sortOrderStrings));
        // 过滤项目类型
        List<ProjectDTO> projects = page.getContent();
        if (CollectionUtils.isEmpty(projects)) {
            return page;
        }
        //项目成员看不到未启用的
        List<ProjectDTO> projectDTOS = page.getContent().stream().filter(projectDTOVO -> {
            //如果是停用的项目
            if (!projectDTOVO.getEnabled()) {
                //如果用户的权限是项目成员，则过滤掉
                if (!isAdmin && !isOrgAdmin && !checkIsProjectOwner(currentUserId, projectDTOVO.getId())) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(projectDTOS)) {
            return new Page<>();
        }
        page.setContent(projectDTOS);
        // 添加额外信息
        addExtraInformation(projectDTOS, isAdmin, isOrgAdmin, organizationId, userId);
        if (onlySucceed) {
            List<ProjectDTO> dtos = projectDTOS.stream().filter(projectDTO1 -> org.apache.commons.lang3.StringUtils.equalsIgnoreCase(projectDTO1.getProjectStatus(), ProjectStatusEnum.SUCCESS.value())).collect(Collectors.toList());
            page.setContent(dtos);
        }
        //TotalElements=page.getTotal-组织下停用且用户是项目成员角色的项目数量
        if (!isAdmin && !isOrgAdmin) {
            page.setTotalElements(page.getTotalElements() - getDisableProjectByProjectMember(organizationId, currentUserId));
        }
        if (pageable.getSize() != 0) {
            int rawPage = (int) page.getTotalElements() / pageable.getSize();
            int remains = (int) page.getTotalElements() % pageable.getSize();
            page.setTotalPages(remains == 0 ? rawPage : rawPage + 1);
        } else {
            page.setTotalPages(1);
        }
        return page;
    }

    @Override
    public int getDisableProjectByProjectMember(Long tenantId, Long userId) {
        Integer integer = projectMapper.selectDisableProjectByProjectMember(tenantId, userId);
        if (Objects.isNull(integer)) {
            return 0;
        }
        return integer.intValue();
    }

    @Override
    public List<Long> queryCanAccessProjectIdsByUserId(Long organizationId, Long userId) {
        boolean isAdmin = isRoot(userId);
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        return projectMapper.selectProjectIdsByUserIdOrAdmin(organizationId, userId, isAdmin, isOrgAdmin);
    }

    @Override
    public List<ProjectDTO> listOwnedProjects(Long organizationId, Long userId) {
        boolean isAdmin = isRoot(userId);
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);


        return projectMapper.listOwnedProjects(organizationId, userId, isAdmin, isOrgAdmin);
    }

    private static String trimFileDirectory(String directory) {
        if (StringUtils.isEmpty(directory)) {
            return UUID.randomUUID().toString();
        }
        int directoryLength = directory.length();
        if (directoryLength < 60) {
            return directory;
        }
        int dotIndex = directory.indexOf(".");
        String suffix = directory.substring(dotIndex, directoryLength);
        String trimDirectory = directory.substring(0, 59 - suffix.length());
        return trimDirectory + suffix;
    }

    @Override
    public UserCountAllVO countAllUsers() {
        int count = userMapper.selectCount(new UserDTO());
        return new UserCountAllVO(count);
    }

    @Override
    public Set<Long> listAllUserIds() {
        return userC7nMapper.listAllUserIds();
    }

    @Override
    public Page<ProjectDTO> queryProjectsOfDevopsOrOperations(String projectName, PageRequest pageRequest) {
        Long userId;
        if (DetailsHelper.getUserDetails() != null) {
            userId = DetailsHelper.getUserDetails().getUserId();
        } else {
            userId = userAssertHelper.queryAnonymousUser().getId();
        }
        boolean isAdmin = isRoot(userId);

        CommonExAssertUtil.assertTrue(userId != null, "error.user.get");
        return PageHelper.doPage(pageRequest, () -> projectMapper.listProjectOfDevopsOrOperations(projectName, userId, isAdmin));
    }

    @Override
    public Boolean platformAdministrator() {
        if (DetailsHelper.getUserDetails().getAdmin()) {
            return Boolean.TRUE;
        } else {
            return userC7nMapper.platformAdministrator(DetailsHelper.getUserDetails().getUserId());
        }
    }

    @Override
    public List<Tenant> adminOrgList() {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return userC7nMapper.adminOrgList(userId);
    }

    @Override
    public List<User> listSiteAdministrator() {
        return userC7nMapper.listSiteAdministrator();
    }

    @Override
    public Boolean selectUserPhoneBind() {
        return userC7nMapper.queryPhoneBind(DetailsHelper.getUserDetails().getUserId());
    }

    @Override
    public Boolean checkIsOrgAdmin(Long organizationId) {
        if (DetailsHelper.getUserDetails().getAdmin()) {
            return Boolean.TRUE;
        } else {
            return userC7nMapper.isOrgAdministrator(organizationId, DetailsHelper.getUserDetails().getUserId());
        }
    }

    @Override
    public Page<ProjectDTO> pageOwnedProjects(Long organizationId, Long currentProjectId, Long userId, PageRequest pageRequest, String param) {
        boolean isAdmin = isRoot(userId);
        boolean isOrgAdmin = checkIsOrgRoot(organizationId, userId);
        return PageHelper.doPage(pageRequest, () -> projectMapper.pageOwnedProjects(organizationId, currentProjectId, userId, isAdmin, isOrgAdmin, param));
    }

    public static List<String> getSortOrderStrings(PageRequest pageable) {
        List<String> sortOrderStrings = new ArrayList<>();
        Sort pageSort = pageable.getSort();
        if (pageSort == null) {
            String statusOrder = "is_enabled desc";
            String creationDateOrder = "creation_date desc";
            sortOrderStrings.add(statusOrder);
            sortOrderStrings.add(creationDateOrder);
            return sortOrderStrings;
        } else {
            for (Sort.Order order : pageSort) {
                if (order.getProperty().equals("program_id")) {
                    String orderString = "fp2.id" + " " + order.getDirection().name();
                    sortOrderStrings.add(orderString);
                } else {
                    String orderString = order.getProperty() + " " + order.getDirection().name();
                    sortOrderStrings.add(orderString);
                }
            }
            return sortOrderStrings;
        }
    }
}
