package io.choerodon.iam.app.service;

import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarService {
    TimeZoneWorkCalendarDTO queryTimeZoneDetailByOrganizationId(Long organizationId);
}
