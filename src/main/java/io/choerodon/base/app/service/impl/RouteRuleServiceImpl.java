package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.RouteRuleService;
import io.choerodon.base.infra.mapper.RouteRuleMapper;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@Service
public class RouteRuleServiceImpl implements RouteRuleService {
    private RouteRuleMapper routeRuleMapper;

    public RouteRuleServiceImpl(RouteRuleMapper routeRuleMapper) {
        this.routeRuleMapper = routeRuleMapper;
    }
}
