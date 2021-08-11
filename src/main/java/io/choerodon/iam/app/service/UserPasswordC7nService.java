package io.choerodon.iam.app.service;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/8/11
 * @Modified By:
 */
public interface UserPasswordC7nService {
    /**
     * 更新用户密码
     *
     * @param userId        用户ID
     * @param newPassword   新密码
     * @param ldapUpdatable Ldap 用户是否可更新密码
     */
    void updateUserPassword(Long userId, String newPassword, boolean ldapUpdatable, boolean skipRecentPassword);
}
