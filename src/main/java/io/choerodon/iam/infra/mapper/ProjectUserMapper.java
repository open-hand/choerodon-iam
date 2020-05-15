package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.RoleDTO;

import io.choerodon.iam.api.vo.agile.RoleUserCountVO;
import io.choerodon.iam.infra.dto.ProjectUserDTO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author zmf
 * @since 20-4-21
 */
public interface ProjectUserMapper extends BaseMapper<ProjectUserDTO> {
    /**
     * 项目层查询用户总数.
     * 1. 查询当前项目分配了项目角色的用户
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 项目用户总数
     */
    int selectCountUsersOnProjectLevel(@Param("sourceType") String sourceType,
                                       @Param("sourceId") Long sourceId,
                                       @Param("loginName") String loginName,
                                       @Param("realName") String realName,
                                       @Param("roleName") String roleName,
                                       @Param("enabled") Boolean enabled,
                                       @Param("params") String params);


    /**
     * 项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）.
     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    List<UserDTO> selectUserWithRolesOnProjectLevel(@Param("start") Integer start,
                                                 @Param("size") Integer size,
                                                 @Param("sourceType") String sourceType,
                                                 @Param("sourceId") Long sourceId,
                                                 @Param("loginName") String loginName,
                                                 @Param("realName") String realName,
                                                 @Param("roleName") String roleName,
                                                 @Param("enabled") Boolean enabled,
                                                 @Param("params") String params);

    /**
     * 项目层根据用户id查询用户列表（包括用户信息以及所分配的项目角色信息）.
     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    List<UserDTO> listUserWithRolesOnProjectLevelByIds(
            @Param("projectId") Long projectId,
            @Param("userIds") Set<Long> userIds);

    /**
     * 查询项目下指定角色的用户列表
     *
     * @param projectId 项目id
     * @param roleLabel 角色标签
     * @return 含有（含有指定角色标签的）角色的用户
     */
    List<UserDTO> listProjectUsersByProjectIdAndRoleLabel(@Param("projectId") Long projectId, @Param("roleLabel") String roleLabel);


    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId 项目id
     * @param param     查询参数
     * @return 用户列表
     */
    List<UserDTO> listUsersByName(@Param("projectId") Long projectId, @Param("param") String param);


    List<UserDTO> listProjectOwnerById(@Param("projectId") Long projectId);


    /**
     * 按用户名搜索项目下的用户（限制20个）
     *
     * @param projectId 项目id
     * @param param     查询参数
     * @return 用户列表
     */
    List<UserDTO> listUsersByNameWithLimit(@Param("projectId") Long projectId,
                                           @Param("param") String param);

    /**
     * 列出用户在项目层的角色id
     *
     * @param projectId 项目id
     * @param userId    用户id
     * @return 角色id列表
     */
    List<Long> listProjectRoleIds(@Param("projectId") Long projectId, @Param("userId") Long userId);

    /**
     * 统计项目下角色分配用户数
     * @param projectId
     * @return
     */
    List<RoleUserCountVO> countProjectRoleUser(@Param("projectId")Long projectId);

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     * @param projectId
     * @param userId
     * @param email
     * @param param
     * @return
     */
    List<UserDTO> selectUsersByOptions(@Param("projectId") Long projectId,
                                       @Param("userId") Long userId,
                                       @Param("email") String email,
                                       @Param("param") String param);

    /**
     *
     * @param projectId
     * @param roleId
     * @param roleAssignmentSearchDTO
     * @param param
     * @return
     */
    List<UserDTO> listProjectUsersByRoleIdAndOptions(@Param("projectId")Long projectId,
                                                     @Param("roleId") Long roleId,
                                                     @Param("roleAssignmentSearchDTO") RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                                     @Param("param") String param);

    /*
     * 根据项目查询项目成员和传入的敏捷用户
     */
    List<UserDTO> selectAgileUsersByProjectId(@Param("projectId") Long projectId,
                                              @Param("userIds") Set<Long> userIds,
                                              @Param("param") String param);

    /**
     * 查询用户在项目下拥有的角色
     * @param projectId
     * @param userId
     * @return
     */
    List<RoleDTO> listRolesByProjectIdAndUserId(@Param("projectId") Long projectId,
                                                @Param("userId") Long userId);
}
