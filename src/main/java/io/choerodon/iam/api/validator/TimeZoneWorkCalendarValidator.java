package io.choerodon.iam.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.constant.MisConstants;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.iam.infra.mapper.TimeZoneWorkCalendarMapper;
import io.choerodon.iam.infra.mapper.TimeZoneWorkCalendarRefMapper;
import io.choerodon.iam.infra.utils.CommonExAssertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by HuangFuqiang@choerodon.io on 2019/7/8.
 * Email: fuqianghuang01@gmail.com
 */
@Component
public class TimeZoneWorkCalendarValidator {

    @Autowired
    private TimeZoneWorkCalendarMapper timeZoneWorkCalendarMapper;
    @Autowired
    private TimeZoneWorkCalendarRefMapper timeZoneWorkCalendarRefMapper;

    public void verifyCreateTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId) {
        TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO = new TimeZoneWorkCalendarDTO();
        timeZoneWorkCalendarDTO.setOrganizationId(organizationId);
        timeZoneWorkCalendarDTO.setTimeZoneId(timeZoneId);
        TimeZoneWorkCalendarDTO query = timeZoneWorkCalendarMapper.selectOne(timeZoneWorkCalendarDTO);
        if (query == null) {
            throw new CommonException("error.TimeZoneWorkCalendar.notFound");
        }
    }

    public void verifyDeleteTimeZoneWorkCalendarRef(Long organizationId, Long calendarId) {
        TimeZoneWorkCalendarRefDTO timeZoneWorkCalendarRefDTO = timeZoneWorkCalendarRefMapper.selectByPrimaryKey(calendarId);
        CommonExAssertUtil.assertTrue(organizationId.equals(timeZoneWorkCalendarRefDTO.getOrganizationId()), MisConstants.ERROR_OPERATING_RESOURCE_IN_OTHER_ORGANIZATION);

        if (calendarId == null) {
            throw new CommonException("error.calendarId.empty");
        }
    }
}
