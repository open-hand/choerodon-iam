package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.TimeZoneWorkCalendarRefDetailVO;
import io.choerodon.base.app.service.TimeZoneWorkCalendarService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author jiameng.cao
 * @date 2019/8/20
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/time_zone_work_calendars")
public class TimeZoneWorkCalendarProjectController {
    @Autowired
    private TimeZoneWorkCalendarService timeZoneWorkCalendarService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("获取冲刺有关于组织层时区设置")
    @GetMapping(value = "/time_zone_detail/{organization_id}")
    public ResponseEntity<TimeZoneWorkCalendarRefDetailVO> queryTimeZoneWorkCalendarDetail(@ApiParam(value = "项目id", required = true)
                                                                                           @PathVariable(name = "project_id") Long projectId,
                                                                                           @ApiParam(value = "组织id", required = true)
                                                                                           @PathVariable(name = "organization_id") Long organizationId,
                                                                                           @ApiParam(value = "组织id", required = true)
                                                                                           @RequestParam(name = "year") Integer year) {
        return Optional.ofNullable(timeZoneWorkCalendarService.queryTimeZoneWorkCalendarDetail(organizationId, year))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.TimeZoneWorkCalendarProjectController.queryTimeZoneWorkCalendarDetail"));
    }
}
