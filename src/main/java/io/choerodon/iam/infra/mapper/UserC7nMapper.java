package io.choerodon.iam.infra.mapper;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.User;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
public interface UserC7nMapper extends BaseMapper<User> {
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

    List<User> selectAdminUserPage(@Param("loginName") String loginName, @Param("realName") String realName, @Param("params") String params);

    /**
     * 查询所用拥有对应角色的用户
     * @return
     */
    List<Long> selectUserByRoleCode(@Param("roleCode") String roleCode);


}

