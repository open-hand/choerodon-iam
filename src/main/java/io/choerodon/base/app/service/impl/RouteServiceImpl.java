package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.RouteService;
import io.choerodon.base.infra.dto.RouteDTO;
import io.choerodon.base.infra.mapper.RouteMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteServiceImpl implements RouteService {
    private RouteMapper routeMapper;

    public RouteServiceImpl(RouteMapper routeMapper) {
        this.routeMapper = routeMapper;
    }

    /**
     * 查询路由
     * @param name 前端服务名，如 base，为null则查询所有路由
     * @return 查询到的路由信息
     */
    @Override
    public List<RouteDTO> selectRoute(String name) {
        RouteDTO example = new RouteDTO();
        if (StringUtils.isNotEmpty(name)){
            example.setBackendPath(String.format("/%s/**", name));
        }
        return routeMapper.select(example);
    }
}
