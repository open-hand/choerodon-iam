package io.choerodon.iam.app.service;

/**
 * @author scp
 * @since 2022/4/19
 */
public interface PluginService {
    /**
     * 设置用户项目下进场时间和撤场时间
     * @param projectId
     * @param userId
     * @param scheduleEntryTime
     * @param scheduleExitTime
     */
    void setUserProjectDate(Long projectId, Long userId, String scheduleEntryTime, String scheduleExitTime);
}
