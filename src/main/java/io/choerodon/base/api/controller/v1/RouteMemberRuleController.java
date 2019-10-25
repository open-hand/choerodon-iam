package io.choerodon.base.api.controller.v1;

import io.choerodon.base.app.service.RouteMemberRuleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RouteMemberRuleController
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@RestController
@RequestMapping(value = "/v1/route_member_rules")
public class RouteMemberRuleController {
    private RouteMemberRuleService routeMemberRuleService;

    public RouteMemberRuleController(RouteMemberRuleService routeMemberRuleService) {
        this.routeMemberRuleService = routeMemberRuleService;
    }
}
