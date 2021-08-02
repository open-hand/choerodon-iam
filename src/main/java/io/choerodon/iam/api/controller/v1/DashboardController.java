package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.DashboardVO;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.choerodon.iam.app.service.DashboardService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * 管理 API
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@RestController("dashboardController.v1")
@RequestMapping("/v1/dashboards")
public class DashboardController extends BaseController {

    private DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @ApiOperation(value = "维护-创建视图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping
    public ResponseEntity<DashboardDTO> createDashboard(@Encrypt @RequestBody DashboardDTO dashboard) {
        validObject(dashboard);
        return Results.success(dashboardService.createDashboard(dashboard));
    }

    @ApiOperation(value = "维护-更新视图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PutMapping
    public ResponseEntity<DashboardDTO> updateDashboard(@Encrypt @RequestBody DashboardVO dashboard) {
        return Results.success(dashboardService.updateDashboard(dashboard));
    }

    @ApiOperation(value = "维护-删除视图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @DeleteMapping("/{dashboard_id}")
    public ResponseEntity<DashboardDTO> deleteDashboard(@Encrypt @PathVariable("dashboard_id") Long dashboardId) {
        return Results.success(dashboardService.deleteDashboard(dashboardId));
    }

    @ApiOperation(value = "查询-用户视图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<DashboardDTO>> queryDashboard() {
        return Results.success(dashboardService.queryDashboard());
    }

    @ApiOperation(value = "查询-官方视图")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/internal")
    public ResponseEntity<Page<DashboardDTO>> queryInternalDashboard(@RequestParam("filterFlag") Integer filterFlag,
                                                                     @ApiIgnore @SortDefault(
                                                                             value = DashboardDTO.FIELD_DASHBOARD_ID,
                                                                             direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(dashboardService.queryInternalDashboard(filterFlag, pageRequest));
    }
}