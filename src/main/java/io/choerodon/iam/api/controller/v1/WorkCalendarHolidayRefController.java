package io.choerodon.iam.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.WorkCalendarHolidayRefVO;
import io.choerodon.iam.app.service.WorkCalendarHolidayRefService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author dinghuang123@gmail.com
 * @since 2018/10/9
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WORK_CALENDAR_HOLIDAY_REF)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/work_calendar_holiday_refs")
public class WorkCalendarHolidayRefController {

    @Autowired
    private WorkCalendarHolidayRefService workCalendarHolidayRefService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("按年份更新工作日历假期")
    @PostMapping
    public ResponseEntity<Void> updateWorkCalendarHolidayRefByYear(@ApiParam(value = "项目id", required = true)
                                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                                   @ApiParam(value = "要更新的年份", required = true)
                                                                   @RequestParam Integer year) {
        workCalendarHolidayRefService.updateWorkCalendarHolidayRefByYear(year);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("根据年份查询工作日历假期(包含查询年份和下一年份数据)")
    @GetMapping
    public ResponseEntity<List<WorkCalendarHolidayRefVO>> queryWorkCalendarHolidayRelByYear(@ApiParam(value = "项目id", required = true)
                                                                                            @PathVariable(name = "organization_id") Long organizationId,
                                                                                            @ApiParam(value = "要查询的年份", required = true)
                                                                                            @RequestParam Integer year,
                                                                                            @RequestParam(required = false) Date startDate,
                                                                                            @RequestParam(required = false) Date endDate) {
        return Optional.ofNullable(workCalendarHolidayRefService.queryWorkCalendarHolidayRelByYear(year, startDate, endDate))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.WorkCalendarHolidayRefController.queryWorkCalendarHolidayRelByYear"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("【敏捷专用】根据年份查询工作日历，包含当年、去年、明年")
    @GetMapping(value = "/year_include_last_and_next")
    public ResponseEntity<List<WorkCalendarHolidayRefVO>> queryByYearIncludeLastAndNext(@ApiParam(value = "项目id", required = true)
                                                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                                                        @ApiParam(value = "要查询的年份", required = true)
                                                                                        @RequestParam Integer year) {
        return Optional.ofNullable(workCalendarHolidayRefService.queryByYearIncludeLastAndNext(year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.WorkCalendarHolidayRefController.queryByYearIncludeLastAndNext"));
    }
}
