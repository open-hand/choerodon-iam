package io.choerodon.iam.infra.interceptor;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;

import org.hzero.core.interceptor.InterceptorChain;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.service.user.interceptor.UserHandlerInterceptor;
import org.hzero.iam.domain.service.user.interceptor.UserInterceptorChainFactory;
import org.springframework.stereotype.Component;

/**
 * @author scp
 * @date 2020/7/1
 * @description
 */
@Component
public class C7nUserInterceptorChainFactory extends UserInterceptorChainFactory {

    public C7nUserInterceptorChainFactory(Optional<List<UserHandlerInterceptor>> optional) {
        super(optional);
    }

    @Nonnull
    protected InterceptorChain<User> buildCreateUserChain(String chainName) {
        return InterceptorChain.Builder.<User>newBuilder()
                .nextPreInterceptor(getInterceptor("VALIDATION_OF_COMMON"))
                .nextPostInterceptor(getInterceptor("MEMBER_ROLE_OF_COMMON"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG"))
                .nextPostInterceptor(getInterceptor("SEND_MESSAGE"))
                .nextPostInterceptor(getInterceptor("LAST_HANDLER"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG_GITLAB"))
                .setChainName(chainName).postAsync().build();
    }

    @Nonnull
    protected InterceptorChain<User> buildRegisterUserChain(String chainName) {
        return InterceptorChain.Builder.<User>newBuilder()
                .nextPostInterceptor(getInterceptor("MEMBER_ROLE_OF_REGISTER"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG"))
                .nextPostInterceptor(getInterceptor("LAST_HANDLER"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG_GITLAB"))
                .setChainName(chainName).postAsync().build();
    }

    @Nonnull
    protected InterceptorChain<User> buildInternalCreateUserChain(String chainName) {
        return InterceptorChain.Builder.<User>newBuilder()
                .nextPreInterceptor(getInterceptor("VALIDATION_OF_COMMON"))
                .nextPostInterceptor(getInterceptor("MEMBER_ROLE_OF_INTERNAL"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG"))
                .nextPostInterceptor(getInterceptor("LAST_HANDLER"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG_LDAP"))
                .setChainName(chainName).postAsync().build();
    }


    @Nonnull
    protected InterceptorChain<User> buildImportUserChain(String chainName) {
        return InterceptorChain.Builder.<User>newBuilder()
                .nextPostInterceptor(getInterceptor("MEMBER_ROLE_OF_INTERNAL"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG"))
                .nextPostInterceptor(getInterceptor("LAST_HANDLER"))
                .nextPostInterceptor(getInterceptor("USER_CONFIG_GITLAB"))
                .setChainName(chainName).build();
    }
}