package io.choerodon.iam.app.service;

import java.util.Date;

/**
 * @author scp
 * @since 2022/4/19
 */
public interface BusinessService {
    /**
     * 设置用户项目下进场时间和撤场时间
     * @param projectId
     * @param userId
     * @param scheduleEntryTime
     * @param scheduleExitTime
     */
    void setUserProjectDate(Long projectId, Long userId, String scheduleEntryTime, String scheduleExitTime);

    /**
     * 界面上使用
     * @param projectId
     * @param userId
     * @param scheduleEntryTime
     * @param scheduleExitTime
     */
    void setUserProjectDate(Long projectId, Long userId, Date scheduleEntryTime, Date scheduleExitTime);
}
