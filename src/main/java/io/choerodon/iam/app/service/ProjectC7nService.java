package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.springframework.data.domain.Pageable;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.ProjectDTO;

/**
 * @author flyleft
 */
public interface ProjectC7nService {

    ProjectDTO queryProjectById(Long projectId);

//    Page<User> pagingQueryTheUsersOfProject(Long id, Long userId, String email, Pageable Pageable, String param);

    ProjectDTO update(ProjectDTO projectDTO);

    ProjectDTO disableProject(Long id);

    Boolean checkProjCode(String code);

    List<Long> listUserIds(Long projectId);

    List<ProjectDTO> queryByIds(Set<Long> ids);

    List<Long> getProListByName(String name);

    /**
     * 根据项目id，查询项目所属组织信息
     * @param projectId
     * @return
     */
    Tenant getOrganizationByProjectId(Long projectId);

    /**
     * 检查项目是否存在
     * @param projectId
     * @return 存在返回项目信息，不存在抛出not.exist exception
     */
    ProjectDTO checkNotExistAndGet(Long projectId);

    /**
     * 查询项目所属组织下所有可用项目（不包含本项目，限制50个)
     * @param projectId
     * @param name
     * @return
     */
    List<ProjectDTO> listOrgProjectsWithLimitExceptSelf(Long projectId, String name);


    /**
     * 统计组织下的项目数量
     * @param tenantId
     * @return
     */
    int countProjectNum(Long tenantId);
}
