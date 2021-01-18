package io.choerodon.iam.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.BarLabelRotationVO;
import io.choerodon.iam.api.vo.ProjectVisitInfoVO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author flyleft
 */
public interface OrganizationProjectC7nService {

    ProjectDTO createProject(Long organizationId, ProjectDTO projectDTO);

    ProjectDTO create(ProjectDTO projectDTO);

    ProjectDTO update(Long organizationId, ProjectDTO projectDTO);

    ProjectDTO updateSelective(ProjectDTO projectDTO);

    ProjectDTO enableProject(Long organizationId, Long projectId, Long userId);

    ProjectDTO disableProject(Long organizationId, Long projectId, Long userId);

    Boolean check(ProjectDTO projectDTO);

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

    Page<ProjectDTO> pagingQuery(Long organizationId, PageRequest Pageable, ProjectDTO projectDTO, String params);

    /**
     * 统计项目部署次数
     *
     * @param projectIds
     * @param startTime
     * @param endTime
     * @return BarLabelRotationVO
     */
    BarLabelRotationVO countDeployRecords(Set<Long> projectIds, Date startTime, Date endTime);

    List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name);


    /**
     * 查询当前用户最近访问信息
     *
     * @return ProjectVisitInfoVO列表
     */
    List<ProjectVisitInfoVO> queryLatestVisitProjectInfo(Long organizationId);


    List<ProjectDTO> listProjectsWithCategoryByOrgId(Long organizationId, Boolean enable);
}
