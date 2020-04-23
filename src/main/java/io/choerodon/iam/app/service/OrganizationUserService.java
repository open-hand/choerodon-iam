package io.choerodon.iam.app.service;


import java.util.List;

import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.ErrorUserVO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author superlee
 * @since 2018/3/26
 */
public interface OrganizationUserService {
    /**
     * 创建用户并分配角色.
     *
     * @param user 用户DTO
     * @return 用户
     */
    User createUserWithRoles(User user);

    void sendUserCreationSaga(Long formUserId, User userDTO, List<Role> userRoles, String value, Long organizationId);

    /**
     * 组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）.
     *
     * @return 用户列表（包括用户信息以及所分配的组织角色信息）
     */
    Page<User> pagingQueryUsersWithRolesOnOrganizationLevel(Long organizationId, PageRequest pageable, String loginName, String realName,
                                                            String roleName, Boolean enabled, Boolean locked, String params);

    User updateUser(User userDTO);

    User resetUserPassword(Long organizationId, Long userId);

    User query(Long organizationId, Long id);

    User unlock(Long organizationId, Long userId);

    User enableUser(Long organizationId, Long userId);

    User disableUser(Long organizationId, Long userId);

    /**
     * ldap 批量同步用户，发送事件
     *
     * @param insertUsers 用户信息列表
     */
    List<LdapErrorUser> batchCreateUsers(List<User> insertUsers);


    List<ErrorUserVO> batchCreateUsersOnExcel(List<User> insertUsers, Long fromUserId, Long organizationId);

    Boolean checkEnableCreateUser(Long organizationId);
}
