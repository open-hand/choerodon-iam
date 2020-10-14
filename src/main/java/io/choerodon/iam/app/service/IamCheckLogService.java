package io.choerodon.iam.app.service;

/**
 * Created by wangxiang on 2020/9/15
 */
public interface IamCheckLogService {
    /**
     * 平滑升级
     *
     * @param version 版本
     */
    void checkLog(String version);
}
