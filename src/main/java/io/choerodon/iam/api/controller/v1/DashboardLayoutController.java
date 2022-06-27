package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.DashboardLayoutVO;
import io.choerodon.iam.app.service.DashboardLayoutService;
import io.choerodon.swagger.annotation.Permission;

/**
 *  管理 API
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@RestController("dashboardLayoutController.v1")
@RequestMapping("/v1/dashboard-layouts")
public class DashboardLayoutController extends BaseController {

    private DashboardLayoutService dashboardLayoutService;

    public DashboardLayoutController(DashboardLayoutService dashboardLayoutService){
        this.dashboardLayoutService = dashboardLayoutService;
    }

    @ApiOperation(value = "维护-查询明细")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping("/{dashboard_id}")
    public ResponseEntity<List<DashboardLayoutVO>> queryLayoutByDashboard(@Encrypt @PathVariable("dashboard_id") Long dashboardId) {
        return Results.success(dashboardLayoutService.queryLayoutByDashboard(dashboardId));
    }
}
