package io.choerodon.iam.api.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.iam.infra.mapper.TimeZoneWorkCalendarMapper;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class TimeZoneWorkCalendarValidator {

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;

    public void verifyCreateTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        timeZoneWorkCalendarDTO.setTimeZoneId(timeZoneId);
        TimeZoneWorkCalendarDTO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDTO);
        if (query == null) {
            throw new CommonException("error.TimeZoneWorkCalendar.notFound");
        }
    }

    public void verifyDeleteTimeZoneWorkCalendarRef(Long calendarId) {
        if (calendarId == null) {
            throw new CommonException("error.calendarId.empty");
        }
    }
}
