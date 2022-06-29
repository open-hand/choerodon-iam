package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.ListLayoutVO;

/**
 * @author superlee
 * @since 2021-10-19
 */
public interface OrganizationListLayoutService {

    ListLayoutVO queryByApplyType(Long organizationId, String applyType);

    ListLayoutVO save(Long organizationId, ListLayoutVO listLayoutVO);
}
