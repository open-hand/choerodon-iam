package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.InterceptorChainBuilder;
import org.hzero.core.interceptor.InterceptorChainConfigurer;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.user.interceptor.UserOperation;
import org.hzero.iam.domain.service.user.interceptor.interceptors.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * hzero界面创建用户 创建gitlab用户
 */
@Order(10)
@Component
public class C7nUserInterceptorChainConfigurer implements InterceptorChainConfigurer<User, InterceptorChainBuilder<User>> {

    @Override
    public void configure(InterceptorChainBuilder<User> builder) {
        builder
                .selectChain(UserOperation.CREATE_USER)
                .pre()
                .addInterceptorAfter(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.REGISTER_USER)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.CREATE_USER_INTERNAL)
                .pre()
                .addInterceptorBefore(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .addInterceptorBefore(LdapUserPreInterceptor.class, ValidationInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER_INTERNAL)
                .pre()
                .addInterceptorBefore(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .addInterceptorBefore(LdapUserPreInterceptor.class, ValidationInterceptor.class);

        builder
                .selectChain(UserOperation.CREATE_USER_INTERNAL)
                .post()
                .addInterceptorAfter(LdapUserPostInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.IMPORT_USER)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);
    }
}
