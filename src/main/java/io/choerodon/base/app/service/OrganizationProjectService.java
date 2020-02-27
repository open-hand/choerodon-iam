package io.choerodon.base.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.pagehelper.PageInfo;

import io.choerodon.base.api.vo.BarLabelRotationVO;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.ProjectDTO;

/**
 * @author flyleft
 */
public interface OrganizationProjectService {

    ProjectDTO createProject(ProjectDTO projectDTO);

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

    /**
     * 查询组织下可被分配至当前项目群的敏捷项目
     *
     * @param organizationId 组织Id
     * @param projectId      项目Id
     * @return 项目列表
     */
    List<ProjectDTO> getAvailableProject(Long organizationId, Long projectId);

    ProjectDTO selectCategoryByPrimaryKey(Long projectId);

    /**
     * 查询当前项目生效的普通项目群信息(项目为启用状态).
     *
     * @param organizationId 组织Id
     * @param projectId      项目Id
     * @return 普通项目群信息
     */
    ProjectDTO getGroupInfoByEnableProject(Long organizationId, Long projectId);

    List<ProjectDTO> getAgileProjects(Long organizationId, String param);

    /**
     * @param organizationId 组织Id
     * @param code           项目code
     * @return 根据组织Id及项目code查询项目
     */
    ProjectDTO getProjectByOrgIdAndCode(Long organizationId, String code);

    List<ProjectDTO> listProjectsByOrgId(Long organizationId);

    PageInfo<ProjectDTO> pagingQuery(Long organizationId, Pageable Pageable, ProjectDTO projectDTO, String params);

    /**
     * 统计项目部署次数
     * @param projectIds
     * @param startTime
     * @param endTime
     * @return
     */
    BarLabelRotationVO countDeployRecords(Set<Long> projectIds, Date startTime, Date endTime);

    List<ProjectDTO> listProjectsWithLimit(Long organizationId, String name);
}
