package io.choerodon.iam.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.UserInfoDTO;
import io.choerodon.iam.infra.dto.payload.WebHookUser;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/1
 */
public interface UserC7nService {

    User queryInfo(Long userId);

    User updateInfo(User user, Boolean checkLogin);

    CustomUserDetails checkLoginUser(Long id);

    String uploadPhoto(Long id, MultipartFile file);

    String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

    void check(User user);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id数组
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<User> 用户集合
     */
    List<User> listUsersByIds(Long[] ids, Boolean onlyEnabled);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<User> 用户集合
     */
    List<UserWithGitlabIdVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled);

    /**
     * 根据用户emails集合查询用户的集合
     *
     * @param emails 用户email数组
     * @return List<User> 用户集合
     */
    List<User> listUsersByEmails(String[] emails);


    /**
     * 根据loginName集合查询所有用户
     */
    List<User> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled);

    Long queryOrgIdByEmail(String email);

    Map<String, Object> queryAllAndNewUsers();


    /**
     * 按时间段统计组织或平台人数
     *
     * @param organizationId 如果为null，则统计平台人数
     * @param startTime
     * @param endTime
     * @return UserNumberVO
     */
    UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime);


    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    List<UserWithGitlabIdVO> listUsersWithRolesAndGitlabUserIdByIdsInOrg(Long organizationId, Set<Long> userIds);

    /**
     * 查询组织下用户的项目列表.
     * 1. admin用户和组织管理员 可查看当前组织所有项目; 普通用户 只能查看分配了权限的启用项目
     * 2. root用户可进入所有项目; 组织管理员和普通用户需分配权限才能进入项目
     *
     * @param organizationId 组织Id
     * @param userId         用户Id
     * @param projectDTO     项目DTO
     * @param params         模糊查询字段
     * @return 项目列表
     */
    List<ProjectDTO> listProjectsByUserId(Long organizationId, Long userId, ProjectDTO projectDTO, String params);

    /**
     * 写给agile 简易版查询
     *
     * @param organizationId
     * @param userId
     * @param projectDTO
     * @param params
     * @return
     */
    List<ProjectDTO> listProjectsByUserIdForSimple(Long organizationId, Long userId, ProjectDTO projectDTO, String params);

    Page<User> pagingQueryAdminUsers(PageRequest pageRequest, String loginName, String realName, String params);

    void addAdminUsers(Long[] ids);

    void deleteAdminUser(long id);


    Page<TenantVO> pagingQueryOrganizationsWithRoles(PageRequest pageRequest,
                                                     Long id, String params);

    Page<ProjectDTO> pagingQueryProjectAndRolesById(PageRequest pageRequest,
                                                    Long id, String params);


    /**
     * 校验用户是否是root用户
     *
     * @param userId
     * @return true表示是root用户
     */
    Boolean isRoot(Long userId);

