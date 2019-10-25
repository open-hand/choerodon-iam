package io.choerodon.base.api.controller.v1;

import io.choerodon.base.app.service.RouteRuleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RouteRuleController
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@RestController
@RequestMapping(value = "/v1/route_rules")
public class RouteRuleController {
    private RouteRuleService routeRuleService;

    public RouteRuleController(RouteRuleService routeRuleService) {
        this.routeRuleService = routeRuleService;
    }
}
