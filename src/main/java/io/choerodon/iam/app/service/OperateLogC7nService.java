package io.choerodon.iam.app.service;

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public interface OperateLogC7nService {
    Page<OperateLogVO> listOperateLog(PageRequest pageRequest, Long sourceId);

    void siteRetry(Long sourceId, long id);

    void orgRetry(Long sourceId, long id);
}
