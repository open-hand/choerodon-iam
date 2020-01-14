package io.choerodon.base.app.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.QuartzDefinition;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.asgard.schedule.annotation.TimedTask;
import io.choerodon.base.app.service.SyncDateService;

@Component
public class SyncDateTask {
    @Autowired
    private SyncDateService syncDateService;

    /**
     * 迁移数据
     */
    @JobTask(maxRetryCount = 3, code = "baseUpgradeVersionTo21", description = "base升级0.20.0-0.21.0，迁移数据")
    @TimedTask(name = "baseUpgradeVersionTo20", description = "base升级0.20.0-0.21.0，迁移数据", oneExecution = true,
            repeatCount = 0, repeatInterval = 1, repeatIntervalUnit = QuartzDefinition.SimpleRepeatIntervalUnit.HOURS, params = {})
    public void syncDate() {
        syncDateService.syncDate("0.21.0");
    }
}
