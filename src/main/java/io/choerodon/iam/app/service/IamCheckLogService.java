package io.choerodon.iam.app.service;

/**
 * User: Mr.Wang
 * Date: 2020/6/24
 */
public interface IamCheckLogService {

    /**
     * 平滑升级
     *
     * @param version 版本
     */
    void checkLog(String version);
}
