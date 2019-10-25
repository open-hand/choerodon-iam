package io.choerodon.base.api.controller.v1;

import java.util.List;
import java.util.Optional;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.*;
import io.choerodon.base.app.service.TimeZoneWorkCalendarService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.TimeZoneWorkCalendarDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/12
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarController {

    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("获取时区设置")
    @GetMapping
    public ResponseEntity<TimeZoneWorkCalendarVO> queryTimeZoneWorkCalendar(@ApiParam(value = "组织id", required = true)
                                                                            @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendar(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneWorkCalendar"));
    }


    @Permission(type = ResourceType.ORGANIZATION)
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

    @Permission(type = ResourceType.ORGANIZATION)
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

    @Permission(type = ResourceType.ORGANIZATION)
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

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
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

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("【敏捷专用】根据组织id获取时区工作日历")
    @GetMapping(value = "/query_by_org_id")
    public ResponseEntity<TimeZoneWorkCalendarDTO> queryTimeZoneDetailByOrganizationId(@ApiParam(value = "组织id", required = true)
                                                                                       @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneDetailByOrganizationId(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarController.queryTimeZoneDetailByOrganizationId"));
    }
}
