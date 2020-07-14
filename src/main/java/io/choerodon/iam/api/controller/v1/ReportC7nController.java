package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.ReportC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ReportDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author bgzyy
 * @since 2019/9/11
 */

@Api(tags = C7nSwaggerApiConfig.CHOERODON_PROJECT_USER)
@RestController
@RequestMapping("/choerodon/v1/projects/{project_id}/report")
public class ReportC7nController {

    private ReportC7nService reportService;

    public ReportC7nController(ReportC7nService reportService) {
        this.reportService = reportService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询报表列表")
    @GetMapping(value = "/list")
    public ResponseEntity<List<ReportDTO>> queryReportList(@Encrypt @PathVariable(value = "project_id") Long projectId) {
        return new ResponseEntity<>(reportService.queryReportList(projectId), HttpStatus.OK);
    }
}
