package io.choerodon.iam.app.task;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.asgard.schedule.annotation.JobParam;
import io.choerodon.asgard.schedule.annotation.JobTask;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.WorkCalendarHolidayRefService;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/2/2
 * @Modified By:
 */
@Component
public class WorkCalendarHolidayJobTask {
    @Autowired
    private WorkCalendarHolidayRefService workCalendarHolidayRefService;

    @JobTask(maxRetryCount = 2, code = "updateWorkCalendarHolidayRefByYear",
            params = {
                    @JobParam(name = "year", defaultValue = "2021", description = "工作日历同步年份", type = Integer.class)
            }, description = "按年份更新日历")
    public void updateWorkCalendarHolidayRefByYear(Map<String, Object> map) {
        Integer year =
                Optional
                        .ofNullable((Integer) map.get("year"))
                        .orElseThrow(() -> new CommonException("error.get.update.cork.calendar.year"));
        workCalendarHolidayRefService.updateWorkCalendarHolidayRefByYear(year);
    }
}
