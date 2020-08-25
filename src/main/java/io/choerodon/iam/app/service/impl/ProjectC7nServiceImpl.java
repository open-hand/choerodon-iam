package io.choerodon.iam.app.service.impl;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.PROJECT_UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.producer.StartSagaBuilder;
import io.choerodon.asgard.saga.producer.TransactionalProducer;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.app.service.OrganizationProjectC7nService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.asserts.DetailsHelperAssert;
import io.choerodon.iam.infra.asserts.OrganizationAssertHelper;
import io.choerodon.iam.infra.asserts.ProjectAssertHelper;
import io.choerodon.iam.infra.asserts.UserAssertHelper;
import io.choerodon.iam.infra.constant.ResourceCheckConstants;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.enums.RoleLabelEnum;
import io.choerodon.iam.infra.feign.AgileFeignClient;
import io.choerodon.iam.infra.feign.TestManagerFeignClient;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
import io.choerodon.iam.infra.mapper.ProjectUserMapper;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/15
 *
 */
@Service
public class ProjectC7nServiceImpl implements ProjectC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectC7nServiceImpl.class);

    protected static final String ERROR_PROJECT_NOT_EXIST = "error.project.not.exist";

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
    protected AgileFeignClient agileFeignClient;
    protected TestManagerFeignClient testManagerFeignClient;
    protected ProjectUserMapper projectUserMapper;
    protected TransactionalProducer transactionalProducer;

    protected RoleC7nMapper roleC7nMapper;

    public ProjectC7nServiceImpl(OrganizationProjectC7nService organizationProjectC7nService,
                                 OrganizationAssertHelper organizationAssertHelper,
                                 UserMapper userMapper,
                                 ProjectMapper projectMapper,
                                 ProjectAssertHelper projectAssertHelper,
                                 ProjectMapCategoryMapper projectMapCategoryMapper,
                                 UserAssertHelper userAssertHelper,
                                 TenantMapper organizationMapper,
                                 TestManagerFeignClient testManagerFeignClient,
                                 AgileFeignClient agileFeignClient,
                                 TransactionalProducer transactionalProducer,
                                 ProjectUserMapper projectUserMapper,
                                 RoleC7nMapper roleC7nMapper) {
        this.organizationProjectC7nService = organizationProjectC7nService;
        this.organizationAssertHelper = organizationAssertHelper;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.userAssertHelper = userAssertHelper;
        this.organizationMapper = organizationMapper;
        this.agileFeignClient = agileFeignClient;
        this.testManagerFeignClient = testManagerFeignClient;
        this.projectUserMapper = projectUserMapper;
        this.transactionalProducer = transactionalProducer;
        this.roleC7nMapper = roleC7nMapper;
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
                ResponseEntity<AgileProjectInfoVO> agileProjectResponse = agileFeignClient.queryProjectInfoByProjectId(projectId);
                if (agileProjectResponse.getStatusCode().is2xxSuccessful()) {
                    AgileProjectInfoVO agileProject = agileProjectResponse.getBody();
                    dto.setAgileProjectId(agileProject.getInfoId());
                    dto.setAgileProjectCode(agileProject.getProjectCode());
                    dto.setAgileProjectObjectVersionNumber(agileProject.getObjectVersionNumber());
                }
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
        if (projectDTO.getAgileProjectId() != null) {
            AgileProjectInfoVO agileProject = new AgileProjectInfoVO();
            agileProject.setInfoId(projectDTO.getAgileProjectId());
            agileProject.setProjectCode(projectDTO.getAgileProjectCode());
            agileProject.setObjectVersionNumber(projectDTO.getAgileProjectObjectVersionNumber());
            try {
                agileFeignClient.updateProjectInfo(projectDTO.getId(), agileProject);
                testManagerFeignClient.updateProjectInfo(projectDTO.getId(), agileProject);
            } catch (Exception e) {
                LOGGER.warn("agile feign invoke exception: {}", e.getMessage());
            }
        }
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
        Set<Long> adminRoleIds = getRoleIdsByLabel(organizationId, RoleLabelEnum.PROJECT_ADMIN.value());
        return PageHelper.doPageAndSort(pageRequest, () -> projectUserMapper.selectUsersByOptionsOrderByRoles(projectId, userId, email, param, adminRoleIds));
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
        return PageHelper.doPage(pageable, () -> projectUserMapper.selectAgileUsersByProjectId(projectId, userIds, param, adminRoleIds));
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
    public Boolean checkProjCode(String code) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setCode(code);
        return projectMapper.selectOne(projectDTO) == null;
    }


}
