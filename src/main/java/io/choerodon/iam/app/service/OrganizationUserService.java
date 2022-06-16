package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.iam.api.vo.UserSearchVO;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author superlee
 * @since 2018/3/26
 */
public interface OrganizationUserService {

    /**
     * 创建用户并分配角色.
     *
     * @param fromUserId 操作人的id
     * @param user       用户DTO
     * @return 用户
     */
    User createUserWithRoles(Long fromUserId, User user);

    /**
     * 创建用户并分配角色.
     *
     * @param organizationId 组织Id
     * @param user           用户DTO
     * @param checkPassword  是否校验密码策略
     * @param checkRoles     是否校验角色必输
     * @return 用户DTO
     */
    User createUserWithRoles(Long organizationId, User user, boolean checkPassword, boolean checkRoles);

    void sendCreateUserAndUpdateRoleSaga(Long formUserId, User user, List<Role> userRoles, String value, Long organizationId);

    void sendUserCreationSaga(Long formUserId, User user, List<Role> userRoles, String value, Long organizationId);

    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    Page<UserDTO> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, PageRequest pageRequest, UserSearchVO userSearchVO);

    Page<UserDTO> setUserRoleAndSagaInfo(Page<User> userPage, Long organizationId, String roleName);

    void updateUser(Long organizationId, User user);

    void updateUser(Long organizationId, User user, Boolean updateRoles);

    void setUserInfo(User user);

    User resetUserPassword(Long organizationId, Long userId);

    String getDefaultPassword(Long organizationId);

    User query(Long organizationId, Long id);

    User unlock(Long organizationId, Long userId);

    User enableUser(Long organizationId, Long userId);

    User disableUser(Long organizationId, Long userId);

    List<ErrorUserVO> batchCreateUsersOnExcel(List<UserDTO> insertUsers, Long fromUserId, Long organizationId);

    /**
     * 获取默认密码
     *
     * @param organizationId
     * @return
     */
    String queryDefaultPassword(Long organizationId);

    Page<UserDTO> pagingUsersOnOrganizationLevel(Long organizationId, PageRequest pageable, AgileUserVO agileUserVO);
}
