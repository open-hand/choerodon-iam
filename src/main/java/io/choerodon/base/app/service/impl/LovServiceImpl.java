package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.LovService;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import org.springframework.stereotype.Service;

@Service
public class LovServiceImpl implements LovService {
    private RouteMapper routeMapper;
    private LovMapper lovMapper;
    private LovGridFieldMapper lovGridFieldMapper;
    private LovQueryFieldMapper lovQueryFieldMapper;
    private PermissionMapper permissionMapper;

    public LovServiceImpl(RouteMapper routeMapper, LovMapper lovMapper, LovGridFieldMapper lovGridFieldMapper, LovQueryFieldMapper lovQueryFieldMapper, PermissionMapper permissionMapper) {
        this.routeMapper = routeMapper;
        this.lovMapper = lovMapper;
        this.lovGridFieldMapper = lovGridFieldMapper;
        this.lovQueryFieldMapper = lovQueryFieldMapper;
        this.permissionMapper = permissionMapper;
    }

    @Override
    public LovDTO queryLovByCode(String code){
        LovDTO example = new LovDTO();
        example.setCode(code);
        LovDTO result = lovMapper.selectOne(example);
        if (result == null){
            throw new CommonException("error.lov.notFound");
        }
        PermissionDTO permissionExample = new PermissionDTO();
        permissionExample.setCode(result.getPermissionCode());
        PermissionDTO permission = permissionMapper.selectOne(permissionExample);
        if (permission != null){
            RouteDTO routeExample = new RouteDTO();
            routeExample.setServiceCode(permission.getServiceCode());
            RouteDTO routeResult = routeMapper.selectOne(routeExample);
            if (routeResult != null){
                result.setUrl(routeResult.getBackendPath().substring(0, routeResult.getBackendPath().length() - 3) + permission.getPath());
            }
            result.setMethod(permission.getMethod());
        }
        LovGridFieldDTO gridExample = new LovGridFieldDTO();
        gridExample.setLovCode(result.getCode());
        result.setGridFields(lovGridFieldMapper.select(gridExample));
        LovQueryFieldDTO queryExample = new LovQueryFieldDTO();
        queryExample.setLovCode(result.getCode());
        result.setQueryFields(lovQueryFieldMapper.select(queryExample));
        return result;
    }
}
