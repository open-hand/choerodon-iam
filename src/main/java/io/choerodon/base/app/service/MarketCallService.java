package io.choerodon.base.app.service;



import io.choerodon.base.api.vo.AppVersionVO;
import io.choerodon.base.api.vo.PublishedApplicationVO;

import java.util.List;
import java.util.Set;

/**
 * @author wanghao
 * @Date 2019/9/9 10:57
 */
public interface MarketCallService {
    PublishedApplicationVO getPublishedApplication(String appCode, Long organizationId);

    Set<Long> listServiceVersionsByVersionId(Long versionId);

    List<AppVersionVO> listServiceVersionsByVersionIds(Set<Long> versionIds);
}
