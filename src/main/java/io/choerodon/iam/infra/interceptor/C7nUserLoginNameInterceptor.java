package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

/**
 * @author scp
 * @date 2020/11/12
 * @description
 */
@Component
public class C7nUserLoginNameInterceptor implements HandlerInterceptor<User> {
    @Lazy
    @Autowired
    private UserC7nService userC7nService;

    @Autowired
    private UserC7nMapper userC7nMapper;

    @Override
    public void interceptor(User user) {
        userC7nService.checkLoginName(user.getLoginName());
    }

}
