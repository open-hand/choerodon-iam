package io.choerodon.iam.app.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.iam.infra.mapper.PermissionC7nMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.Sort;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class PermissionC7nServiceImpl implements PermissionC7nService {

    @Autowired
    private PermissionC7nMapper permissionC7nMapper;

    @Override
    public List<Permission> query(String level, String serviceName, String code) {
        Permission permission = new Permission();
        permission.setCode(code);
        permission.setLevel(level);
        permission.setServiceName(serviceName);
        return PageHelper.doSort(new Sort("code,asc"), () -> permissionC7nMapper.select(permission));
    }

    @Override
    public Set<Permission> queryByRoleIds(List<Long> roleIds) {
        Set<Permission> permissions = new HashSet<>();
        roleIds.forEach(roleId -> {
            List<Permission> permissionList = permissionC7nMapper.selectByRoleId(roleId, null);
            permissions.addAll(permissionList);
        });
        return permissions;
    }
}
