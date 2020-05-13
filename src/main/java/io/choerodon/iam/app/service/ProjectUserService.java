package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.UserWithGitlabIdDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author zmf
 * @since 20-4-21
 */
public interface ProjectUserService {
    /**
     * 项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    Page<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, PageRequest pageRequest, String loginName, String realName,
                                                          String roleName, Boolean enabled, String params);


    /**
     * 项目层查询用户列表（包括用户信息以及所分配的项目角色信息）排除自己.
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    List<UserDTO> listUsersWithRolesOnProjectLevel(Long projectId, String loginName, String realName, String roleName, String params);

    /**
     * 项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    List<UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIdsInProject(Long projectId, Set<Long> userIds);

    /**
     * 查询项目下指定角色的用户列表
     *
     * @param projectId 项目id
     * @param roleLabel 角色标签
     * @return 用户列表
     */
    List<UserDTO> listProjectUsersByProjectIdAndRoleLabel(Long projectId, String roleLabel);

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId 项目id
     * @param param     查询参数
     * @return 用户列表
     */
    List<UserDTO> listUsersByName(Long projectId, String param);

    /**
     * 查询项目下的项目所有者
     *
     * @param projectId 项目id
     * @return 项目所有者列表
     */
    List<UserDTO> listProjectOwnerById(Long projectId);

    /**
     * 查询项目下的用户列表，根据真实名称或登录名搜索(限制20个)
     *
     * @param projectId 项目id
     * @param param     查询参数
     * @return 用户列表
     */
    List<UserDTO> listUsersByNameWithLimit(Long projectId, String param);

    /**
     * 检查是否还能创建用户
     *
     * @param projectId 项目id
     * @return true表示可以
     */
    Boolean checkEnableCreateUser(Long projectId);

    Page<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(PageRequest pageRequest, RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, Long sourceId, boolean doPage);
}
