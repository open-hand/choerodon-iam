package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.BarLabelRotationVO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author flyleft
 */
public interface    OrganizationProjectService {

    ProjectDTO createProject(Long organizationId, ProjectDTO projectDTO);

    ProjectDTO create(ProjectDTO projectDTO);

    ProjectDTO update(Long organizationId, ProjectDTO projectDTO);

    ProjectDTO updateSelective(ProjectDTO projectDTO);

    ProjectDTO enableProject(Long organizationId, Long projectId, Long userId);

    ProjectDTO disableProject(Long organizationId, Long projectId, Long userId);

    void check(ProjectDTO projectDTO);

    /**
     * 查询 组织下 各类型下的项目数及项目名
     *
     * @param organizationId 组织Id
     * @return map
     */
    Map<String, Object> getProjectsByType(Long organizationId);

    ProjectDTO selectCategoryByPrimaryKey(Long projectId);

    List<ProjectDTO> getAgileProjects(Long organizationId, String param);

    /**
     * @param organizationId 组织Id
     * @param code           项目code
     * @return 根据组织Id及项目code查询项目
     */
    ProjectDTO getProjectByOrgIdAndCode(Long organizationId, String code);

    List<ProjectDTO> listProjectsByOrgId(Long organizationId);

    Page<ProjectDTO> pagingQuery(Long organizationId, PageRequest pageRequest, ProjectDTO projectDTO, String params);


    List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name);

    /**
     * 判断组织是否还能创建项目（指定日期后的创建的组织，最后能创建20个项目）
     *
     * @param organizationId
     */
    Boolean checkEnableCreateProject(Long organizationId);
}
