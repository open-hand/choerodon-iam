package io.choerodon.iam.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hzero.iam.domain.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.api.vo.UserNumberVO;
import io.choerodon.iam.api.vo.UserWithGitlabIdVO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserWithGitlabIdDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/4/1
 * @description
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
     * @return List<UserDTO> 用户集合
     */
    List<User> listUsersByIds(Long[] ids, Boolean onlyEnabled);

    /**
     * 根据用户id集合查询用户的集合
     *
     * @param ids         用户id
     * @param onlyEnabled 默认为true，只查询启用的用户
     * @return List<UserDTO> 用户集合
     */
    List<UserWithGitlabIdVO> listUsersByIds(Set<Long> ids, Boolean onlyEnabled);

    /**
     * 根据用户emails集合查询用户的集合
     *
     * @param emails 用户email数组
     * @return List<UserDTO> 用户集合
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
     * @return
     */
    UserNumberVO countByDate(Long organizationId, Date startTime, Date endTime);


    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    List<UserWithGitlabIdDTO> listUsersWithRolesAndGitlabUserIdByIdsInOrg(Long organizationId, Set<Long> userIds);

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

    Page<User> pagingQueryAdminUsers(PageRequest pageRequest, String loginName, String realName, String params);

    void addAdminUsers(long[] ids);

    void deleteAdminUser(long id);


    Page<TenantVO> pagingQueryOrganizationsWithRoles(PageRequest pageRequest,
                                                     Long id, String params);

    Page<ProjectDTO> pagingQueryProjectAndRolesById(PageRequest pageRequest,
                                                    Long id, String params);


    /**
     * 校验用户是否是root用户
     *
     * @param id
     * @return
     */
    Boolean checkIsRoot(Long id);

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

}