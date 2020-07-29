package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.TimeZoneWorkCalendarRefDetailVO;
import io.choerodon.iam.app.service.TimeZoneWorkCalendarService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author jiameng.cao
 * @since 2019/8/20
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WORK_CALENDAR_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/projects/{project_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarProjectC7nController {
    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("获取冲刺有关于组织层时区设置")
    @GetMapping(value = "/time_zone_detail/{organization_id}")
    public ResponseEntity<TimeZoneWorkCalendarRefDetailVO> queryTimeZoneWorkCalendarDetail(@ApiParam(value = "项目id", required = true)
                                                                                           @PathVariable(name = "project_id") Long projectId,
                                                                                           @ApiParam(value = "组织id", required = true)
                                                                                           @PathVariable(name = "organization_id") Long organizationId,
                                                                                           @ApiParam(value = "时间", required = true)
                                                                                           @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarDetail(organizationId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarProjectController.queryTimeZoneWorkCalendarDetail"));
    }
}
