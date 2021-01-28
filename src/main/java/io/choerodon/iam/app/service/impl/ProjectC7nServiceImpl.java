package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.PROJECT_UPDATE;

import java.util.*;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.thymeleaf.util.MapUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.core.utils.ConvertUtils;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.app.service.OrganizationProjectC7nService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.asserts.DetailsHelperAssert;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.enums.InstanceStatusEnum;
import io.choerodon.iam.infra.enums.ProjectOperatorTypeEnum;
import io.choerodon.iam.infra.enums.ProjectStatusEnum;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.operator.AgileFeignClientOperator;
import io.choerodon.iam.infra.feign.operator.AsgardServiceClientOperator;
import io.choerodon.iam.infra.mapper.*;
import io.choerodon.iam.infra.utils.SagaInstanceUtils;
import io.choerodon.iam.infra.utils.SagaTopic;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/15
 */
@Service
public class ProjectC7nServiceImpl implements ProjectC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectC7nServiceImpl.class);

    protected static final String ERROR_PROJECT_NOT_EXIST = "error.project.not.exist";
    protected static final String CATEGORY_REF_TYPE = "projectCategory";
    private static final String PROJECT = "project";
    private static final String DOCKER_REPO = "dockerRepo";
    //saga的状态
    private static final String FAILED = "FAILED";
    private static final String RUNNING = "RUNNING";
    private static final String COMPLETED = "COMPLETED";

    protected OrganizationProjectC7nService organizationProjectC7nService;

    @Value("${choerodon.category.enabled:false}")
    protected boolean enableCategory;

    @Value("${spring.application.name:default}")
    protected String serviceName;

    private final ObjectMapper mapper = new ObjectMapper();

    protected UserMapper userMapper;

    protected ProjectMapper projectMapper;
    protected ProjectAssertHelper projectAssertHelper;
    protected ProjectMapCategoryMapper projectMapCategoryMapper;
    protected UserAssertHelper userAssertHelper;
    protected OrganizationAssertHelper organizationAssertHelper;
    protected TenantMapper organizationMapper;
    protected AgileFeignClientOperator agileFeignClientOperator;
    protected ProjectPermissionMapper projectPermissionMapper;
    protected TransactionalProducer transactionalProducer;

    protected RoleC7nMapper roleC7nMapper;
    protected UserC7nService userC7nService;

    private ProjectCategoryMapper projectCategoryMapper;

    @Autowired
    private AsgardServiceClientOperator asgardServiceClientOperator;

    public ProjectC7nServiceImpl(OrganizationProjectC7nService organizationProjectC7nService,
                                 OrganizationAssertHelper organizationAssertHelper,
                                 UserMapper userMapper,
                                 ProjectMapper projectMapper,
                                 ProjectAssertHelper projectAssertHelper,
                                 ProjectMapCategoryMapper projectMapCategoryMapper,
                                 UserAssertHelper userAssertHelper,
                                 TenantMapper organizationMapper,
                                 AgileFeignClientOperator agileFeignClientOperator,
                                 TransactionalProducer transactionalProducer,
                                 ProjectPermissionMapper projectPermissionMapper,
                                 @Lazy
                                         UserC7nService userC7nService,
                                 RoleC7nMapper roleC7nMapper,
                                 ProjectCategoryMapper projectCategoryMapper) {
        this.organizationProjectC7nService = organizationProjectC7nService;
        this.organizationAssertHelper = organizationAssertHelper;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.userAssertHelper = userAssertHelper;
        this.organizationMapper = organizationMapper;
        this.agileFeignClientOperator = agileFeignClientOperator;
        this.projectPermissionMapper = projectPermissionMapper;
        this.transactionalProducer = transactionalProducer;
        this.roleC7nMapper = roleC7nMapper;
        this.userC7nService = userC7nService;
        this.projectCategoryMapper = projectCategoryMapper;
    }

    @Override
    public ProjectDTO queryProjectById(Long projectId, boolean withCategoryInfo, boolean withUserInfo, boolean withAgileInfo) {
        ProjectDTO dto = projectAssertHelper.projectNotExisted(projectId);
        if (withCategoryInfo) {
            if (enableCategory) {
                dto.setCategories(projectMapCategoryMapper.selectProjectCategoryNames(dto.getId()));
            }
        }
        if (withUserInfo) {
            User createdUser = userMapper.selectByPrimaryKey(dto.getCreatedBy());
            if (createdUser != null) {
                dto.setCreateUserName(createdUser.getRealName());
                dto.setCreateUserImageUrl(createdUser.getImageUrl());
            }
        }
        if (withAgileInfo) {
            try {
                AgileProjectInfoVO agileProjectResponse = agileFeignClientOperator.queryProjectInfoByProjectId(projectId);
                dto.setAgileProjectId(agileProjectResponse.getInfoId());
                dto.setAgileProjectCode(agileProjectResponse.getProjectCode());
                dto.setAgileProjectObjectVersionNumber(agileProjectResponse.getObjectVersionNumber());
            } catch (Exception e) {
                LOGGER.warn("agile feign invoke exception: {}", e.getMessage());
            }
        }
        return dto;
    }


    @Transactional(rollbackFor = CommonException.class)
    @Override
    @Saga(code = PROJECT_UPDATE, description = "iam更新项目", inputSchemaClass = ProjectEventPayload.class)
    public ProjectDTO update(ProjectDTO projectDTO) {
        ProjectDTO dto = new ProjectDTO();
        CustomUserDetails details = DetailsHelperAssert.userDetailNotExisted();
        User user = userAssertHelper.userNotExisted(UserAssertHelper.WhichColumn.LOGIN_NAME, details.getUsername());
        ProjectDTO newProject = projectAssertHelper.projectNotExisted(projectDTO.getId());

        Tenant tenant = organizationMapper.selectByPrimaryKey(newProject.getOrganizationId());
        ProjectEventPayload projectEventMsg = new ProjectEventPayload();
        projectEventMsg.setUserName(details.getUsername());
        projectEventMsg.setUserId(user.getId());
        if (tenant != null) {
            projectEventMsg.setOrganizationCode(tenant.getTenantNum());
            projectEventMsg.setOrganizationName(tenant.getTenantName());
        }

        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setProjectId(projectDTO.getId());
        List<Long> dbProjectCategoryIds = projectMapCategoryMapper.select(projectMapCategoryDTO).stream().map(ProjectMapCategoryDTO::getCategoryId).collect(Collectors.toList());
        List<Long> projectCategoryIds = projectDTO.getCategories().stream().map(ProjectCategoryDTO::getId).collect(Collectors.toList());
        List<Long> deleteProjectCategoryIds = dbProjectCategoryIds.stream().filter(id -> !projectCategoryIds.contains(id)).collect(Collectors.toList());
        List<Long> addProjectCategoryIds = projectCategoryIds.stream().filter(id -> !dbProjectCategoryIds.contains(id)).collect(Collectors.toList());
        deleteProjectCategory(projectDTO.getId(), deleteProjectCategoryIds);

        //增加项目类型的数据  这个之前存在的类型要从数据库中取，因为前端传的可能不准确。
        Set<String> beforeCode = new HashSet<>();
        if (!org.springframework.util.StringUtils.isEmpty(newProject.getBeforeCategory())) {
            beforeCode = Arrays.asList(newProject.getBeforeCategory().split(BaseConstants.Symbol.COMMA)).stream().collect(Collectors.toSet());
        }
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(addProjectCategoryIds)) {
            List<ProjectCategoryDTO> projectCategoryDTOS = projectCategoryMapper.selectByIds(org.apache.commons.lang3.StringUtils.join(addProjectCategoryIds, ","));
            if (!org.springframework.util.CollectionUtils.isEmpty(projectCategoryDTOS)) {
                projectEventMsg.setProjectCategoryVOS(ConvertUtils.convertList(projectCategoryDTOS, ProjectCategoryVO.class));
                Set<String> addCode = projectCategoryDTOS.stream().map(ProjectCategoryDTO::getCode).collect(Collectors.toSet());
                beforeCode.addAll(addCode);
            }
        }

        projectDTO.setBeforeCategory(beforeCode.stream().collect(Collectors.joining(",")));

        projectEventMsg.setProjectId(newProject.getId());
        projectEventMsg.setProjectCode(newProject.getCode());
        projectEventMsg.setProjectName(projectDTO.getName());
        projectEventMsg.setImageUrl(newProject.getImageUrl());
        projectEventMsg.setAgileProjectCode(projectDTO.getAgileProjectCode());

        try {
            String input = mapper.writeValueAsString(projectEventMsg);
            transactionalProducer.apply(StartSagaBuilder.newBuilder()
                            .withRefId(String.valueOf(projectDTO.getId()))
                            .withRefType(ResourceLevel.PROJECT.value())
                            .withSagaCode(PROJECT_UPDATE)
                            .withLevel(ResourceLevel.PROJECT)
                            .withSourceId(projectDTO.getId())
                            .withJson(input),
                    builder -> {
                        ProjectDTO newDTO = organizationProjectC7nService.updateSelective(projectDTO);
                        BeanUtils.copyProperties(newDTO, dto);
                    });
        } catch (Exception e) {
            throw new CommonException("error.projectService.update.event", e);
        }
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO disableProject(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return organizationProjectC7nService.disableProject(null, projectId, userId);
    }

    @Override
    public List<Long> listUserIds(Long projectId) {
        return projectMapper.listUserIds(projectId);
    }

    @Override
    public List<ProjectDTO> queryByIds(Set<Long> ids) {
        if (ids.isEmpty()) {
            return new ArrayList<>();
        } else {
            return projectMapper.selectByProjectIds(ids);
        }
    }

    @Override
    public List<Long> getProListByName(String name) {
        return projectMapper.getProListByName(name);
    }

    @Override
    public Tenant getOrganizationByProjectId(Long projectId) {
        ProjectDTO projectDTO = checkNotExistAndGet(projectId);
        return organizationAssertHelper.notExisted(projectDTO.getOrganizationId());
    }

    @Override
    public ProjectDTO checkNotExistAndGet(Long projectId) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        if (projectDTO == null) {
            throw new CommonException(ERROR_PROJECT_NOT_EXIST);
        }
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> listOrgProjectsWithLimitExceptSelf(Long projectId, String name) {
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        if (projectDTO == null) {
            throw new CommonException(ERROR_PROJECT_NOT_EXIST);
        }
        int limit = 50;

        List<ProjectDTO> projectDTOS = projectMapper.selectProjectsByOrgIdAndNameWithLimit(projectDTO.getOrganizationId(), name, limit);
        return projectDTOS.stream()
                .filter(project -> !project.getId().equals(projectId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        return projectMapper.selectAll();
    }

    @Override
    public Page<UserDTO> pagingQueryTheUsersOfProject(Long projectId, Long userId, String email, PageRequest pageRequest, String param) {
        ProjectDTO project = projectMapper.selectByPrimaryKey(projectId);
        if (ObjectUtils.isEmpty(project)) {
            return new Page<>();
        }
        Long organizationId = project.getOrganizationId();
        Role projectAdmin = queryProjectAdminByTenantId(organizationId);
        Role projectMember = queryProjectMemberByTenantId(organizationId);
        return PageHelper.doPage(pageRequest, () -> projectPermissionMapper.selectUsersByOptionsOrderByRoles(projectId, userId, email, param, projectAdmin.getId(), projectMember.getId()));
    }

    protected Role queryProjectAdminByTenantId(Long organizationId) {
        List<Role> roles = roleC7nMapper.getByTenantIdAndLabel(organizationId, RoleLabelEnum.PROJECT_ADMIN.value());
        if (ObjectUtils.isEmpty(roles)) {
            throw new CommonException("error.project.role.not.existed");
        }
        return roles.get(0);
    }

    protected Role queryProjectMemberByTenantId(Long organizationId) {
        List<Role> roles = roleC7nMapper.getByTenantIdAndLabel(organizationId, RoleLabelEnum.PROJECT_MEMBER.value());
        if (ObjectUtils.isEmpty(roles)) {
            throw new CommonException("error.project.role.not.existed");
        }
        return roles.get(0);
    }

    protected Set<Long> getRoleIdsByLabel(Long organizationId, String labelName) {
        List<Role> roles = roleC7nMapper.getByTenantIdAndLabel(organizationId, labelName);
        if (ObjectUtils.isEmpty(roles)) {
            throw new CommonException("error.project.role.not.existed");
        }
        return roles.stream().map(Role::getId).collect(Collectors.toSet());
    }

    @Override
    public Page<UserDTO> agileUsers(Long projectId, PageRequest pageable, Set<Long> userIds, String param) {
        ProjectDTO project = projectMapper.selectByPrimaryKey(projectId);
        if (ObjectUtils.isEmpty(project)) {
            return new Page<>();
        }
        Long organizationId = project.getOrganizationId();
        Set<Long> adminRoleIds = getRoleIdsByLabel(organizationId, RoleLabelEnum.PROJECT_ADMIN.value());
        return PageHelper.doPage(pageable, () -> projectPermissionMapper.selectAgileUsersByProjectId(projectId, userIds, param, adminRoleIds));
    }

    @Override
    public Page<UserDTO> agileUsersByProjects(PageRequest pageable, AgileUserVO agileUserVO) {
        Long organizationId = agileUserVO.getOrganizationId();
        if (ObjectUtils.isEmpty(organizationId)) {
            throw new CommonException("error.feign.agile.user.organizationId.null");
        }
        Set<Long> projectIds = agileUserVO.getProjectIds();
        Set<Long> userIds = agileUserVO.getUserIds();
        if (ObjectUtils.isEmpty(projectIds)) {
            throw new CommonException("error.feign.agile.user.projectIds.empty");
        }
        Set<Long> adminRoleIds = getRoleIdsByLabel(organizationId, RoleLabelEnum.PROJECT_ADMIN.value());
        return PageHelper.doPage(pageable, () -> projectPermissionMapper.selectAgileUsersByProjectIds(projectIds, userIds, agileUserVO.getParam(), adminRoleIds));
    }

    @Override
    public ProjectDTO queryBasicInfo(Long projectId) {
        Assert.notNull(projectId, ResourceCheckConstants.ERROR_PROJECT_IS_NULL);
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        List<ProjectCategoryDTO> projectCategoryDTOS = projectMapCategoryMapper.selectProjectCategoryNames(projectDTO.getId());
        projectDTO.setCategories(projectCategoryDTOS);

        return projectDTO;
    }

    @Override
    public List<ProjectDTO> queryProjectByOption(ProjectDTO projectDTO) {
        return projectMapper.select(projectDTO);
    }

    @Override
    public Boolean checkProjCode(String code) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setCode(code);
        return projectMapper.selectOne(projectDTO) == null;
    }


    @Override
    public Boolean checkPermissionByProjectId(Long projectId) {
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails == null) {
            return false;
        }
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        boolean isAdmin = userC7nService.isRoot(userDetails.getUserId());
        boolean isOrgAdmin = userC7nService.checkIsOrgRoot(projectDTO.getOrganizationId(), userDetails.getUserId());
        if (isAdmin || isOrgAdmin) {
            return true;
        } else {
            return projectMapper.checkPermissionByProjectId(projectDTO.getOrganizationId(), projectId, userDetails.getUserId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProjectCategory(Long projectId, List<Long> deleteProjectCategoryIds) {
        if (CollectionUtils.isEmpty(deleteProjectCategoryIds)) {
            return;
        }
        //批量删除
        projectMapCategoryMapper.batchDelete(projectId, deleteProjectCategoryIds);

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addProjectCategory(Long projectId, List<Long> categoryIds) {

        //批量插入
        List<ProjectMapCategoryDTO> projectMapCategoryDTOS = new ArrayList<>();
        for (Long categoryId : categoryIds) {
            ProjectMapCategoryDTO mapCategoryDTO = new ProjectMapCategoryDTO();
            mapCategoryDTO.setProjectId(projectId);
            mapCategoryDTO.setCategoryId(categoryId);
            projectMapCategoryDTOS.add(mapCategoryDTO);
        }
        projectMapCategoryMapper.batchInsert(projectMapCategoryDTOS);
    }

    @Override
    public ProjectCategoryWarpVO queryProjectCategory(Long projectId) {
        ProjectCategoryWarpVO projectCategoryWarpVO = new ProjectCategoryWarpVO();
        ProjectMapCategoryDTO mapCategoryDTO = new ProjectMapCategoryDTO();
        mapCategoryDTO.setProjectId(projectId);

        List<ProjectMapCategoryDTO> selectedCategory = projectMapCategoryMapper.select(mapCategoryDTO);
        List<ProjectCategoryDTO> projectCategoryDTOS = projectCategoryMapper.selectByIds(StringUtils.join(selectedCategory.stream().map(ProjectMapCategoryDTO::getCategoryId).collect(Collectors.toList()), ","));
        projectCategoryWarpVO.setSelectedProjectCategory(ConvertUtils.convertList(projectCategoryDTOS, ProjectCategoryVO.class));
        List<Long> ids = projectCategoryDTOS.stream().map(ProjectCategoryDTO::getId).collect(Collectors.toList());

        List<ProjectCategoryDTO> unSelected = projectCategoryMapper.selectAll().stream().filter(projectCategoryDTO -> !ids.contains(projectCategoryDTO.getId())).collect(Collectors.toList());
        projectCategoryWarpVO.setUnSelectedProjectCategory(ConvertUtils.convertList(unSelected, ProjectCategoryVO.class));
        return projectCategoryWarpVO;
    }

    @Override
    public ProjectSagaVO queryProjectSaga(Long organizationId, Long projectId, String operateType) {
        //获取创建项目的事务实例id PROJECT = "project";
        List<String> refIds = Arrays.asList(String.valueOf(projectId));
        //创建项目的saga 有两个，一个是choerodon这边创建项目的iam-create-project  一个是由制品发起的 rdupm-docker-repo-create
        //修改项目的 saga 有两个 一个choerodon这边修改项目的。可能由制品发起的 rdupm-docker-repo-create
        Map<String, SagaInstanceDetails> instanceDetailsIamMap = null;
        Map<String, SagaInstanceDetails> instanceDetailsRepoMap = null;
        if (StringUtils.equalsIgnoreCase(ProjectOperatorTypeEnum.CREATE.value(), operateType)) {
            instanceDetailsIamMap = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, refIds, SagaTopic.Project.PROJECT_CREATE));
            instanceDetailsRepoMap = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(DOCKER_REPO, refIds, SagaTopic.Project.REPO_CREATE));
        } else if (StringUtils.equalsIgnoreCase(ProjectOperatorTypeEnum.UPDATE.value(), operateType)) {
            instanceDetailsIamMap = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(PROJECT, refIds, SagaTopic.Project.PROJECT_UPDATE));
            instanceDetailsRepoMap = SagaInstanceUtils.listToMap(asgardServiceClientOperator.queryByRefTypeAndRefIds(DOCKER_REPO, refIds, SagaTopic.Project.REPO_CREATE));
        } else {
            return null;
        }

        ProjectSagaVO projectSagaVO = new ProjectSagaVO();
        //合并两个saga
        List<SagaInstanceDetails> sagaInstanceDetails = new ArrayList<>();
        if (!MapUtils.isEmpty(instanceDetailsIamMap) && !Objects.isNull(instanceDetailsIamMap.get(String.valueOf(projectId)))) {
            sagaInstanceDetails.add(instanceDetailsIamMap.get(String.valueOf(projectId)));
        }
        if (!MapUtils.isEmpty(instanceDetailsRepoMap) && !Objects.isNull(instanceDetailsRepoMap.get(String.valueOf(projectId)))) {
            sagaInstanceDetails.add(instanceDetailsRepoMap.get(String.valueOf(projectId)));
        }
        //根据事务实例获取当前项目状态 运行中，成功，失败
        String sagaStatus = SagaInstanceUtils.getSagaStatus(sagaInstanceDetails);
        LOGGER.info(">>>>>>>>>>>>>>>>>>>>>当前事务的状态{}", sagaStatus);
        //获取需要重试的任务id集合
        List<Long> sagaIds = SagaInstanceUtils.getSagaIds(sagaInstanceDetails);
        //根据总的任务数和已完成的任务数来判断状态
        projectSagaVO.setCompletedCount(SagaInstanceUtils.getCompletedCount(sagaInstanceDetails));
        projectSagaVO.setAllTask(SagaInstanceUtils.getAllTaskCount(sagaInstanceDetails));
        if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.RUNNING.getValue())) {
            if (StringUtils.equalsIgnoreCase(ProjectOperatorTypeEnum.CREATE.value(), operateType)) {
                projectSagaVO.setStatus(ProjectStatusEnum.CREATING.value());
            }
            if (StringUtils.equalsIgnoreCase(ProjectOperatorTypeEnum.UPDATE.value(), operateType)) {
                projectSagaVO.setStatus(ProjectStatusEnum.UPDATING.value());
            }
        } else if (org.apache.commons.lang3.StringUtils.equalsIgnoreCase(sagaStatus, InstanceStatusEnum.FAILED.getValue())) {
            projectSagaVO.setStatus(ProjectStatusEnum.FAILED.value());
            projectSagaVO.setSagaInstanceIds(sagaIds);
        } else {
            projectSagaVO.setStatus(ProjectStatusEnum.SUCCESS.value());
        }

        projectSagaVO.setProjectId(projectId);
        projectSagaVO.setOperateType(operateType);

        return projectSagaVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long projectId) {
        //删除项目 删除项目类型
        ProjectDTO projectDTO = projectMapper.selectByPrimaryKey(projectId);
        ProjectSagaVO projectSagaVO = queryProjectSaga(projectId, projectDTO.getId(), ProjectOperatorTypeEnum.CREATE.value());
        if (StringUtils.equalsIgnoreCase(projectSagaVO.getStatus(), ProjectStatusEnum.FAILED.value())) {
            ProjectMapCategoryDTO mapCategoryDTO = new ProjectMapCategoryDTO();
            mapCategoryDTO.setProjectId(projectId);
            projectMapCategoryMapper.delete(mapCategoryDTO);
            projectMapper.deleteByPrimaryKey(projectId);
        }
    }

    private List<Long> validateAndGetDbCategoryIds(ProjectDTO projectDTO, List<Long> categoryIds) {
        Assert.notNull(projectDTO, ERROR_PROJECT_NOT_EXIST);
        if (CollectionUtils.isEmpty(categoryIds)) {
            return new ArrayList<>();
        }
        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setProjectId(projectDTO.getId());
        List<Long> dbProjectCategoryIds = projectMapCategoryMapper.select(projectMapCategoryDTO).stream().map(ProjectMapCategoryDTO::getCategoryId).collect(Collectors.toList());
        return dbProjectCategoryIds;
    }
}
