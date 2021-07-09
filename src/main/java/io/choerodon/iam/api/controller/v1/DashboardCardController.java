package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.DashboardCardDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import org.hzero.core.util.Results;
import org.hzero.core.base.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.choerodon.iam.app.service.DashboardCardService;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

/**
 * 管理 API
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@RestController("dashboardCardController.v1")
@RequestMapping("/v1/dashboard-cards")
public class DashboardCardController extends BaseController {

    private DashboardCardService dashboardCardService;

    public DashboardCardController(DashboardCardService dashboardCardService) {
        this.dashboardCardService = dashboardCardService;
    }

    @ApiOperation(value = "维护-分页查询卡片列表")
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @GetMapping
    public ResponseEntity<Page<DashboardCardDTO>> pageDashboardCard(@RequestParam(value = "groupId", required = false) String groupId,
                                                                    @ApiIgnore @SortDefault(value = DashboardCardDTO.FIELD_CARD_ID,
                                                                            direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(dashboardCardService.pageDashboardCard(groupId, pageRequest));
    }
}
