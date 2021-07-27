package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.iam.infra.dto.DashboardUserDTO;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.choerodon.iam.app.service.DashboardUserService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 * 管理 API
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@RestController("dashboardUserController.v1")
@RequestMapping("/v1/dashboard-users")
public class DashboardUserController extends BaseController {

    private DashboardUserService dashboardUserService;

    public DashboardUserController(DashboardUserService dashboardUserService) {
        this.dashboardUserService = dashboardUserService;
    }

    @ApiOperation(value = "维护-分配视图给用户")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping
    public ResponseEntity<DashboardUserDTO> createDashboardUser(@Encrypt @RequestBody DashboardUserDTO dashboardUser) {
        validObject(dashboardUser);
        return Results.success(dashboardUserService.createDashboardUser(dashboardUser));
    }

    @ApiOperation(value = "维护-批量更新用户视图顺序")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @PostMapping("/dashboard-rank")
    public ResponseEntity<List<DashboardDTO>> batchUpdateDashboardUserRank(@Encrypt @RequestBody List<DashboardUserDTO> dashboardUserS) {
        return Results.success(dashboardUserService.batchUpdateDashboardUserRank(dashboardUserS));
    }
}
