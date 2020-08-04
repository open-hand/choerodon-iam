package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.infra.utils.CustomContextUtil;

@Component
public class LdapUserPostInterceptor implements HandlerInterceptor<User> {
    private final OrganizationUserService organizationUserService;

    @Autowired
    public LdapUserPostInterceptor(OrganizationUserService organizationUserService) {
        this.organizationUserService = organizationUserService;
    }

    @Override
    public void interceptor(User user) {
        if (user.getLdap() == null || !user.getLdap()) {
            return;
        }
        CustomUserDetails userDetails = UserUtils.getUserDetails();
        if (userDetails == null || userDetails.getUserId() == null) {
            CustomContextUtil.setUserContext(0L);
        }
        organizationUserService.sendUserCreationSaga(null, user, null, ResourceLevel.ORGANIZATION.value(), user.getOrganizationId());
    }
}
