package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.constant.ProjectVisitInfoConstants.PROJECT_VISIT_INFO_KEY_TEMPLATE;
import static io.choerodon.iam.infra.constant.ProjectVisitInfoConstants.USER_VISIT_INFO_KEY_TEMPLATE;
import static io.choerodon.iam.infra.utils.SagaTopic.Project.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.FeignException;
import io.choerodon.core.exception.ext.EmptyParamException;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.utils.ConvertUtils;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.asserts.DetailsHelperAssert;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.MisConstants;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectPermissionDTO;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.enums.ProjectCategoryEnum;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.enums.SendSettingBaseEnum;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.feign.operator.DevopsFeignClientOperator;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.utils.CommonExAssertUtil;
import io.choerodon.iam.infra.utils.DateUtil;
import io.choerodon.iam.infra.utils.JsonHelper;
import io.choerodon.iam.infra.valitador.ProjectValidator;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * @author scp
 * @since 2020/4/15
 */
@Service
public class OrganizationProjectC7nServiceImpl implements OrganizationProjectC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationProjectC7nServiceImpl.class);

    private static final String ERROR_PROJECT_NOT_EXIST = "error.project.not.exist";
    private static final String ERROR_PROJECT_CATEGORY_EMPTY = "error.project.category.empty";
    public static final String PROJECT = "project";
    public static final String ERROR_ORGANIZATION_PROJECT_NUM_MAX = "error.organization.project.num.max";
    //saga的状态
    private static final String FAILED = "FAILED";
    private static final String RUNNING = "RUNNING";

    @Value("${spring.application.name:default}")
    private String serviceName;

    @Value("${choerodon.category.enabled:false}")
    private Boolean categoryEnable;

    private SagaClient sagaClient;

    private UserC7nService userC7nService;

    private AsgardFeignClient asgardFeignClient;

    private DevopsFeignClientOperator devopsFeignClientOperator;

    private ProjectMapCategoryMapper projectMapCategoryMapper;

    private ProjectMapper projectMapper;

    private ProjectCategoryMapper projectCategoryMapper;

    private LabelC7nMapper labelC7nMapper;

    private RoleC7nMapper roleC7nMapper;

    private ProjectAssertHelper projectAssertHelper;

    private OrganizationAssertHelper organizationAssertHelper;

    private UserAssertHelper userAssertHelper;


    private final ObjectMapper mapper = new ObjectMapper();

    private ProjectValidator projectValidator;

    private TransactionalProducer producer;
    private C7nTenantConfigService c7nTenantConfigService;

    private OrganizationResourceLimitService organizationResourceLimitService;

    private ProjectPermissionService projectPermissionService;

    private MessageSendService messageSendService;

    private RedisTemplate<String, String> redisTemplate;

    private StarProjectService starProjectService;

    @Autowired
    @Lazy
    private ProjectC7nService projectC7nService;
    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;
    @Autowired
    @Lazy
    private ProjectCategoryC7nService projectCategoryC7nService;

    public OrganizationProjectC7nServiceImpl(SagaClient sagaClient,
                                             ProjectMapCategoryMapper projectMapCategoryMapper,
                                             ProjectMapper projectMapper,
                                             ProjectAssertHelper projectAssertHelper,
                                             ProjectCategoryMapper projectCategoryMapper,
                                             OrganizationAssertHelper organizationAssertHelper,
                                             UserAssertHelper userAssertHelper,
                                             ProjectValidator projectValidator,
                                             TransactionalProducer producer,
                                             DevopsFeignClientOperator devopsFeignClientOperator,
                                             @Lazy
                                                     UserC7nService userC7nService,
                                             LabelC7nMapper labelC7nMapper,
                                             RoleC7nMapper roleC7nMapper,
                                             C7nTenantConfigService c7nTenantConfigService,
                                             @Lazy ProjectPermissionService projectPermissionService,
                                             OrganizationResourceLimitService organizationResourceLimitService,
                                             AsgardFeignClient asgardFeignClient,
                                             @Lazy
                                                     MessageSendService messageSendService,
                                             RedisTemplate<String, String> redisTemplate,
                                             @Lazy StarProjectService starProjectService
    ) {
        this.starProjectService = starProjectService;
        this.redisTemplate = redisTemplate;
        this.sagaClient = sagaClient;
        this.userC7nService = userC7nService;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.organizationAssertHelper = organizationAssertHelper;
        this.projectCategoryMapper = projectCategoryMapper;
        this.userAssertHelper = userAssertHelper;
        this.projectValidator = projectValidator;
        this.producer = producer;
        this.devopsFeignClientOperator = devopsFeignClientOperator;
        this.organizationResourceLimitService = organizationResourceLimitService;
        this.c7nTenantConfigService = c7nTenantConfigService;
        this.labelC7nMapper = labelC7nMapper;
        this.projectPermissionService = projectPermissionService;
        this.roleC7nMapper = roleC7nMapper;
        this.asgardFeignClient = asgardFeignClient;
        this.messageSendService = messageSendService;
    }


    @Override
    @Saga(code = PROJECT_CREATE, description = "iam创建项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO createProject(Long organizationId, ProjectDTO projectDTO) {
        organizationResourceLimitService.checkEnableCreateProjectOrThrowE(organizationId);
        organizationResourceLimitService.checkEnableCreateProjectType(organizationId, projectDTO);
        projectValidator.validateProjectCategory(projectDTO.getCategories());
        Boolean enabled = projectDTO.getEnabled();
        projectDTO.setEnabled(enabled == null || enabled);
        ProjectDTO res = create(projectDTO);
        res.setCategories(projectDTO.getCategories());
        res.setUseTemplate(projectDTO.getUseTemplate());
        insertProjectMapCategory(projectDTO.getCategories(), res.getId());
        res = sendCreateProjectEvent(res);
        try {
            User user;
            if (DetailsHelper.getUserDetails() != null && DetailsHelper.getUserDetails().getUserId() != 0) {
                user = userC7nService.queryInfo(DetailsHelper.getUserDetails().getUserId());
            } else {
                user = userAssertHelper.queryAnonymousUser();
            }
            //创建项目成功发送webhook
            Map<String, String> params = new HashMap<>();
            params.put("projectId", String.valueOf(res.getId()));
            params.put("name", res.getName());
            params.put("code", res.getCode());
            params.put("organizationId", String.valueOf(res.getOrganizationId()));
            params.put("enabled", String.valueOf(res.getEnabled()));
            params.put("category", res.getCategory());
            params.put("loginName", user.getLoginName());
            params.put("userName", user.getRealName());
            userC7nService.sendNotice(Arrays.asList(res.getCreatedBy()), SendSettingBaseEnum.CREATE_PROJECT.value(), params, res.getOrganizationId(), ResourceLevel.ORGANIZATION);
        } catch (Exception e) {
            LOGGER.error("error.send.message", e);
        }
        return res;
    }


    @Override
    public ProjectDTO create(ProjectDTO projectDTO) {
        Long organizationId = projectDTO.getOrganizationId();
        organizationAssertHelper.notExisted(organizationId);
        projectAssertHelper.codeExisted(projectDTO.getCode(), organizationId);
        projectDTO.setEnabled(Boolean.TRUE);
        projectDTO.setBeforeCategory(projectDTO.getCategories().stream().map(ProjectCategoryDTO::getCode).collect(Collectors.joining(",")));
        if (projectMapper.insertSelective(projectDTO) != 1) {
            throw new CommonException("error.project.create");
        }
        return projectMapper.selectByPrimaryKey(projectDTO);
    }


    private void insertProjectMapCategory(List<ProjectCategoryDTO> projectCategoryVOS, Long projectId) {
        projectCategoryVOS.forEach(projectCategoryVO -> {
            ProjectMapCategoryDTO example = new ProjectMapCategoryDTO();
            example.setCategoryId(projectCategoryVO.getId());
            example.setProjectId(projectId);
            if (projectMapCategoryMapper.insertSelective(example) != 1) {
                throw new InsertException("error.project.map.category.insert");
            }
        });
    }

    private ProjectDTO sendCreateProjectEvent(ProjectDTO projectDTO) {
        return producer.applyAndReturn(
                StartSagaBuilder
                        .newBuilder()
                        .withLevel(ResourceLevel.ORGANIZATION)
                        .withRefType(PROJECT)
                        .withSagaCode(PROJECT_CREATE)
                        .withSourceId(projectDTO.getOrganizationId()),
                builder -> {
                    Set<String> roleLabels = initMemberRole(projectDTO);
                    ProjectEventPayload projectEventPayload = generateProjectEventMsg(projectDTO, roleLabels, projectDTO.getCategories());
                    builder
                            .withPayloadAndSerialize(projectEventPayload)
                            .withRefId(String.valueOf(projectDTO.getId()))
                            .withSourceId(projectDTO.getOrganizationId());
                    return projectDTO;
                });
    }

    private ProjectEventPayload generateProjectEventMsg(ProjectDTO projectDTO, Set<String> roleLabels, List<ProjectCategoryDTO> projectCategoryDTOS) {
        ProjectEventPayload projectEventMsg = new ProjectEventPayload();
        CustomUserDetails details = DetailsHelper.getUserDetails();
        Tenant tenant = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
        if (details != null && details.getUserId() != 0) {
            projectEventMsg.setUserName(details.getUsername());
            projectEventMsg.setUserId(details.getUserId());
        } else {
            User user = userAssertHelper.queryAnonymousUser();
            projectEventMsg.setUserId(user.getId());
            projectEventMsg.setUserName(user.getRealName());
        }
        projectEventMsg.setRoleLabels(roleLabels);
        projectEventMsg.setProjectId(projectDTO.getId());
        projectEventMsg.setProjectCode(projectDTO.getCode());
        projectEventMsg.setProjectCategoryVOS(ConvertUtils.convertList(projectCategoryDTOS, ProjectCategoryVO.class));
        projectEventMsg.setProjectName(projectDTO.getName());
        projectEventMsg.setImageUrl(projectDTO.getImageUrl());
        projectEventMsg.setOrganizationCode(tenant.getTenantNum());
        projectEventMsg.setOrganizationName(tenant.getTenantName());
        projectEventMsg.setOrganizationId(tenant.getTenantId());
        projectEventMsg.setUseTemplate(projectDTO.getUseTemplate());
        return projectEventMsg;
    }

    private Set<String> initMemberRole(ProjectDTO project) {
        // 查出项目所有者角色
        List<Role> roles = roleC7nMapper.getByTenantIdAndLabel(project.getOrganizationId(), RoleLabelEnum.PROJECT_ADMIN.value());
        if (roles.isEmpty()) {
            throw new CommonException("error.role.not.found.by.label", RoleLabelEnum.PROJECT_ADMIN.value(), "role");
        }
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (customUserDetails == null) {
            throw new CommonException("error.user.not.login");
        }
        if (customUserDetails.getClientId() != null && StringUtils.isEmpty(customUserDetails.getRealName())) {
            LOGGER.info("The client creation project does not assign roles by default!");
            return new HashSet<>();
        }
        Long projectId = project.getId();
        Long userId = customUserDetails.getUserId();
        // 为创建者分配项目层的角色关系
        projectPermissionService.assignProjectUserRolesInternal(projectId, roles.stream().map(role -> new ProjectPermissionDTO(userId, projectId, role.getId())).collect(Collectors.toList()));

        // 查出来的符合要求的角色，要拿出来所有的label，发送给devops处理
        return labelC7nMapper.selectLabelNamesInRoleIds(roles.stream().map(Role::getId).collect(Collectors.toSet()));
    }

    @Transactional(rollbackFor = CommonException.class)
    @Override
    public ProjectDTO update(Long organizationId, ProjectDTO projectDTO) {
        ProjectDTO projectToUpdate = projectMapper.selectByPrimaryKey(projectDTO.getId());
        CommonExAssertUtil.assertTrue(organizationId.equals(projectToUpdate.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        updateCheck(projectDTO);
        // 项目编码、类型不可编辑
        projectDTO.setCode(null);
        projectDTO.setCategory(null);
        ProjectDTO projectRecord = projectMapper.selectByPrimaryKey(projectDTO.getId());
        Tenant organizationDTO = organizationAssertHelper.notExisted(projectDTO.getOrganizationId());

        // 校验组织id是否和项目所属组织匹配
        if (!projectRecord.getOrganizationId().equals(organizationId)) {
            throw new CommonException(ResourceCheckConstants.ERROR_PARAM_IS_INVALID);
        }
        // 判断是否修改启停用状态
        boolean updateStatus = false;
        if (projectDTO.getEnabled() != null && !projectRecord.getEnabled().equals(projectDTO.getEnabled())) {
            updateStatus = true;
        }
        ProjectDTO dto;
        dto = new ProjectDTO();
        CustomUserDetails details = DetailsHelperAssert.userDetailNotExisted();
        User user = userAssertHelper.userNotExisted(UserAssertHelper.WhichColumn.LOGIN_NAME, details.getUsername());
        ProjectEventPayload projectEventMsg = new ProjectEventPayload();
        projectEventMsg.setUserName(details.getUsername());
        projectEventMsg.setUserId(user.getId());
        projectEventMsg.setOrganizationCode(organizationDTO.getTenantNum());
        projectEventMsg.setOrganizationName(organizationDTO.getTenantName());
        projectEventMsg.setOrganizationId(organizationDTO.getTenantId());


        //修改项目的类型  拿到项目的所有类型，查询已有的，判断是新增项目类型还是删除项目类型
        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setProjectId(projectDTO.getId());
        List<Long> dbProjectCategoryIds = projectMapCategoryMapper.select(projectMapCategoryDTO).stream().map(ProjectMapCategoryDTO::getCategoryId).collect(Collectors.toList());
        List<Long> projectCategoryIds = projectDTO.getCategories().stream().map(ProjectCategoryDTO::getId).collect(Collectors.toList());
        List<Long> deleteProjectCategoryIds = dbProjectCategoryIds.stream().filter(id -> !projectCategoryIds.contains(id)).collect(Collectors.toList());
        List<Long> addProjectCategoryIds = projectCategoryIds.stream().filter(id -> !dbProjectCategoryIds.contains(id)).collect(Collectors.toList());
        //真正插入项目了类型放到saga里面做
//        projectC7nService.addProjectCategory(projectDTO.getId(), addProjectCategoryIds);
        projectC7nService.deleteProjectCategory(projectDTO.getId(), deleteProjectCategoryIds);

        //增加项目类型的数据  这个之前存在的类型要从数据库中取，因为前端传的可能不准确。
        Set<String> beforeCode = new HashSet<>();
        if (!StringUtils.isEmpty(projectRecord.getBeforeCategory())) {
            beforeCode = Arrays.asList(projectRecord.getBeforeCategory().split(BaseConstants.Symbol.COMMA)).stream().collect(Collectors.toSet());
        }
        if (!CollectionUtils.isEmpty(addProjectCategoryIds)) {
            List<ProjectCategoryDTO> projectCategoryDTOS = projectCategoryMapper.selectByIds(org.apache.commons.lang3.StringUtils.join(addProjectCategoryIds, ","));
            if (!org.springframework.util.CollectionUtils.isEmpty(projectCategoryDTOS)) {
                projectEventMsg.setProjectCategoryVOS(ConvertUtils.convertList(projectCategoryDTOS, ProjectCategoryVO.class));
                Set<String> addCode = projectCategoryDTOS.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toSet());
                beforeCode.addAll(addCode);
            }
        }

        projectDTO.setBeforeCategory(beforeCode.stream().collect(Collectors.joining(",")));
        ProjectDTO newProjectDTO = updateSelective(projectDTO);


        projectEventMsg.setProjectId(newProjectDTO.getId());
        projectEventMsg.setProjectCode(newProjectDTO.getCode());
        projectEventMsg.setProjectName(newProjectDTO.getName());
        projectEventMsg.setImageUrl(newProjectDTO.getImageUrl());
        BeanUtils.copyProperties(newProjectDTO, dto);


        // 发送修改项目启停用状态消息
        if (updateStatus) {
            if (Boolean.TRUE.equals(projectDTO.getEnabled())) {
                updateProjectAndSendEvent(projectDTO.getId(), PROJECT_ENABLE, true, details.getUserId());
            } else if (Boolean.FALSE.equals(projectDTO.getEnabled())) {
                updateProjectAndSendEvent(projectDTO.getId(), PROJECT_DISABLE, false, details.getUserId());
            }

        }

        try {
            String input = mapper.writeValueAsString(projectEventMsg);
            sagaClient.startSaga(PROJECT_UPDATE, new StartInstanceDTO(input, PROJECT, newProjectDTO.getId() + "", ResourceLevel.ORGANIZATION.value(), organizationId));
        } catch (Exception e) {
            throw new CommonException("error.organizationProjectService.updateProject.event", e);
        }
        return dto;
    }

    @Override
    public ProjectDTO updateSelective(ProjectDTO projectDTO) {
        //可能只修改项目类型
        projectMapper.updateByPrimaryKeySelective(projectDTO);
        return projectMapper.selectByPrimaryKey(projectDTO.getId());
    }

    private void updateCheck(ProjectDTO projectDTO) {
        String name = projectDTO.getName();
        projectAssertHelper.objectVersionNumberNotNull(projectDTO.getObjectVersionNumber());
        if (StringUtils.isEmpty(name)) {
            throw new EmptyParamException("error.project.name.empty");
        }
        if (name.length() < 1 || name.length() > 32) {
            throw new IllegalArgumentException("error.project.name.size");
        }
        List<ProjectCategoryDTO> categories = projectDTO.getCategories();
        if (CollectionUtils.isEmpty(categories)) {
            throw new CommonException("error.choose.least.one.category");
        }
    }

    @Override
    @Saga(code = PROJECT_ENABLE, description = "iam启用项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO enableProject(Long organizationId, Long projectId, Long userId) {
        ProjectDTO projectToEnable = projectMapper.selectByPrimaryKey(projectId);
        CommonExAssertUtil.assertTrue(organizationId.equals(projectToEnable.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
        organizationAssertHelper.notExisted(organizationId);
        return updateProjectAndSendEvent(projectId, PROJECT_ENABLE, true, userId);
    }

    @Override
    @Saga(code = PROJECT_DISABLE, description = "iam停用项目", inputSchemaClass = ProjectEventPayload.class)
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO disableProject(Long organizationId, Long projectId, Long userId) {
        if (organizationId != null) {
            ProjectDTO projectToEnable = projectMapper.selectByPrimaryKey(projectId);
            CommonExAssertUtil.assertTrue(organizationId.equals(projectToEnable.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);
            organizationAssertHelper.notExisted(organizationId);
        }
        return updateProjectAndSendEvent(projectId, PROJECT_DISABLE, false, userId);
    }

    /**
     * 启用、禁用项目且发送相应通知消息.
     *
     * @param projectId    项目Id
     * @param consumerType saga消息类型
     * @param enabled      是否启用
     * @param userId       用户Id
     * @return 项目信息
     */
    private ProjectDTO updateProjectAndSendEvent(Long projectId, String consumerType, boolean enabled, Long userId) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        projectDTO.setEnabled(enabled);
        // 更新项目
        projectDTO = updateSelective(projectDTO);
        String category = selectCategoryByPrimaryKey(projectId).getCategory();
        projectDTO.setCategory(category);
        if (!ProjectCategoryEnum.contains(category)) {
            throw new CommonException("error.project.type");
        }
        // 发送通知消息
        sendEvent(consumerType, enabled, userId, null, projectDTO);
        return projectDTO;
    }

    /**
     * 启用、禁用项目时，发送相应通知消息.
     *
     * @param consumerType saga消息类型
     * @param enabled      是否启用
     * @param userId       用户Id
     * @param programId    项目群Id
     * @param projectDTO   项目DTO
     */
    private void sendEvent(String consumerType, boolean enabled, Long userId, Long programId, ProjectDTO projectDTO) {
        Long projectId = projectDTO.getId();
        ProjectEventPayload payload = new ProjectEventPayload();
        payload.setProjectId(projectId);
        payload.setProjectCategoryVOS(ConvertUtils.convertList(projectDTO.getCategories(), ProjectCategoryVO.class));
        payload.setProgramId(programId);
        //saga
        try {
            String input = mapper.writeValueAsString(payload);
            sagaClient.startSaga(consumerType, new StartInstanceDTO(input, PROJECT, "" + payload.getProjectId(), ResourceLevel.ORGANIZATION.value(), projectDTO.getOrganizationId()));
        } catch (Exception e) {
            throw new CommonException("error.organizationProjectService.enableOrDisableProject", e);
        }
        if (!enabled) {
            //给asgard发送禁用定时任务通知
            asgardFeignClient.disableProj(projectId);
        }
        messageSendService.sendDisableOrEnableProject(projectDTO, consumerType, enabled, userId);
    }

    @Override
    public Boolean check(ProjectDTO projectDTO) {
        boolean checkCode = !StringUtils.isEmpty(projectDTO.getCode());
        if (!checkCode) {
            return false;
        } else {
            return checkCode(projectDTO);
        }
    }

    private Boolean checkCode(ProjectDTO projectDTO) {
        boolean createCheck = StringUtils.isEmpty(projectDTO.getId());
        ProjectDTO project = new ProjectDTO();
        project.setOrganizationId(projectDTO.getOrganizationId());
        project.setCode(projectDTO.getCode());
        if (createCheck) {
            boolean existed = projectMapper.selectOne(project) != null;
            if (existed) {
                return false;
            }
        } else {
            Long id = projectDTO.getId();
            ProjectDTO dto = projectMapper.selectOne(project);
            boolean existed = dto != null && !id.equals(dto.getId());
            if (existed) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Map<String, Object> getProjectsByType(Long organizationId) {
        //1.获取所有类型
        List<ProjectCategoryDTO> list = projectCategoryMapper.selectAll();
        List<String> legend = list.stream().map(ProjectCategoryDTO::getName).collect(Collectors.toList());
        List<Map<String, Object>> data = new ArrayList<>();
        //2.获取类型下所有项目名
        list.forEach(type -> {
            List<String> projectNames = projectMapper.selectProjectNameByType(type.getCode(), organizationId);
            Map<String, Object> dataMap = new HashMap<>(5);
            dataMap.put("value", projectNames.size());
            dataMap.put("name", type.getName());
            dataMap.put("projects", projectNames);
            data.add(dataMap);
        });
        //3.获取无类型的所有项目名
        List<String> projsNoType = projectMapper.selectProjectNameNoType(organizationId);
        Map<String, Object> noTypeProjectList = new HashMap<>(5);
        noTypeProjectList.put("value", projsNoType.size());
        noTypeProjectList.put("name", "无");
        noTypeProjectList.put("projects", projsNoType);
        legend.add("无");
        data.add(noTypeProjectList);
        //4.构造返回map
        Map<String, Object> map = new HashMap<>(5);
        map.put("legend", legend);
        map.put("data", data);
        return map;
    }

    @Override
    public ProjectDTO selectCategoryByPrimaryKey(Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectCategoryByPrimaryKey(projectId);
        if (projectDTO == null) {
            throw new CommonException(ERROR_PROJECT_NOT_EXIST, projectId);
        }
        List<ProjectCategoryDTO> projectCategories = projectDTO.getCategories();
        if (CollectionUtils.isEmpty(projectCategories)) {
            throw new CommonException(ERROR_PROJECT_CATEGORY_EMPTY);
        }
        projectDTO.setCategory(projectCategories.get(0).getCode());
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> getAgileProjects(Long organizationId, String param) {
        List<ProjectDTO> projectDTOS;
        // todo 不清楚新逻辑，暂时返回组织下所有项目
//        if (categoryEnable) {
//            projectDTOS = projectMapper.selectByOrgIdAndCategoryEnable(organizationId, ProjectCategory.GENERAL.value(), param);
//        } else {
//            projectDTOS = projectMapper.selectByOrgIdAndCategory(organizationId, param);
//        }
        projectDTOS = projectMapper.selectByOrgIdAndCategory(organizationId, param);
        return projectDTOS;
    }

    @Override
    public ProjectDTO getProjectByOrgIdAndCode(Long organizationId, String code) {
        ProjectDTO projectQuery = new ProjectDTO();
        projectQuery.setOrganizationId(organizationId);
        projectQuery.setCode(code);
        return projectMapper.selectOne(projectQuery);
    }

    @Override
    public List<ProjectDTO> listProjectsByOrgId(Long organizationId) {
        ProjectDTO projectQuery = new ProjectDTO();
        projectQuery.setOrganizationId(organizationId);
        return projectMapper.select(projectQuery);
    }

    @Override
    public Page<ProjectDTO> pagingQuery(Long organizationId, PageRequest pageRequest, ProjectDTO projectDTO, String params) {
        int size = pageRequest.getSize();
        boolean doPage = (size != 0);
        String sortString = pageRequest.getSort() == null ? null : getSortStringForPageQuery(pageRequest.getSort());
        if (doPage) {
            return PageHelper.doPage(pageRequest, () -> projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));

        } else {
            Page<ProjectDTO> result = new Page<>();
            result.getContent().addAll(projectMapper.selectProjectsByOptions(organizationId, projectDTO, sortString, params));
            result.setSize(result.size());
            return result;
        }
    }

    @Override
    public BarLabelRotationVO countDeployRecords(Set<Long> projectIds, Date startTime, Date endTime) {
        BarLabelRotationVO barLabelRotationVO = new BarLabelRotationVO();

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate startDate = startTime.toInstant().atZone(zoneId).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(zoneId).toLocalDate();
        while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
            String date = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            barLabelRotationVO.getDateList().add(date);
            startDate = startDate.plusDays(1);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        projectIds.forEach(id -> {
            ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(id);
            BarLabelRotationItemVO labelRotationItemVO = devopsFeignClientOperator.countByDate(id, simpleDateFormat.format(startTime), simpleDateFormat.format(endTime));
            if (labelRotationItemVO != null) {
                labelRotationItemVO.setName(projectDTO.getName());
                labelRotationItemVO.setId(id);
                barLabelRotationVO.getProjectDataList().add(labelRotationItemVO);
            }
        });
        return barLabelRotationVO;
    }

    @Override
    public List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name) {
        return projectMapper.selectProjectsByOrgIdAndNameWithLimit(organizationId, name, 20);
    }

    private String getSortStringForPageQuery(Sort sort) {
        Iterator<Sort.Order> iterator = sort.iterator();

        List<String> list = new ArrayList<>();
        while (iterator.hasNext()) {
            Sort.Order t = iterator.next();
            String field;

            if ("name".equals(t.getProperty())) {
                field = "fp.name";
            } else if ("id".equals(t.getProperty())) {
                field = "fp.id";
            } else {
                throw new FeignException("error.field.not.supported.for.sort", t.getProperty());
            }
            list.add(field + " " + t.getDirection());
        }
        return String.join(",", list);
    }

    @Override
    public List<ProjectVisitInfoVO> queryLatestVisitProjectInfo(Long organizationId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        String userVisitInfoKey = String.format(USER_VISIT_INFO_KEY_TEMPLATE, userId, organizationId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(userVisitInfoKey);
        if (entries.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> projectVisitInfoJson = new ArrayList<>();
        entries.forEach((k, v) -> projectVisitInfoJson.add((String) v));

        String projectVisitInfosJson = "[" + String.join(",", projectVisitInfoJson) + "]";
        List<ProjectVisitInfoVO> projectVisitInfoVOList = JsonHelper.unmarshalByJackson(projectVisitInfosJson, new TypeReference<List<ProjectVisitInfoVO>>() {
        });
        Date now = new Date();
        List<String> fieldToDelete = new ArrayList<>();

        // 查出用户在该组织下可访问的项目(有项目权限并且项目处于启用状态)
        List<Long> canAccessProjectIds = userC7nService.queryCanAccessProjectIdsByUserId(organizationId, userId);

        List<ProjectVisitInfoVO> result = projectVisitInfoVOList.stream().peek(p -> {
            if (DateUtil.isExceedDay(p.getLastVisitTime(), now) || !canAccessProjectIds.contains(p.getProjectId())) {
                fieldToDelete.add(String.format(PROJECT_VISIT_INFO_KEY_TEMPLATE, p.getProjectId()));
            }
        }).filter(p -> !DateUtil.isExceedDay(p.getLastVisitTime(), now) && canAccessProjectIds.contains(p.getProjectId())).collect(Collectors.toList());

        // 将保存时间超过7天的记录删除
        if (!CollectionUtils.isEmpty(fieldToDelete)) {
            redisTemplate.opsForHash().delete(userVisitInfoKey, fieldToDelete.toArray(new Object[0]));
        }

        Set<Long> projectIds = result.stream().map(ProjectVisitInfoVO::getProjectId).collect(Collectors.toSet());
        // 如果记录为空，直接返回
        if (CollectionUtils.isEmpty(projectIds)) {
            return result;
        }
        List<ProjectDTO> projectDTOS = projectMapper.selectProjectWithCategoryByPrimaryKey(projectIds);
        //过滤项目类型
        projectCategoryC7nService.filterCategory(projectDTOS);
        List<Long> starProjectIds = starProjectService.listStarProjectIds(projectIds);
        projectDTOS.forEach(p -> {
            if (starProjectIds.contains(p.getId())) {
                p.setStarFlag(true);
            }
        });
        Map<Long, ProjectDTO> projectDTOMap = projectDTOS.stream()
                .collect(Collectors.toMap(ProjectDTO::getId, projectDTO -> projectDTO));
        result.forEach(r -> r.setProjectDTO(projectDTOMap.get(r.getProjectId())));
        //按照visit date 时间排序
        result = result.stream().sorted(Comparator.comparing(ProjectVisitInfoVO::getLastVisitTime).reversed()).collect(Collectors.toList());
        return result;
    }

    @Override
    public Page<ProjectDTO> listProjectsWithCategoryByOrgId(Long organizationId, ProjectSearchVO projectSearchVO, PageRequest pageRequest) {
        Page<ProjectDTO> pageAndSort = PageHelper.doPageAndSort(pageRequest, () -> projectMapper.selectWithCategory(organizationId, projectSearchVO));
        return pageAndSort;
    }
}
