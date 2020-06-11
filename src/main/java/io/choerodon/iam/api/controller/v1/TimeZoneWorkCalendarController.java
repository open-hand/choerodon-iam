package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WORK_CALENDAR)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarController {

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取时区设置")
    @GetMapping
    public ResponseEntity<TimeZoneWorkCalendarVO> queryTimeZoneWorkCalendar(@ApiParam(value = "组织id", required = true)
                                                                            @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendar(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendar"));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("修改时区设置")
    @PutMapping(value = "/{timeZoneId}")
    public ResponseEntity<TimeZoneWorkCalendarVO> updateTimeZoneWorkCalendar(@ApiParam(value = "组织id", required = true)
                                                                             @PathVariable(name = "organization_id") Long organizationId,
                                                                             @ApiParam(value = "时区id", required = true)
                                                                             @PathVariable(name = "timeZoneId") Long timeZoneId,
                                                                             @ApiParam(value = "timeZoneWorkCalendar", required = true)
                                                                             @RequestBody TimeZoneWorkCalendarUpdateVO timeZoneWorkCalendarUpdateVO) {
        return Optional.ofNullable(timeZoneWorkCalendarService.updateTimeZoneWorkCalendar(organizationId, timeZoneId, timeZoneWorkCalendarUpdateVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.updateTimeZoneWorkCalendar"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("批量创建或删除时区工作日历")
    @PutMapping("/ref/batch/{timeZoneId}")
    public ResponseEntity<List<TimeZoneWorkCalendarRefVO>> batchUpdateTimeZoneWorkCalendarRef(@ApiParam(value = "组织id", required = true)
                                                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                                                              @ApiParam(value = "时区id", required = true)
                                                                                              @PathVariable(name = "timeZoneId") Long timeZoneId,
                                                                                              @ApiParam(value = "日期列表", required = true)
                                                                                              @RequestBody List<TimeZoneWorkCalendarRefCreateVO> timeZoneWorkCalendarRefCreateVOList) {
        return new ResponseEntity<>(timeZoneWorkCalendarService.batchUpdateTimeZoneWorkCalendarRef(organizationId, timeZoneId, timeZoneWorkCalendarRefCreateVOList), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取时区工作日历")
    @GetMapping(value = "/ref/{timeZoneId}")
    public ResponseEntity<List<TimeZoneWorkCalendarRefVO>> queryTimeZoneWorkCalendarRefByTimeZoneId(@ApiParam(value = "组织id", required = true)
                                                                                                    @PathVariable(name = "organization_id") Long organizationId,
                                                                                                    @ApiParam(value = "时区id", required = true)
                                                                                                    @PathVariable(name = "timeZoneId") Long timeZoneId,
                                                                                                    @ApiParam(value = "年份", required = true)
                                                                                                    @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarRefByTimeZoneId(organizationId, timeZoneId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendarRefByTimeZoneId"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取时区下的工作日历")
    @GetMapping(value = "/detail")
    public ResponseEntity<TimeZoneWorkCalendarRefDetailVO> queryTimeZoneWorkCalendarDetail(@ApiParam(value = "组织id", required = true)
                                                                                           @PathVariable(name = "organization_id") Long organizationId,
                                                                                           @ApiParam(value = "年份", required = true)
                                                                                           @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarDetail(organizationId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendarDetail"));
    }

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
