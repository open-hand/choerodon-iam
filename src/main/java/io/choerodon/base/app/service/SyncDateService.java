package io.choerodon.base.app.service;

public interface SyncDateService {
    /**
     * 平滑升级
     *
     * @param version 版本
     */
    void syncDate(String version);
}
