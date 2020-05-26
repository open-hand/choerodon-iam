package io.choerodon.iam.app.service;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public interface OperateLogService {
    void siteRetry(Long sourceId, long id);

    void orgRetry(Long sourceId, long id);
}
