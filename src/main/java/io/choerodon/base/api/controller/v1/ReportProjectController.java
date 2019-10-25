package io.choerodon.base.api.controller.v1;

import io.choerodon.base.api.dto.ReportDTO;
import io.choerodon.base.app.service.ReportService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/report")
public class ReportProjectController {

    private ReportService reportService;

    public ReportProjectController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_MEMBER)
    @ApiOperation(value = "查询报表列表")
    @GetMapping(value = "/list")
    public ResponseEntity<List<ReportDTO>> queryReportList() {
        return new ResponseEntity<>(reportService.queryReportList(), HttpStatus.OK);
    }
}
