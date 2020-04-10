package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.base.api.dto.ReportDTO;
import io.choerodon.base.app.service.ReportService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/report")
public class ReportProjectC7nController {

    private ReportService reportService;

    public ReportProjectC7nController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_MEMBER)
    @ApiOperation(value = "查询报表列表")
    @GetMapping(value = "/list")
    public ResponseEntity<List<ReportDTO>> queryReportList(@PathVariable(value = "project_id") Long projectId) {
        return new ResponseEntity<>(reportService.queryReportList(projectId), HttpStatus.OK);
    }
}
