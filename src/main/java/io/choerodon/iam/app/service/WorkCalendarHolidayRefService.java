package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.WorkCalendarHolidayRefVO;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public interface WorkCalendarHolidayRefService {

    /**
     * 根据年份查询工作日历
     *
     * @param year year
     * @return WorkCalendarHolidayRefVO
     */
    List<WorkCalendarHolidayRefVO> queryWorkCalendarHolidayRelByYear(Integer year);

    /**
     * 根据年份查询工作日历，包含当年、去年、明年
     *
     * @param year year
     * @return WorkCalendarHolidayRefVO
     */
    List<WorkCalendarHolidayRefVO> queryByYearIncludeLastAndNext(Integer year);

}
