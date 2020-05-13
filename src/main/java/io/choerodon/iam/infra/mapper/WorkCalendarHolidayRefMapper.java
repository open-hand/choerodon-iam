package io.choerodon.iam.infra.mapper;


import io.choerodon.iam.infra.dto.WorkCalendarHolidayRefDTO;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
public interface WorkCalendarHolidayRefMapper extends BaseMapper<WorkCalendarHolidayRefDTO> {

    /**
     * 根据年份查询节假日
     *
     * @param year year
     * @return WorkCalendarHolidayRefDTO
     */
    List<WorkCalendarHolidayRefDTO> queryWorkCalendarHolidayRelWithNextYearByYear(Integer year);
}
