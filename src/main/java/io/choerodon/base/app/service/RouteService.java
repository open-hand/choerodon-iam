package io.choerodon.base.app.service;

import io.choerodon.base.infra.dto.RouteDTO;

import java.util.List;

public interface RouteService {
    List<RouteDTO> selectRoute(String name);
}
