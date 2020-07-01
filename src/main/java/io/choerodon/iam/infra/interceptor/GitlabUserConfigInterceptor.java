package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.user.interceptor.UserHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OrganizationUserService;

/**
 * @author scp
 * @date 2020/7/1
 * @description
 */
@Component
public class GitlabUserConfigInterceptor implements UserHandlerInterceptor {

    @Autowired
    private OrganizationUserService organizationUserService;


    public GitlabUserConfigInterceptor() {
    }

    @Override
    public String name() {
        return "USER_CONFIG_GITLAB";
    }

    @Override
    public void interceptor(User user) {
        organizationUserService.sendUserCreationSaga(null, user, null, ResourceLevel.ORGANIZATION.value(), user.getOrganizationId());
    }
}