//    /**
//     * 校验用户是否是组织Root用户
//     *
//     * @param organizationId 组织id
//     * @param userId         用户id
//     * @return true表示是
//     */
//    Boolean checkIsOrgRoot(Long organizationId, Long userId);

    List<ProjectDTO> queryProjects(Long userId, Boolean includedDisabled);


    /**
     * 全局层分页查询用户列表（包括用户信息以及所分配的全局角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的全局角色信息）
     */
    Page<User> pagingQueryUsersWithRolesOnSiteLevel(PageRequest pageRequest, String orgName, String loginName, String realName,
                                                    String roleName, Boolean enabled, Boolean locked, String params);

    /**
     * 查询组织和项目
     *
     * @param userId      用户id
     * @param projectName 项目名称
     * @return
     */
    OrganizationProjectVO queryOrganizationProjectByUserId(Long userId, String projectName);

    List<ProjectDTO> listProjectsByUserIdOptional(Long organizationId, Long userId);

    /**
     * 校验用户是否是项目的所有者
     *
     * @param id
     * @param projectId
     * @return true 是
     */
    Boolean checkIsGitlabOwner(Long id, Long projectId, String level);

    /**
     * 校验用户是否是项目的所有者
     *
     * @param ids
     * @param projectId
     * @return true 是
     */
    Map<Long, Boolean> checkUsersAreGitlabProjectOwner(Set<Long> ids, Long projectId, String level);

    Boolean checkIsGitlabOwnerInOrgOrProject(Long projectId, Long userId);


    /**
     * 校验用户是否是项目的所有者
     *
     * @param userId    用户id
     * @param projectId 项目id
     * @return true 是
     */
    Boolean checkIsProjectOwner(Long userId, Long projectId);

    Page<OrgAdministratorVO> pagingQueryOrgAdministrator(PageRequest Pageable, Long organizationId,
                                                         String realName, String loginName, String params);

    /**
     * 异步
     * 向用户发送通知（包括邮件和站内信）
     *
     * @param userIds       接受通知的目标用户
     * @param code          业务code
     * @param params        渲染参数
     * @param sourceId      触发发送通知对应的组织/项目id，如果是site层，可以为0或null
     * @param resourceLevel 资源层次
     */
    void sendNotice(List<Long> userIds, String code,
                    Map<String, String> params, Long sourceId, ResourceLevel resourceLevel);

    /**
     * 校验用户是否是组织Root用户
     *
     * @param organizationId
     * @param userId
     * @return true 或者 false
     */
    Boolean checkIsOrgRoot(Long organizationId, Long userId);


    /**
     * 创建用户角色.
     *
     * @param userDTO          用户DTO
     * @param roleList         角色列表
     * @param sourceType       资源层级
     * @param sourceId         资源Id
     * @param isEdit           是否为新建操作: 如果为false,则只插入; 否则既插入也删除
     * @param allowRoleEmpty   是否允许用户角色为空
     * @param allowRoleDisable 是否允许用户角色为禁用
     * @return 用户角色DTO列表
     */
    List<MemberRole> createUserRoles(User userDTO, List<Role> roleList, String sourceType, Long sourceId,
                                     boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable);

    List<MemberRole> createUserRoles(User userDTO, List<Role> roleDTOList, String sourceType, Long sourceId,
                                     boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable, Boolean syncAll);


    /**
     * 更新用户角色.
     *
     * @param userId     用户Id
     * @param sourceType 资源层级
     * @param sourceId   资源Id
     * @param roleList   角色列表
     * @return 用户DTO
     */
    User updateUserRoles(Long userId, String sourceType, Long sourceId, List<Role> roleList);

    User updateUserRoles(Long userId, String sourceType, Long sourceId, List<Role> roleList, Boolean syncAll);

    /**
     * 在全局层/组织层/项目层 批量分配给用户角色.
     *
     * @param sourceType     资源层级
     * @param sourceId       资源层级
     * @param memberRoleList 用户角色列表
     * @return 用户角色DTO列表
     */
    List<MemberRole> assignUsersRoles(String sourceType, Long sourceId, List<MemberRole> memberRoleList);

    UserInfoDTO updateUserInfo(Long id, UserInfoDTO userInfoDTO);

    void selfUpdatePassword(Long userId, UserPasswordDTO userPasswordDTO, Boolean checkPassword, Boolean checkLogin);

    User updateUserDisabled(Long userId);

    UserDTO queryByLoginName(String loginName);

    List<UserDTO> listUsersWithGitlabLabel(Long projectId, String labelName, RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    UserVO selectSelf();

    /**
     * 在全局层/组织层/项目层 根据用户名查询启用状态的用户列表.
     *
     * @param sourceType     资源层级
     * @param sourceId       资源Id
     * @param userName       用户名
     * @param exactMatchFlag
     * @return 启用状态的用户列表
     */
    List<User> listEnableUsersByName(String sourceType, Long sourceId, String userName, Boolean exactMatchFlag, Boolean organizationFlag);

    /**
     * 给用户分配组织管理员角色
     *
     * @param userIds
     * @param organizationId
     */
    void createOrgAdministrator(List<Long> userIds, Long organizationId);

    Page<SimplifiedUserVO> pagingQueryAllUser(PageRequest pageRequest, String param, Long organizationId);

    void deleteOrgAdministrator(Long organizationId, Long userId);

    void assignUsersRolesOnOrganizationLevel(Long organizationId, List<MemberRole> memberRoleDTOS);

    Boolean switchSite();

    List<User> listUsersByRealNames(Set<String> realNames, Boolean onlyEnabled);

    List<UserDTO> pagingQueryUsersByRoleIdOnSiteLevel(Long roleId);

    List<UserDTO> pagingQueryUsersByRoleIdOnOrganizationLevel(Long roleId, Long sourceId);

    List<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(Long roleId, Long sourceId);

    List<UserDTO> listUsersUnderRoleByIds(Long projectId, String roleIdString);

    UserDTO queryPersonalInfo();

    WebHookUser getWebHookUser(Long userId);

    /**
     * 批量根据项目id查询用户在这个项目下拥有的角色标签
     *
     * @param userId     用户id
     * @param projectIds 项目id集合
     * @return 项目下的用户有的角色的标签, 如果在某个项目下没有角色, 不会包含该项目的纪录
     */
    List<UserProjectLabelVO> listRoleLabelsForUserInTheProject(Long userId, Set<Long> projectIds);

    Page<ProjectDTO> pagingProjectsByUserId(Long organizationId, Long userId, ProjectSearchVO projectSearchVO, PageRequest pageable, Boolean onlySucceed);

    int getDisableProjectByProjectMember(Long tenantId, Long userId);

    /**
     * 查询用户在组织下可以访问的所有项目的id
     *
     * @param organizationId
     * @param userId
     * @return
     */
    List<Long> queryCanAccessProjectIdsByUserId(Long organizationId, Long userId);

    List<ProjectDTO> listOwnedProjects(Long organizationId, Long userId);

    Page<ProjectDTO> pageOwnedProjects(Long organizationId, Long currentProjectId, Long userId, PageRequest pageRequest, String param);

    void checkEmail(User user);

    List<User> listMarketAuditor();

    List<User> listRoot();

    /**
     * 计算所有用户的数量
     *
     * @return 用户数量
     */
    UserCountAllVO countAllUsers();

    /**
     * 获取所有用户id
     *
     * @return 用户id
     */
    Set<Long> listAllUserIds();

    /**
     * 查询出用户有角色的devops或运维项目
     *
     * @param projectName
     * @param pageRequest
     * @return
     */
    Page<ProjectDTO> queryProjectsOfDevopsOrOperations(String projectName, PageRequest pageRequest);


    Boolean platformAdministrator();


    List<Tenant> adminOrgList();

    List<User> listSiteAdministrator();

    Boolean selectUserPhoneBind();

    Boolean checkIsOrgAdmin(Long organizationId);


    void addExtraInformation(List<ProjectDTO> projects,
                             boolean isAdmin,
                             boolean isOrgAdmin,
                             Long organizationId,
                             Long userId);

    Boolean platformAdministratorOrAuditor(Long userId);

    void checkUserPhoneOccupied(String phone, Long userId);

    Page<ProjectDTO> pagingProjectsByOptions(Long organizationId, Long userId, ProjectSearchVO projectSearchVO, String params, PageRequest pageable, Boolean onlySucceed);

    CheckEmailVO checkUserEmail(Long organizationId, String email);

    void checkLoginName(String loginName);

    Page<User> listUsersOnProjectLevelPage(Long projectId, String userName, PageRequest pageRequest);

}