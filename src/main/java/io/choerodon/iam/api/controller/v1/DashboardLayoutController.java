package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.api.vo.DashboardLayoutVO;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.choerodon.iam.app.service.DashboardLayoutService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;

import java.util.List;

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
