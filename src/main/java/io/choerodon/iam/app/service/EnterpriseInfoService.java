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

}
