package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.RouteMemberRuleService;
import io.choerodon.base.infra.mapper.RouteMemberRuleMapper;
import org.springframework.stereotype.Service;

/**
 * RouteMemberRuleServiceImpl
 *
 * @author pengyuhua
 * @date 2019/10/25
 */
@Service
public class RouteMemberRuleServiceImpl implements RouteMemberRuleService {
    private RouteMemberRuleMapper routeMemberRuleMapper;

    public RouteMemberRuleServiceImpl(RouteMemberRuleMapper routeMemberRuleMapper) {
        this.routeMemberRuleMapper = routeMemberRuleMapper;
    }
}
