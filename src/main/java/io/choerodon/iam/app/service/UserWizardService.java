package io.choerodon.iam.app.service;

import java.util.List;

import io.choerodon.iam.api.vo.UserWizardStatusVO;
import io.choerodon.iam.infra.dto.UserWizardDTO;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
public interface UserWizardService {
    /**
     * 查询向导首页
     * @param organizationId
     * @return
     */
    List<UserWizardDTO> listUserWizards(Long organizationId);

    /**
     * 查询组织下向导完成情况
     * @param organizationId
     * @return
     */
    List<UserWizardStatusVO> listUserWizardsStatus(Long organizationId);
}
