package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarController {

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("【敏捷专用】根据组织id获取时区工作日历")
    @GetMapping(value = "/query_by_org_id")
    public ResponseEntity<TimeZoneWorkCalendarDTO> queryTimeZoneDetailByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                                       @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneDetailByOrganizationId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneDetailByOrganizationId"));
    }
}
