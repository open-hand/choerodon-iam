package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.iam.app.service.UserWizardService;
import io.choerodon.iam.infra.enums.UserWizardStepEnum;

/**
 * @Author: scp
 * @Description: 创建用户完成 用户向导步骤状态更新
 * @Date: Created in 2021/9/28
 * @Modified By:
 */
@Component
public class UserWizardInterceptor implements HandlerInterceptor<User> {
    @Autowired
    private UserWizardService userWizardService;

    @Override
    public void interceptor(User obj) {
        userWizardService.updateUserWizardCompleted(obj.getOrganizationId(), UserWizardStepEnum.CREATE_USER.value());
    }
}
