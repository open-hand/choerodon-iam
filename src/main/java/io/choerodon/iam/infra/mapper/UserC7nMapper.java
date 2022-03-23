package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.api.vo.SimplifiedUserVO;
import io.choerodon.iam.api.vo.UserProjectLabelVO;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.common.BaseMapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;

import java.sql.Date;
import java.util.List;
import java.util.Set;


/**
 * @author scp
 * @since 2020/4/15
 */
public interface UserC7nMapper extends BaseMapper<UserDTO> {
    List<User> listUsersByIds(@Param("ids") Long[] ids, @Param("onlyEnabled") Boolean onlyEnabled);

    List<User> listMarketAuditor();

    List<User> listUsersByEmails(@Param("emails") String[] emails);

    /**
     * 根据用户登录名集合查所有用户
     *
     * @param loginNames
     * @param onlyEnabled
     * @return 用户列表
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
     * @return long
     */
    long countPreviousNumberByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                           @Param("startTime") Date startTime);

    /**
     * 查询指定时间平台或组织新增人数
     *
     * @param organizationId 如果为null，则查询平台人数
     * @param startTime
     * @param endTime
     * @return User列表
     */
    List<User> selectByOrgIdAndDate(@Param("organizationId") Long organizationId,
                                    @Param("startTime") Date startTime,
                                    @Param("endTime") Date endTime);


    List<User> selectUsersByLevelAndOptions(@Param("sourceType") String sourceType,
                                            @Param("sourceId") Long sourceId,
                                            @Param("userId") Long userId,
                                            @Param("email") String email,
                                            @Param("param") String param,
                                            @Param("notSelectUserIds") List<Long> notSelectUserIds);

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
     * @return Long列表
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
     * @return RoleC7nDTO列表
     */
    List<RoleC7nDTO> selectRolesByUidAndProjectId(@Param("id") Long id, @Param("projectId") Long projectId);

    /**
     * 查询用户在项目下拥有的角色
     *
     * @param uids
     * @param projectId
     * @return RoleC7nDTO列表
     */
    List<RoleVO> selectRolesByUidsAndProjectId(@Param("ids") Set<Long> uids, @Param("projectId") Long projectId);


    List<User> listOrgAdministrator(@Param("organizationId") Long organizationId,
                                    @Param("realName") String realName,
                                    @Param("loginName") String loginName,
                                    @Param("params") String params);


