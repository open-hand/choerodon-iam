package io.choerodon.iam.infra.interceptor;

import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.infra.utils.CustomContextUtil;

@Component
public class GitlabUserInterceptor implements HandlerInterceptor<User> {
    private final OrganizationUserService organizationUserService;

    @Autowired
    public GitlabUserInterceptor(OrganizationUserService organizationUserService) {
        this.organizationUserService = organizationUserService;
    }

    @Override
    public void interceptor(User user) {
        CustomContextUtil.setUserContext(0L);
        organizationUserService.sendUserCreationSaga(null, user, null, ResourceLevel.ORGANIZATION.value(), user.getOrganizationId());
    }
}
