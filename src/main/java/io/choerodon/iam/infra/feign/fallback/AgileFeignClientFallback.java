package io.choerodon.iam.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.infra.feign.AgileFeignClient;

/**
 * @author jiameng.cao
 * @date 2019/7/30
 */
@Component
public class AgileFeignClientFallback implements AgileFeignClient {

//    @Override
//    public ResponseEntity<List<TimeZoneWorkCalendarDTO>> queryTimeZoneWorkCalendarList(Long organizationId) {
//        throw new CommonException("error.agile.queryTimeZoneWorkCalendarList");
//    }
//
//    @Override
//    public ResponseEntity<List<TimeZoneWorkCalendarRefDTO>> queryTimeZoneWorkCalendarRefList(Long organizationId) {
//        throw new CommonException("error.agile.queryTimeZoneWorkCalendarRefList");
//    }
//
//    @Override
//    public ResponseEntity<List<WorkCalendarHolidayRefDTO>> queryWorkCalendarHolidayRelList(Long organizationId) {
//        throw new CommonException("error.agile.queryTimeZoneWorkCalendarHolidayRefList");
//    }
//
//    @Override
//    public ResponseEntity<List<ProductVersionVO>> listByProjectId(Long projectId) {
//        throw new CommonException("error.agile.listByProjectId");
//    }

    @Override
    public ResponseEntity<AgileProjectInfoVO> updateProjectInfo(Long projectId, AgileProjectInfoVO agileProjectInfoVO) {
        throw new CommonException("error.agile.updateProjectInfo");
    }

    @Override
    public ResponseEntity<AgileProjectInfoVO> queryProjectInfoByProjectId(Long projectId) {
        throw new CommonException("error.agile.queryProjectInfoByProjectId");
    }
}
