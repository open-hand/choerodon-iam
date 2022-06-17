package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.ListLayoutVO;

public interface ListLayoutService {
    ListLayoutVO save(Long organizationId, Long projectId, ListLayoutVO listLayoutVO);

    ListLayoutVO queryByApplyType(Long organizationId, Long projectId, String applyType);
}
