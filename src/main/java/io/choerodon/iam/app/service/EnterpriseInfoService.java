package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.EnterpriseInfoVO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:12
 */
public interface EnterpriseInfoService {

    Boolean checkEnterpriseInfoComplete();

    void saveEnterpriseInfo(EnterpriseInfoVO enterpriseInfoVO);

    /**
     * 校验是否能够修改组织编码，组织存在项目则不能修改
     * @return
     */
    Boolean checkEnableUpdateTenantNum();

}
