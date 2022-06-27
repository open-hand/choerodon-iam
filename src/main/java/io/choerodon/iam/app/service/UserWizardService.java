package io.choerodon.iam.app.service;

import javax.validation.constraints.NotNull;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
public interface UserWizardService {
    /**
     * 创建组织时初始化组织的用户向导
     *
     * @param tenantId
     */
    void initUserWizardByTenantId(Long tenantId);

    /**
     * 更新步骤完整状态
     * @param tenantId
     * @param code 步骤code
     */
    void updateUserWizardCompleted(@NotNull Long tenantId, String code);
}
