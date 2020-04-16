package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.infra.dto.ReportDTO;
import io.choerodon.iam.app.service.ReportC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/report")
public class ReportProjectC7nController {

    private ReportC7nService reportService;

    public ReportProjectC7nController(ReportC7nService reportService) {
        this.reportService = reportService;
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_MEMBER)
    @ApiOperation(value = "查询报表列表")
    @GetMapping(value = "/list")
    public ResponseEntity<List<ReportDTO>> queryReportList(@PathVariable(value = "project_id") Long projectId) {
        return new ResponseEntity<>(reportService.queryReportList(projectId), HttpStatus.OK);
    }
}
