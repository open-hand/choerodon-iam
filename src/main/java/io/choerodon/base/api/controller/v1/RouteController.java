package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.RouteService;
import io.choerodon.base.infra.dto.RouteDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/route")
public class RouteController {
    private RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询路由数据，管理服务内部调用")
    public List<RouteDTO> selectRoute(@RequestParam(name = "name", required = false) String name) {
        return routeService.selectRoute(name);
    }

}
