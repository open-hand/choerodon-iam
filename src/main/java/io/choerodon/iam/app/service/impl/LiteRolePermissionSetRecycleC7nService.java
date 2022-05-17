package io.choerodon.iam.app.service.impl;

import java.util.Set;

import org.hzero.iam.domain.service.role.lite.LiteRolePermissionSetRecycleService;

/**
 * @author scp
 * @since 2022/5/16
 */
public class LiteRolePermissionSetRecycleC7nService extends LiteRolePermissionSetRecycleService {

    @Override
    public void recycleRolePermissionSets(Long roleId, Set<Long> permissionSetIds, String type) {
        super.recycleRolePermissionSets(roleId, permissionSetIds, type);
    }
}
