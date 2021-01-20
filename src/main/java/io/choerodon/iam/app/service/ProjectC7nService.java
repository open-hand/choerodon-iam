package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.iam.api.vo.ProjectCategoryWarpVO;
import io.choerodon.iam.api.vo.ProjectSagaVO;
import io.choerodon.iam.api.vo.agile.AgileUserVO;

import org.hzero.iam.domain.entity.Tenant;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public interface ProjectC7nService {

    ProjectDTO queryProjectById(Long projectId, boolean enableCategory, boolean withUserInfo, boolean withAgileInfo);

    ProjectDTO update(ProjectDTO projectDTO);

    ProjectDTO disableProject(Long id);

    Boolean checkProjCode(String code);

    List<Long> listUserIds(Long projectId);

    List<ProjectDTO> queryByIds(Set<Long> ids);

    List<Long> getProListByName(String name);

    /**
     * 根据项目id，查询项目所属组织信息
     *
     * @param projectId
     * @return Tenant
     */
    Tenant getOrganizationByProjectId(Long projectId);

    /**
     * 检查项目是否存在
     *
     * @param projectId
     * @return 存在返回项目信息，不存在抛出not.exist exception
     */
    ProjectDTO checkNotExistAndGet(Long projectId);

    /**
     * 查询项目所属组织下所有可用项目（不包含本项目，限制50个)
     *
     * @param projectId
     * @param name
     * @return ProjectDTO列表
     */
    List<ProjectDTO> listOrgProjectsWithLimitExceptSelf(Long projectId, String name);


    /**
     * 查询所有项目
     *
     * @return
     */
    List<ProjectDTO> listAllProjects();

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId
     * @param userId
     * @param email
     * @param pageRequest
     * @param param
     * @return UserDTO分页
     */
    Page<UserDTO> pagingQueryTheUsersOfProject(Long projectId, Long userId, String email, PageRequest pageRequest, String param);

    /**
     * 查询项目下的项目成员，以及传入的userId的并集
     *
     * @param projectId
     * @param pageable
     * @param userIds
     * @param param
     * @return user 分页数据
     */
    Page<UserDTO> agileUsers(Long projectId, PageRequest pageable, Set<Long> userIds, String param);

    ProjectDTO queryBasicInfo(Long id);

    List<ProjectDTO> queryProjectByOption(ProjectDTO projectDTO);

    /**
     * 查询多个项目项目下的项目成员，以及传入的userId的并集
     *
     * @param pageable
     * @param agileUserVO
     * @return
     */
    Page<UserDTO> agileUsersByProjects(PageRequest pageable, AgileUserVO agileUserVO);

    Boolean checkPermissionByProjectId(Long projectId);

    /**
     * 删除项目得类型
     *
     * @param projectId
     * @param categoryIds
     */
    void deleteProjectCategory(Long projectId, List<Long> categoryIds);

    /**
     * 添加项目类型
     *
     * @param projectId
     * @param categoryIds
     * @return
     */
    void addProjectCategory(Long projectId, List<Long> categoryIds);

    ProjectCategoryWarpVO queryProjectCategory(Long projectId);

    ProjectSagaVO queryProjectSaga(Long organizationId, Long projectId, String operateType);

    void deleteProject(Long projectId);
}
