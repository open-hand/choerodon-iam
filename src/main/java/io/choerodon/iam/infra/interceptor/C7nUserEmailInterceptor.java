package io.choerodon.iam.infra.interceptor;

import java.util.List;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.validator.UserValidator;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

/**
 * @author scp
 * @date 2020/11/12
 * @description
 */
@Component
public class C7nUserEmailInterceptor implements HandlerInterceptor<User> {
    @Lazy
    @Autowired
    private UserC7nService userC7nService;

    @Autowired
    private UserC7nMapper userC7nMapper;

    @Override
    public void interceptor(User user) {
        UserValidator.validateEmail(user.getEmail());
        userC7nService.check(user);
    }

}
