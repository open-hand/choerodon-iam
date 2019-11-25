package io.choerodon.base.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.base.api.vo.ProductVersionVO;
import io.choerodon.base.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.base.infra.dto.TimeZoneWorkCalendarRefDTO;
import io.choerodon.base.infra.dto.WorkCalendarHolidayRefDTO;
import io.choerodon.base.infra.feign.AgileFeignClient;
import io.choerodon.core.exception.CommonException;

/**
 * @author jiameng.cao
 * @date 2019/7/30
 */
@Component
public class AgileFeignClientFallback implements AgileFeignClient {

    @Override
    public ResponseEntity<List<TimeZoneWorkCalendarDTO>> queryTimeZoneWorkCalendarList(Long organizationId) {
        throw new CommonException("error.agile.queryTimeZoneWorkCalendarList");
    }

    @Override
    public ResponseEntity<List<TimeZoneWorkCalendarRefDTO>> queryTimeZoneWorkCalendarRefList(Long organizationId) {
        throw new CommonException("error.agile.queryTimeZoneWorkCalendarRefList");
    }

    @Override
    public ResponseEntity<List<WorkCalendarHolidayRefDTO>> queryWorkCalendarHolidayRelList(Long organizationId) {
        throw new CommonException("error.agile.queryTimeZoneWorkCalendarHolidayRefList");
    }

    @Override
    public ResponseEntity<List<ProductVersionVO>> listByProjectId(Long projectId) {
        throw new CommonException("error.agile.listByProjectId");
    }
}
