package io.choerodon.iam.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.mybatis.common.BaseMapper;


/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/15
 */
public interface TimeZoneWorkCalendarRefMapper extends BaseMapper<TimeZoneWorkCalendarRefDTO> {

    /**
     * 按年份获取今年和下一年的时区工作日期
     *
     * @param organizationId organizationId
     * @param timeZoneId     timeZoneId
     * @param year           year
     * @return TimeZoneWorkCalendarRefDTO
     */
    List<TimeZoneWorkCalendarRefDTO> queryWithNextYearByYear(@Param("organizationId") Long organizationId,
                                                             @Param("timeZoneId") Long timeZoneId,
                                                             @Param("year") Integer year,
                                                             @Param("startDate") Date startDate,
                                                             @Param("endDate") Date endDate);
}