//    /**
//     * 项目层查询用户总数.
//     * 1. 查询当前项目分配了项目角色的用户
//     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
//     *
//     * @return 项目用户总数
//     */
//    int selectCountUsersOnProjectLevel(@Param("sourceType") String sourceType,
//                                       @Param("sourceId") Long sourceId,
//                                       @Param("loginName") String loginName,
//                                       @Param("realName") String realName,
//                                       @Param("roleName") String roleName,
//                                       @Param("enabled") Boolean enabled,
//                                       @Param("params") String params);
//
//    /**
//     * 项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）.
//     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
//     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
//     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
//     *
//     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
//     */
//    List<User> selectUserWithRolesOnProjectLevel(@Param("start") Integer start,
//                                                 @Param("size") Integer size,
//                                                 @Param("sourceType") String sourceType,
//                                                 @Param("sourceId") Long sourceId,
//                                                 @Param("loginName") String loginName,
//                                                 @Param("realName") String realName,
//                                                 @Param("roleName") String roleName,
//                                                 @Param("enabled") Boolean enabled,
//                                                 @Param("params") String params);
//
//    /**
//     * 项目层根据用户id查询用户列表（包括用户信息以及所分配的项目角色信息）.
//     * 1. 用户信息包括用户Id、用户名、登录名、状态、安全状态、所属组织Id
//     * 2. 角色信息包括角色Id、角色名、角色编码、启用状态
//     * 3. 根据是否为ldap导入用户,登录名为用户的登录名或邮箱
//     *
//     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
//     */
//    List<User> listUserWithRolesOnProjectLevelByIds(
//            @Param("projectId") Long projectId,
//            @Param("userIds") Set<Long> userIds);


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
     * @param sourceType     资源层级
     * @param sourceId       资源Id
     * @param userName       用户名
     * @param exactMatchFlag
     * @return 启用状态的用户列表
     */
    List<User> listEnableUsersByName(@Param("sourceType") String sourceType,
                                     @Param("sourceId") Long sourceId,
                                     @Param("userName") String userName,
                                     @Param("exactMatchFlag") Boolean exactMatchFlag,
                                     @Param("tenantId") Long tenantId);

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
     * @return 结果数量
     */
    Integer checkIsGitlabProjectOwner(@Param("id") Long id, @Param("projectId") Long projectId);

    /**
     * 查询项目下指定角色的用户列表
     */
    List<User> listProjectUsersByProjectIdAndRoleLabel(@Param("projectId") Long projectId, @Param("roleLabel") String roleLabel);


    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId
     * @param param
     * @return User列表
     */
    List<User> listUsersByName(@Param("projectId") Long projectId, @Param("param") String param);

    /**
     * 查询所有的组织管理员
     *
     * @return User列表
     */
    List<User> queryAllOrgAdmin(@Param("roleId") Long roleId);

    List<User> listProjectOwnerById(@Param("projectId") Long projectId);

    /**
     * 按用户名搜索项目下的用户（限制20个）
     *
     * @param projectId
     * @param param
     * @return User列表
     */
    List<User> listUsersByNameWithLimit(@Param("projectId") Long projectId,
                                        @Param("param") String param);

    /**
     * 查询用户有管理权限的组织id集合
     *
     * @param userId
     * @param orgIds
     * @return Long Set集合
     */
    Set<Long> listManagedOrgIdByUserId(@Param("userId") Long userId,
                                       @Param("orgIds") Set<Long> orgIds);

    UserDTO queryUserByLoginName(@Param("loginName") String loginName);

    List<User> listUsersWithGitlabLabel(@Param("projectId") Long sourceId,
                                        @Param("labelName") String labelName,
                                        @Param("roleAssignmentSearchDTO")
                                                RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                        @Param("param") String param);


    Set<String> matchEmail(@Param("emailSet") Set<String> emailSet);

    Set<String> matchPhone(@Param("phoneSet") Set<String> phoneSet);


    List<SimplifiedUserVO> selectAllUsersSimplifiedInfo(@Param("params") String params);


    /**
     * 选择性查询用户，如果用户在组织下，则模糊查询，如果用户不在组织下精确匹配
     *
     * @param param
     * @param organizationId
     * @return 用户信息
     */
    List<SimplifiedUserVO> selectUsersOptional(@Param("params") String param, @Param("organizationId") Long organizationId);

    Integer selectUserCountFromMemberRoleByOptions(@Param("roleId") Long roleId,
                                                   @Param("memberType") String memberType,
                                                   @Param("sourceId") Long sourceId,
                                                   @Param("sourceType") String sourceType,
                                                   @Param("roleAssignmentSearchDTO")
                                                           RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                                   @Param("param") String param);

    List<User> listOrganizationUser(@Param("organizationId") Long organizationId,
                                    @Param("loginName") String loginName,
                                    @Param("realName") String realName,
                                    @Param("roleName") String roleName,
                                    @Param("enabled") Boolean enabled,
                                    @Param("locked") Boolean locked,
                                    @Param("params") String params);

    /**
     * 查询用户在项目下的角色标签
     *
     * @param userId    用户id
     * @param projectId 项目id
     * @return 在项目下的所有角色标签
     */
    List<String> queryUserLabelsInProjectLevel(@Param("userId") Long userId,
                                               @Param("projectId") Long projectId);

    /**
     * 用户在此项目是否有包含指定标签的角色
     *
     * @param userId    用户id
     * @param labelName 标签名
     * @param projectId 项目id
     * @return true表示有
     */
    boolean doesUserHaveLabelInProject(@Param("userId") Long userId,
                                       @Param("labelName") String labelName,
                                       @Param("projectId") Long projectId);

    /**
     * 根据realName模糊查询用户
     *
     * @param realNames
     * @param onlyEnabled
     * @return user列表
     */
    List<User> listUsersByRealNames(@Param("realNames") Set<String> realNames,
                                    @Param("onlyEnabled") Boolean onlyEnabled);

    List<UserDTO> listSiteUsersByRoleId(@Param("roleId") Long roleId);

    List<UserDTO> listOrgUsersByRoleId(@Param("roleId") Long roleId,
                                       @Param("tenantId") Long tenantId);

    List<UserDTO> listProjectUsersByRoleId(@Param("roleId") Long roleId,
                                           @Param("projectId") Long projectId);

    UserDTO queryPersonalInfo(@Param("userId") Long userId);

    /**
     * 批量根据项目id查询用户在这个项目下拥有的角色标签
     *
     * @param userId     用户id
     * @param projectIds 项目id集合
     * @return 项目下的用户有的角色的标签, 如果在某个项目下没有角色, 不会包含该项目的纪录
     */
    List<UserProjectLabelVO> listRoleLabelsForUserInTheProject(@Param("userId") Long userId,
                                                               @Param("projectIds") Set<Long> projectIds);

    /**
     * 查出所有用户的id
     *
     * @return 用户id
     */
    Set<Long> listAllUserIds();

    /**
     * attribute15 作为存放拼音字段
     *
     * @param userId
     * @param pinyin
     */
    void updatePinyinById(@Param("userId") Long userId, @Param("pinyin") String pinyin);

    /**
     * attribute14 作为存放拼音首字母字段
     *
     * @param userId
     * @param pinyinHeadChar
     */
    void updatePinyinHeadCharById(@Param("userId") Long userId, @Param("pinyinHeadChar") String pinyinHeadChar);

    /**
     * 查询用户所拥有角色的层级
     *
     * @param userId
     */
    List<String> queryUserRoleLevels(@Param("userId") Long userId);

    void updateUserPhoneBind(@Param("userId") Long userId, @Param("false") Integer no);

    Boolean platformAdministrator(@Param("userId") Long userId);

    List<Tenant> adminOrgList(@Param("userId") Long userId);

    List<User> listSiteAdministrator();

    Boolean queryPhoneBind(@Param("userId") Long userId);

    List<User> listAgileUserOnOrganizationLevel(@Param("organizationId") Long organizationId,
                                                @Param("agileUserVO") AgileUserVO agileUserVO);
}

