package io.choerodon.iam.service.impl;

import java.util.List;

import org.hzero.iam.domain.entity.Permission;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.iam.infra.mapper.PermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.PermissionC7nService;
import io.choerodon.base.infra.utils.ConvertUtils;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class PermissionC7nServiceImpl implements PermissionC7nService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public List<Permission> query(String level, String serviceName, String code) {
        PermissionVO permissionVO = new PermissionVO();
        permissionVO.setFdLevel(level);
        permissionVO.setCode(code);
        permissionVO.setServiceName(serviceName);
        return ConvertUtils.convertList(permissionMapper.selectApis(permissionVO), Permission.class);
    }
}
