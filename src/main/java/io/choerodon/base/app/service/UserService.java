package io.choerodon.base.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.UserNumberVO;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.base.api.dto.UserInfoDTO;
import io.choerodon.base.api.dto.*;
import org.springframework.data.domain.Pageable;

import io.choerodon.base.api.vo.UserVO;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.*;


/**
 * @author superlee
 * @author wuguokai
 */
public interface UserService {

    UserDTO querySelf();

    Set<OrganizationDTO> queryOrganizations(Long userId, Boolean includedDisabled);

    UserDTO queryByLoginName(String loginName);

    void selfUpdatePassword(Long userId, UserPasswordDTO userPasswordDTO, Boolean checkPassword, Boolean checkLogin);

    UserDTO lockUser(Long userId, Integer lockExpireTime);

    UserDTO queryInfo(Long userId);

    RegistrantInfoDTO queryRegistrantInfoAndAdmin(String orgCode);

    UserDTO updateInfo(UserDTO user, Boolean checkLogin);

    void check(UserDTO user);

    PageInfo<UserDTO> pagingQueryUsersWithRoles(Pageable Pageable,
                                                RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long sourceId, ResourceType resourceType);

    PageInfo<UserDTO> pagingQueryUsersByRoleIdOnSiteLevel(Pageable Pageable,
                                                          RoleAssignmentSearchDTO roleAssignmentSearchDTO, Long roleId, boolean doPage);

    PageInfo<UserDTO> pagingQueryUsersByRoleIdOnOrganizationLevel(Pageable Pageable,
                                                                  RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                                                  Long roleId, Long sourceId, boolean doPage);

    PageInfo<UserDTO> pagingQueryUsersByRoleIdOnProjectLevel(Pageable Pageable,
                                                             RoleAssignmentSearchDTO roleAssignmentSearchDTO,
                                                             Long roleId, Long sourceId, boolean doPage);

    List<UserVO> listUsersWithGitlabLabel(Long projectId, String labelName, RoleAssignmentSearchDTO roleAssignmentSearchDTO);

    String uploadPhoto(Long id, MultipartFile file);

    String savePhoto(Long id, MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height);

    PageInfo<UserDTO> pagingQueryAdminUsers(Pageable Pageable, String loginName, String realName, String params);

    void addAdminUsers(long[] ids);

    void deleteAdminUser(long id);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id数组
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<UserDTO> 用户集合
     */
    List<UserDTO> listUsersByIds(Long[] ids, Boolean onlyEnabled);

    /**
     * 根据用户emails集合查询用户的集合
     *
     * @param emails 用户email数组
     * @return List<UserDTO> 用户集合
     */
    List<UserDTO> listUsersByEmails(String[] emails);

    PageInfo<OrganizationDTO> pagingQueryOrganizationsWithRoles(Pageable Pageable,
                                                                Long id, String params);

    PageInfo<ProjectDTO> pagingQueryProjectAndRolesById(Pageable Pageable,
                                                        Long id, String params);

    UserDTO createUserAndAssignRoles(CreateUserWithRolesDTO userWithRoles);

    Long[] listUserIds();

    Long queryOrgIdByEmail(String email);

    PageInfo<SimplifiedUserDTO> pagingQueryAllUser(Pageable Pageable, String param, Long organizationId);

    PageInfo<UserDTO> pagingQueryUsersOnSiteLevel(Long userId, String email, Pageable Pageable, String param);

    Map<String, Object> queryAllAndNewUsers();

    PageInfo<UserRoleDTO> pagingQueryRole(Pageable Pageable, Long userId, String name, String level, String params);

    /**
     * 根据loginName集合查询所有用户
     */
    List<UserDTO> listUsersByLoginNames(String[] loginNames, Boolean onlyEnabled);

    /**
     * 异步
     * 向用户发送通知（包括邮件和站内信）
     *
     * @param fromUserId 发送通知的用户
     * @param userIds    接受通知的目标用户
     * @param code       业务code
     * @param params     渲染参数
     * @param sourceId   触发发送通知对应的组织/项目id，如果是site层，可以为0或null
     */
    Future<String> sendNotice(Long fromUserId, List<Long> userIds, String code, Map<String, Object> params, Long sourceId);

    Future<String> sendNotice(Long fromUserId, List<Long> userIds, String code, Map<String, Object> params, Long sourceId, boolean sendAll);

    Future<String> sendNotice(Long fromUserId, Map<Long, Set<Long>> longSetMap, String code, Map<String, Object> params, Long sourceId);

    UserDTO updateUserDisabled(Long userId);


    UserInfoDTO updateUserInfo(Long id, UserInfoDTO userInfoDTO);

    /**
     * 更新用户角色.
     *
     * @param userId      用户Id
     * @param sourceType  资源层级
     * @param sourceId    资源Id
     * @param roleDTOList 角色列表
     * @return 用户DTO
     */
    UserDTO updateUserRoles(Long userId, String sourceType, Long sourceId, List<RoleDTO> roleDTOList);

