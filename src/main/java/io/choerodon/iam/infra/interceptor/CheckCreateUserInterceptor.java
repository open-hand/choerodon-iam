package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.iam.app.service.OrganizationResourceLimitService;

/**
 * @author scp
 * @date 2020/11/12
 * @description
 */
@Component
public class CheckCreateUserInterceptor implements HandlerInterceptor<User> {
    @Autowired
    private OrganizationResourceLimitService organizationResourceLimitService;

    @Override
    public void interceptor(User user) {
        organizationResourceLimitService.checkEnableCreateUserOrThrowE(user.getOrganizationId(), 1);
    }
}
