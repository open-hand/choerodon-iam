package io.choerodon.iam.service;

/**
 * @author Eugen
 */
public interface DemoRegisterC7nService {
    /**
     * 校验邮箱是否存在(base/gitlab)
     *
     * @param email 邮箱
     */
    void checkEmail(String email);
}
