package io.choerodon.base.infra.factory;

import io.choerodon.base.app.service.WorkCalendarService;
import io.choerodon.base.app.service.impl.JuheWorkCalendarServiceImpl;
import io.choerodon.base.infra.config.WorkCalendarHolidayProperties;
import io.choerodon.base.infra.mapper.WorkCalendarHolidayRefMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/10
 */
@Component
public class WorkCalendarFactory {

    @Autowired
    private WorkCalendarHolidayProperties workCalendarHolidayProperties;

    @Autowired
    private WorkCalendarHolidayRefMapper workCalendarHolidayRefMapper;

    private static final String JU_HE = "juhe";

    public WorkCalendarService getWorkCalendarHoliday(String type) {
        if (type == null) {
            return null;
        }
        if (type.equals(JU_HE)) {
            return new JuheWorkCalendarServiceImpl(workCalendarHolidayProperties, workCalendarHolidayRefMapper);
        }
        return null;
    }
}
