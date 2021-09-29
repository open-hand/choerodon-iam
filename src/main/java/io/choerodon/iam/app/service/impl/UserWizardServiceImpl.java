package io.choerodon.iam.app.service.impl;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.UserWizardService;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
@Service
public class UserWizardServiceImpl implements UserWizardService {
    @Override
    public void initUserWizardByTenantId(Long tenantId) {
    }

    @Override
    public void updateUserWizardCompleted(@NotNull Long tenantId, String code) {
    }
}
