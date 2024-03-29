package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;

import java.util.Date;
import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarService {
    /**
     * 更新时区设置
     *
     * @param organizationId               organizationId
     * @param timeZoneId                   timeZoneId
     * @param timeZoneWorkCalendarUpdateVO timeZoneWorkCalendarUpdateVO
     * @return TimeZoneWorkCalendarVO
     */
    TimeZoneWorkCalendarVO updateTimeZoneWorkCalendar(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarUpdateVO timeZoneWorkCalendarUpdateVO);

    /**
     * 创建时区下的工作日历
     *
     * @param organizationId                  organizationId
     * @param timeZoneId                      timeZoneId
     * @param timeZoneWorkCalendarRefCreateVO timeZoneWorkCalendarRefCreateVO
     * @return TimeZoneWorkCalendarRefVO
     */
    TimeZoneWorkCalendarRefVO createTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, TimeZoneWorkCalendarRefCreateVO timeZoneWorkCalendarRefCreateVO);

    /**
     * 删除工作日历
     *
     * @param organizationId organizationId
     * @param calendarId     calendarId
     */
    void deleteTimeZoneWorkCalendarRef(Long organizationId, Long calendarId);

    List<TimeZoneWorkCalendarRefVO> batchUpdateTimeZoneWorkCalendarRef(Long organizationId, Long timeZoneId, List<TimeZoneWorkCalendarRefCreateVO> timeZoneWorkCalendarRefCreateVOList);

    /**
     * 查询时区信息
     *
     * @param organizationId organizationId
     * @return TimeZoneWorkCalendarVO
     */
    TimeZoneWorkCalendarVO queryTimeZoneWorkCalendar(Long organizationId);

    /**
     * 根据时区id获取工作日历
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     * @param year           year
     * @return TimeZoneWorkCalendarRefVO
     */
    List<TimeZoneWorkCalendarRefVO> queryTimeZoneWorkCalendarRefByTimeZoneId(Long organizationId,
                                                                             Long timeZoneId,
                                                                             Integer year,
                                                                             Date startDate,
                                                                             Date endDate);

    /**
     * ¬
     * 获取时区下的工作日历详情
     *
     * @param organizationId organizationId
     * @param year           year
     * @return TimeZoneWorkCalendarRefCreateVO
     */
    TimeZoneWorkCalendarRefDetailVO queryTimeZoneWorkCalendarDetail(Long organizationId,
                                                                    Integer year,
                                                                    Date startDate,
                                                                    Date endDate);

    TimeZoneWorkCalendarDTO create(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO);

    TimeZoneWorkCalendarDTO update(TimeZoneWorkCalendarDTO timeZoneWorkCalendarDTO);

    TimeZoneWorkCalendarDTO queryTimeZoneDetailByOrganizationId(Long organizationId);

    void handleOrganizationInitTimeZone(Long organizationId);
}
