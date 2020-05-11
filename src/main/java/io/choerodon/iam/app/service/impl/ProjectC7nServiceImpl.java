package io.choerodon.iam.app.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
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
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.payload.ProjectEventPayload;
import io.choerodon.iam.infra.feign.AgileFeignClient;
import io.choerodon.iam.infra.feign.TestManagerFeignClient;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.iam.infra.mapper.ProjectMapper;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.choerodon.iam.infra.utils.SagaTopic.Project.PROJECT_UPDATE;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
@Service
public class ProjectC7nServiceImpl implements ProjectC7nService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectC7nServiceImpl.class);

    private static final String ERROR_PROJECT_NOT_EXIST = "error.project.not.exist";

    private OrganizationProjectC7nService organizationProjectC7nService;

    @Value("${choerodon.category.enabled:false}")
    private boolean enableCategory;

    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    @Value("${spring.application.name:default}")
    private String serviceName;

    private SagaClient sagaClient;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserMapper userMapper;

    private ProjectMapper projectMapper;
    private ProjectAssertHelper projectAssertHelper;
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    private UserAssertHelper userAssertHelper;
    private OrganizationAssertHelper organizationAssertHelper;
    private TenantMapper organizationMapper;
    private AgileFeignClient agileFeignClient;
    private TestManagerFeignClient testManagerFeignClient;

    public ProjectC7nServiceImpl(OrganizationProjectC7nService organizationProjectC7nService,
                                 SagaClient sagaClient,
                                 OrganizationAssertHelper organizationAssertHelper,
                                 UserMapper userMapper,
                                 ProjectMapper projectMapper,
                                 ProjectAssertHelper projectAssertHelper,
                                 ProjectMapCategoryMapper projectMapCategoryMapper,
                                 UserAssertHelper userAssertHelper,
                                 TenantMapper organizationMapper,
                                 TestManagerFeignClient testManagerFeignClient,
                                 AgileFeignClient agileFeignClient) {
        this.organizationProjectC7nService = organizationProjectC7nService;
        this.sagaClient = sagaClient;
        this.organizationAssertHelper = organizationAssertHelper;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.userAssertHelper = userAssertHelper;
        this.organizationMapper = organizationMapper;
        this.agileFeignClient = agileFeignClient;
        this.testManagerFeignClient = testManagerFeignClient;
    }

    @Override
    public ProjectDTO queryProjectById(Long projectId) {
        ProjectDTO dto = projectAssertHelper.projectNotExisted(projectId);
        if (enableCategory) {
            dto.setCategories(projectMapCategoryMapper.selectProjectCategoryNames(dto.getId()));
        }
        User createdUser = userMapper.selectByPrimaryKey(dto.getCreatedBy());
        if (createdUser != null) {
            dto.setCreateUserName(createdUser.getRealName());
            dto.setCreateUserImageUrl(createdUser.getImageUrl());
        }
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
        if (devopsMessage) {
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
            ProjectDTO newDTO = organizationProjectC7nService.updateSelective(projectDTO);
            projectEventMsg.setProjectName(projectDTO.getName());
            projectEventMsg.setImageUrl(newDTO.getImageUrl());
            BeanUtils.copyProperties(newDTO, dto);
            try {
                String input = mapper.writeValueAsString(projectEventMsg);
                sagaClient.startSaga(PROJECT_UPDATE, new StartInstanceDTO(input, "project", "" + newProject.getId(), ResourceLevel.PROJECT.value(), projectDTO.getId()));
            } catch (Exception e) {
                throw new CommonException("error.projectService.update.event", e);
            }
            return dto;
        } else {
            return organizationProjectC7nService.updateSelective(projectDTO);
        }
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
    public Boolean checkProjCode(String code) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setCode(code);
        return projectMapper.selectOne(projectDTO) == null;
    }


}