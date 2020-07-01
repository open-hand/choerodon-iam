package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.user.interceptor.UserHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OrganizationUserService;
import io.choerodon.iam.infra.utils.CustomContextUtil;

/**
 * @author scp
 * @date 2020/7/1
 * @description
 */
@Component
public class LdapUserConfigInterceptor implements UserHandlerInterceptor {

    @Autowired
    private OrganizationUserService organizationUserService;


    public LdapUserConfigInterceptor() {
    }

    @Override
    public String name() {
        return "USER_CONFIG_LDAP";
    }

    @Override
    public void interceptor(User user) {
        if (user.getLdap() == null || !user.getLdap()) {
            return;
        }
        CustomContextUtil.setUserContext(0L);
        organizationUserService.sendUserCreationSaga(null, user, null, ResourceLevel.ORGANIZATION.value(), user.getOrganizationId());
    }
}

