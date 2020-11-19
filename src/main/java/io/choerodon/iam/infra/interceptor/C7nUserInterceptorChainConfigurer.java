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
        /**
         * 覆盖hzero拦截器 改为非异步
         */
        builder
                .selectChain(UserOperation.CREATE_USER)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(CommonMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(SendMessageInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(CommonMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.REGISTER_USER)
                .post()
                .addInterceptor(RegisterMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.CREATE_USER_INTERNAL)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(InternalMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER_INTERNAL)
                .pre()
                .addInterceptor(ValidationInterceptor.class)
                .post()
                .addInterceptor(LastHandlerInterceptor.class);

        builder
                .selectChain(UserOperation.IMPORT_USER)
                .post()
                .addInterceptor(InternalMemberRoleInterceptor.class)
                .addInterceptor(UserConfigInterceptor.class)
                .addInterceptor(LastHandlerInterceptor.class);

        /**
         * c7n 自定义拦截器
         */
        builder
                .selectChain(UserOperation.CREATE_USER)
                .pre()
                .addInterceptorAfter(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.CREATE_USER_INTERNAL)
                .pre()
                .addInterceptorBefore(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .addInterceptorBefore(LdapUserPreInterceptor.class, ValidationInterceptor.class)
                .post()
                .addInterceptorAfter(LdapUserPostInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER_INTERNAL)
                .pre()
                .addInterceptorBefore(C7nUserEmailInterceptor.class, ValidationInterceptor.class)
                .addInterceptorBefore(LdapUserPreInterceptor.class, ValidationInterceptor.class);

        builder
                .selectChain(UserOperation.UPDATE_USER)
                .pre()
                .addInterceptor(C7nUserEmailInterceptor.class);

        builder
                .selectChain(UserOperation.IMPORT_USER)
                .pre()
                .addInterceptor(C7nUserEmailInterceptor.class)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);

        builder
                .selectChain(UserOperation.REGISTER_USER)
                .pre()
                .addInterceptor(C7nUserEmailInterceptor.class)
                .post()
                .addInterceptorAfter(GitlabUserInterceptor.class, UserConfigInterceptor.class);
    }
}
