package io.choerodon.iam.infra.mapper;

import java.sql.Date;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.User;

import io.choerodon.iam.infra.dto.RoleDTO;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
public interface UserC7nMapper {
    List<User> listUsersByIds(@Param("ids") Long[] ids, @Param("onlyEnabled") Boolean onlyEnabled);

    List<User> listUsersByEmails(@Param("emails") String[] emails);

    /**
     * 根据用户登录名集合查所有用户
     *
     * @param loginNames
     * @param onlyEnabled
     * @return
     */
    List<User> listUsersByLoginNames(@Param("loginNames") String[] loginNames,
                                     @Param("onlyEnabled") Boolean onlyEnabled);

    /**
     * 全平台新增用户数（包括停用）
     *
     * @return 返回时间段内新增用户数
     */
    Integer newUsersByDate(@Param("begin") String begin,
                           @Param("end") String end);

    /**
     * 统计指定时间前平台或组织人数
     *
     * @param organizationId 如果为null，则统计平台人数
     * @param startTime
     * @return
     */
    long countPreviousNumberByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                           @Param("startTime") Date startTime);

    /**
     * 查询指定时间平台或组织新增人数
     *
     * @param organizationId 如果为null，则查询平台人数
     * @param startTime
     * @param endTime
     * @return
     */
    List<User> selectByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);


    List<User> selectUsersByLevelAndOptions(@Param("sourceType") String sourceType,
                                            @Param("sourceId") Long sourceId,
                                            @Param("userId") Long userId,
                                            @Param("email") String email,
                                            @Param("param") String param);

    /**
     * 组织层查询用户总数.
     * 1. 查询属于该组织的用户
     * 2. 查询member_role表分配了该组织角色的用户
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 组织用户总数
     */
    int selectCountUsersOnOrganizationLevel(@Param("sourceType") String sourceType,
                                            @Param("sourceId") Long sourceId,
                                            @Param("loginName") String loginName,
                                            @Param("realName") String realName,
                                            @Param("roleName") String roleName,
                                            @Param("enabled") Boolean enabled,
                                            @Param("locked") Boolean locked,
                                            @Param("params") String params);


    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    List<User> selectUserWithRolesOnOrganizationLevel(@Param("start") Integer start,
                                                      @Param("size") Integer size,
                                                      @Param("sourceType") String sourceType,
                                                      @Param("sourceId") Long sourceId,
                                                      @Param("loginName") String loginName,
                                                      @Param("realName") String realName,
                                                      @Param("roleName") String roleName,
                                                      @Param("enabled") Boolean enabled,
                                                      @Param("locked") Boolean locked,
                                                      @Param("params") String params);

    List<User> selectAdminUserPage(@Param("loginName") String loginName,
                                   @Param("realName") String realName,
                                   @Param("params") String params,
                                   @Param("userId") Long userId);

    /**
     * 查询所用拥有对应角色的用户
     *
     * @return
     */
    List<Long> selectUserByRoleCode(@Param("roleCode") String roleCode);


    /**
     * 全局层查询用户总数.
     * 1. 查询用户表所有数据
     * 2. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 平台用户总数
     */
    int selectCountUsersOnSiteLevel(@Param("sourceType") String sourceType,
                                    @Param("sourceId") Long sourceId,
                                    @Param("orgName") String orgName,
                                    @Param("loginName") String loginName,
                                    @Param("realName") String realName,
                                    @Param("roleName") String roleName,
                                    @Param("enabled") Boolean enabled,
                                    @Param("locked") Boolean locked,
                                    @Param("params") String params);


    /**
     * 全局层分页查询用户列表（包括用户信息以及所分配的全局角色信息）.
     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 用户列表（包括用户信息以及所分配的全局角色信息）
     */
    List<User> selectUserWithRolesOnSiteLevel(@Param("start") Integer start,
                                              @Param("size") Integer size,
                                              @Param("sourceType") String sourceType,
                                              @Param("sourceId") Long sourceId,
                                              @Param("orgName") String orgName,
                                              @Param("loginName") String loginName,
                                              @Param("realName") String realName,
                                              @Param("roleName") String roleName,
                                              @Param("enabled") Boolean enabled,
                                              @Param("locked") Boolean locked,
                                              @Param("params") String params);


    /**
     * 查询用户在项目下拥有的角色
     *
     * @param id
     * @param projectId
     * @return
     */
    List<RoleDTO> selectRolesByUidAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);

    List<User> listOrgAdministrator(@Param("organizationId") Long organizationId,
                                    @Param("realName") String realName,
                                    @Param("loginName") String loginName,
                                    @Param("params") String params);


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
    List<User> selectUserWithRolesOnProjectLevel(@Param("start") Integer start,
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
    List<User> listUserWithRolesOnProjectLevelByIds(
            @Param("projectId") Long projectId,
            @Param("userIds") Set<Long> userIds);


    /**
     * 组织层根据用户id查询用户列表（包括用户信息以及所分配的组织角色信息）.
     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    List<User> listUserWithRolesOnOrganizationLevelByIds(
            @Param("organization_id") Long organizationId,
            @Param("userIds") Set<Long> userIds);


    /**
     * 根据用户名查询启用状态的用户列表.
     * 1. 全局层: 模糊匹配全局用户
     * 2. 组织层: 精确匹配全局用户以及模糊匹配本组织用户
     * 3. 项目层：精确匹配全局用户以及模糊匹配项目所在组织用户
     *
     * @param sourceType 资源层级
     * @param sourceId   资源Id
     * @param userName   用户名
     * @return 启用状态的用户列表
     */
    List<User> listEnableUsersByName(@Param("sourceType") String sourceType,
                                     @Param("sourceId") Long sourceId,
                                     @Param("userName") String userName);

    /**
     * 根据组织Id和用户Id查询该用户是否分配了组织管理员角色.
     *
     * @param organizationId 组织Id
     * @param userId         用户Id
     * @return 返回true 表示分配了组织管理员角色;否则, 表示未分配
     */
    Boolean isOrgAdministrator(@Param("organizationId") Long organizationId,
                               @Param("userId") Long userId);

    /**
     * 校验用户是否是gitlab项目所有者
     *
     * @param id
     * @param projectId
     * @return
     */
    Integer checkIsGitlabProjectOwner(@Param("id") Long id, @Param("projectId") Long projectId);

    /**
     * 查询项目下指定角色的用户列表
     *
     * @param projectId
     * @param roleLable
     * @return
     */
    List<User> listProjectUsersByProjectIdAndRoleLable(@Param("projectId") Long projectId, @Param("roleLable") String roleLable);


    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId
     * @param param
     * @return
     */
    List<User> listUsersByName(@Param("projectId") Long projectId, @Param("param") String param);

    /**
     * 查询所有的组织管理员
     *
     * @return
     */
    List<User> queryAllOrgAdmin(@Param("roleId") Long roleId);

    List<User> listProjectOwnerById(@Param("projectId") Long projectId);

    /**
     * 按用户名搜索项目下的用户（限制20个）
     *
     * @param projectId
     * @param param
     * @return
     */
    List<User> listUsersByNameWithLimit(@Param("projectId") Long projectId,
                                        @Param("param") String param);

    /**
     * 查询用户有管理权限的组织id集合
     * @param userId
     * @param orgIds
     * @return
     */
    Set<Long> listManagedOrgIdByUserId(@Param("userId") Long userId,
                                       @Param("orgIds") Set<Long> orgIds);
}

