package io.choerodon.iam.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.choerodon.iam.infra.feign.fallback.AgileFeignClientFallback;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;

/**
 * @author jiameng.cao
 * @date 2019/7/30
 */
@FeignClient(value = "agile-service", fallback = AgileFeignClientFallback.class)
public interface AgileFeignClient {

//    @GetMapping("/v1/organizations/{organization_id}/time_zone_work_calendars/list")
//    ResponseEntity<List<TimeZoneWorkCalendarDTO>> queryTimeZoneWorkCalendarList(@PathVariable(name = "organization_id") Long organizationId);
//
//    @GetMapping("/v1/organizations/{organization_id}/time_zone_work_calendars/ref/list")
//    ResponseEntity<List<TimeZoneWorkCalendarRefDTO>> queryTimeZoneWorkCalendarRefList(@PathVariable(name = "organization_id") Long organizationId);
//
//    @GetMapping("/v1/organizations/{organization_id}/work_calendar_holiday_refs/list")
//    ResponseEntity<List<WorkCalendarHolidayRefDTO>> queryWorkCalendarHolidayRelList(@PathVariable(name = "organization_id") Long organizationId);

//    @GetMapping("/v1/projects/{project_id}/product_version/versions")
//    ResponseEntity<List<ProductVersionVO>> listByProjectId(@PathVariable(name = "project_id") Long projectId);

    @PutMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<AgileProjectInfoVO> updateProjectInfo(@PathVariable(name = "project_id") Long projectId, @RequestBody AgileProjectInfoVO agileProjectInfoVO);

    @GetMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<AgileProjectInfoVO> queryProjectInfoByProjectId(@PathVariable(name = "project_id") Long projectId);
}
