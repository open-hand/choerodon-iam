package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.ErrorUserDTO;
import io.choerodon.base.infra.dto.LdapErrorUserDTO;
import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.UserDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * @author superlee
 * @since 2018/3/26
 */
public interface OrganizationUserService {

    /**
     * 创建用户并分配角色.
     *
     * @param organizationId 组织Id
     * @param userDTO        用户DTO
     * @param checkPassword  是否校验密码策略
     * @param checkRoles     是否校验角色必输
     * @return 用户DTO
     */
    UserDTO createUserWithRoles(Long organizationId, UserDTO userDTO, boolean checkPassword, boolean checkRoles);

    UserDTO createUserAndUpdateRole(Long formUserId, UserDTO userDTO, List<RoleDTO> userRoles, String value, Long organizationId);

    UserDTO createUser(UserDTO userDTO);

    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    PageInfo<UserDTO> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, Pageable pageable, String loginName, String realName,
                                                                   String roleName, Boolean enabled, Boolean locked, String params);

    UserDTO update(Long organizationId, UserDTO userDTO);

    UserDTO resetUserPassword(Long organizationId, Long userId);

    void delete(Long organizationId, Long id);

    UserDTO query(Long organizationId, Long id);

    UserDTO unlock(Long organizationId, Long userId);

    UserDTO enableUser(Long organizationId, Long userId);

    UserDTO disableUser(Long organizationId, Long userId);

    /**
     * ldap 批量同步用户，发送事件
     *
     * @param insertUsers 用户信息列表
     */
    List<LdapErrorUserDTO> batchCreateUsers(List<UserDTO> insertUsers);

    List<Long> listUserIds(Long organizationId);

    UserDTO createUserWithRoles(UserDTO userDTO, Long organizationId, Long fromUserId);

    List<ErrorUserDTO> batchCreateUsersOnExcel(List<UserDTO> insertUsers, Long fromUserId, Long organizationId);

    /**
     * 校验组织是否还能新增用户
     * @param organizationId
     * @param userNumber
     */
    void checkEnableCreateUser(Long organizationId, int userNumber);
}
