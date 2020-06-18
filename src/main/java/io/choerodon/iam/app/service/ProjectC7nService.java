package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.saas.domain.entity.Tenant;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public interface ProjectC7nService {

    ProjectDTO queryProjectById(Long projectId);

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
     * 查询所有项目
     * @return
     */
    List<ProjectDTO> listAllProjects();

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     * @param projectId
     * @param userId
     * @param email
     * @param pageRequest
     * @param param
     * @return
     */
    Page<UserDTO> pagingQueryTheUsersOfProject(Long projectId, Long userId, String email, PageRequest pageRequest, String param);
}
