package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.iam.infra.utils.RandomInfoGenerator;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/5/11
 * @Modified By:
 */
@Component
public class C7nUserLoginNameInterceptor implements HandlerInterceptor<User> {
    @Autowired
    private RandomInfoGenerator randomInfoGenerator;

    @Override
    public void interceptor(User user) {
        user.setLoginName(randomInfoGenerator.randomLoginName());
    }
}