    /**
     * 创建用户角色.
     *
     * @param userDTO          用户DTO
     * @param roleDTOList      角色列表
     * @param sourceType       资源层级
     * @param sourceId         资源Id
     * @param isEdit           是否为新建操作: 如果为false,则只插入; 否则既插入也删除
     * @param allowRoleEmpty   是否允许用户角色为空
     * @param allowRoleDisable 是否允许用户角色为禁用
     * @return 用户角色DTO列表
     */
    List<MemberRoleDTO> createUserRoles(UserDTO userDTO, List<RoleDTO> roleDTOList, String sourceType, Long sourceId,
                                        boolean isEdit, boolean allowRoleEmpty, boolean allowRoleDisable);

    /**
     * 全局层分页查询用户列表（包括用户信息以及所分配的全局角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的全局角色信息）
     */
    PageInfo<UserDTO> pagingQueryUsersWithRolesOnSiteLevel(Pageable Pageable, String orgName, String loginName, String realName,
                                                           String roleName, Boolean enabled, Boolean locked, String params);

    /**
     * 项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的项目角色信息）
     */
    PageInfo<UserDTO> pagingQueryUsersWithRolesOnProjectLevel(Long projectId, Pageable Pageable, String loginName, String realName,
                                                              String roleName, Boolean enabled, String params);

    /**
     * 在全局层/组织层/项目层 批量分配给用户角色.
     *
     * @param sourceType        资源层级
     * @param sourceId          资源层级
     * @param memberRoleDTOList 用户角色列表
     * @return 用户角色DTO列表
     */
    List<MemberRoleDTO> assignUsersRoles(String sourceType, Long sourceId, List<MemberRoleDTO> memberRoleDTOList);

    List<MemberRoleDTO> assignUsersRoles(String sourceType, Long sourceId, List<MemberRoleDTO> memberRoleDTOList, Boolean syncAll);

    /**
     * 在全局层/组织层/项目层 根据用户名查询启用状态的用户列表.
     *
     * @param sourceType 资源层级
     * @param sourceId   资源Id
     * @param userName   用户名
     * @return 启用状态的用户列表
     */
    List<UserDTO> listEnableUsersByName(String sourceType, Long sourceId, String userName);


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
     * 更新用户信息.
     *
     * @param userDTO 用户DTO
     * @return 更新后的用户信息
     */
    UserDTO updateUser(UserDTO userDTO);

    /**
     * 根据用户Id启用用户.
     *
     * @param userId 用户Id
     * @return 用户DTO
     */
    UserDTO enableUser(Long userId);

    /**
     * 根据用户Id禁用用户.
     *
     * @param userId 用户Id
     * @return 用户DTO
     */
    UserDTO disableUser(Long userId);

    /**
     * 根据用户Id解锁用户.
     *
     * @param userId 用户Id
     * @return 用户DTO
     */
    UserDTO unlockUser(Long userId);

    List<ProjectDTO> queryProjects(Long userId, Boolean includedDisabled);

    OrganizationProjectDTO queryOrganizationProjectByUserId(Long userId);

    List<UserDTO> listEnableUsersByRouteRuleCode(String userName);

    /**
     * 查询用户下指定项目，
     *
     * @param id
     * @param projectId
     * @return null (项目不存在或者用户没有项目权限)
     */
    ProjectDTO queryProjectById(Long id, Long projectId);

    /**
     * 校验用户是否是项目的所有者
     *
     * @param id
     * @param projectId
     * @return true 是
     */
    Boolean checkIsProjectOwner(Long id, Long projectId);


    Boolean checkIsGitlabOrgOwner(Long id, Long projectId);

    /**
     * 校验用户是否是gitlab项目所有者
     *
     * @param id
     * @param projectId
     * @return
     */
    Boolean checkIsGitlabProjectOwner(Long id, Long projectId);

    /**
     * 查询项目下指定角色的用户列表
     *
     * @param projectId
     * @param roleLable
     * @return
     */
    List<UserDTO> listProjectUsersByProjectIdAndRoleLable(Long projectId, String roleLable);

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     *
     * @param projectId
     * @param param
     * @return
     */
    List<UserDTO> listUsersByName(Long projectId, String param);


    /**
     * 查询所有的Root用户
     *
     * @return 所有的root用户
     */
    List<UserDTO> queryAllAdminUsers();

    /**
     * 查询所有的组织管理员
     *
     * @return
     */
    List<UserDTO> queryAllOrgAdmin();

    /**
     * 按时间段统计组织或平台人数
     *
     * @param organizationId 如果为null，则统计平台人数
     * @param startTime
     * @param endTime
     * @return
     */
    UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime);

    /**
     * 校验用户是否是root用户
     *
     * @param id
     * @return
     */
    Boolean checkIsRoot(Long id);

    /**
     * 校验用户是否是组织Root用户
     *
     * @param organizationId
     * @param userId
     * @return
     */
    Boolean checkIsOrgRoot(Long organizationId, Long userId);

    /**
     * 查询项目下的项目所有者
     * @param projectId
     * @return
     */
    List<UserDTO> listProjectOwnerById(Long projectId);
}
