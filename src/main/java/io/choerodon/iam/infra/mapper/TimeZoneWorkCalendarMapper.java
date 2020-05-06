package io.choerodon.iam.infra.mapper;


import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
public interface TimeZoneWorkCalendarMapper extends BaseMapper<TimeZoneWorkCalendarDTO> {

    /**
     * 查询时区详情
     *
     * @param organizationId organizationId
     * @return TimeZoneWorkCalendarDTO
     */
    TimeZoneWorkCalendarDTO queryTimeZoneDetailByOrganizationId(@Param("organizationId") Long organizationId);
}
