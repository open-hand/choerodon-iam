package io.choerodon.base.app.service.impl;

import static io.choerodon.base.infra.asserts.UserAssertHelper.WhichColumn;
import static io.choerodon.base.infra.utils.SagaTopic.Project.PROJECT_UPDATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.choerodon.asgard.saga.annotation.Saga;
import io.choerodon.asgard.saga.dto.StartInstanceDTO;
import io.choerodon.asgard.saga.feign.SagaClient;
import io.choerodon.base.api.dto.payload.ProjectEventPayload;
import io.choerodon.base.api.vo.AgileProjectInfoVO;
import io.choerodon.base.app.service.OrganizationProjectService;
import io.choerodon.base.app.service.ProjectService;
import io.choerodon.base.infra.asserts.DetailsHelperAssert;
import io.choerodon.base.infra.asserts.ProjectAssertHelper;
import io.choerodon.base.infra.asserts.UserAssertHelper;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.feign.AgileFeignClient;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.mapper.ProjectMapCategoryMapper;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * @author flyleft
 */
@Service
@RefreshScope
public class ProjectServiceImpl implements ProjectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private OrganizationProjectService organizationProjectService;

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
    private OrganizationMapper organizationMapper;
    private AgileFeignClient agileFeignClient;

    public ProjectServiceImpl(OrganizationProjectService organizationProjectService,
                              SagaClient sagaClient,
                              UserMapper userMapper,
                              ProjectMapper projectMapper,
                              ProjectAssertHelper projectAssertHelper,
                              ProjectMapCategoryMapper projectMapCategoryMapper,
                              UserAssertHelper userAssertHelper,
                              OrganizationMapper organizationMapper,
                              AgileFeignClient agileFeignClient) {
        this.organizationProjectService = organizationProjectService;
        this.sagaClient = sagaClient;
        this.userMapper = userMapper;
        this.projectMapper = projectMapper;
        this.projectAssertHelper = projectAssertHelper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.userAssertHelper = userAssertHelper;
        this.organizationMapper = organizationMapper;
        this.agileFeignClient = agileFeignClient;
    }

    @Override
    public ProjectDTO queryProjectById(Long projectId) {
        ProjectDTO dto = projectAssertHelper.projectNotExisted(projectId);
        if (enableCategory) {
            dto.setCategories(projectMapCategoryMapper.selectProjectCategoryNames(dto.getId()));
        }
        UserDTO createdUser = userMapper.selectByPrimaryKey(dto.getCreatedBy());
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

    @Override
    public PageInfo<UserDTO> pagingQueryTheUsersOfProject(Long id, Long userId, String email, Pageable pageable, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> userMapper.selectUsersByLevelAndOptions(ResourceLevel.PROJECT.value(), id, userId, email, param));
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
            } catch (Exception e) {
                LOGGER.warn("agile feign invoke exception: {}", e.getMessage());
            }
        }
        if (devopsMessage) {
            ProjectDTO dto = new ProjectDTO();
            CustomUserDetails details = DetailsHelperAssert.userDetailNotExisted();
            UserDTO user = userAssertHelper.userNotExisted(WhichColumn.LOGIN_NAME, details.getUsername());
            ProjectDTO newProject = projectAssertHelper.projectNotExisted(projectDTO.getId());

            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(newProject.getOrganizationId());
            ProjectEventPayload projectEventMsg = new ProjectEventPayload();
            projectEventMsg.setUserName(details.getUsername());
            projectEventMsg.setUserId(user.getId());
            if (organizationDTO != null) {
                projectEventMsg.setOrganizationCode(organizationDTO.getCode());
                projectEventMsg.setOrganizationName(organizationDTO.getName());
            }
            projectEventMsg.setProjectId(newProject.getId());
            projectEventMsg.setProjectCode(newProject.getCode());
            ProjectDTO newDTO = organizationProjectService.updateSelective(projectDTO);
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
            return organizationProjectService.updateSelective(projectDTO);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProjectDTO disableProject(Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return organizationProjectService.disableProject(null, projectId, userId);
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
            return projectMapper.selectByIds(ids);
        }
    }

    @Override
    public Boolean checkProjCode(String code) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setCode(code);
        return projectMapper.selectOne(projectDTO) == null;
    }
}
