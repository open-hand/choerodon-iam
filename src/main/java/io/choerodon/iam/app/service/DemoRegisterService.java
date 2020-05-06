package io.choerodon.iam.app.service;

/**
 * @author Eugen
 */
public interface DemoRegisterService {
    /**
     * 校验邮箱是否存在(base/gitlab)
     *
     * @param email 邮箱
     */
    void checkEmail(String email);
}
