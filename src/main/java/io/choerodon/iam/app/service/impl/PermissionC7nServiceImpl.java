package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.iam.infra.mapper.PermissionC7nMapper;
import io.choerodon.swagger.swagger.PermissionRegistry;
import org.hzero.iam.domain.entity.Permission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class PermissionC7nServiceImpl implements PermissionC7nService {

    @Autowired
    private PermissionC7nMapper permissionC7nMapper;
    @Autowired
    private PermissionRegistry permissionRegistry;


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
