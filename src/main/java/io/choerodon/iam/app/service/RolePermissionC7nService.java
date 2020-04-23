package io.choerodon.iam.app.service;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.RolePermission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 11:51
 */
public interface RolePermissionC7nService {

    /**
     * 查询角色拥有的权限
     * @param roleId
     * @return
     */
    List<RolePermission> listRolePermissionByRoleId(Long roleId);

    /**
     * 批量删除权限
     * @param roleId
     * @param deletePermissionIds
     */
    void batchDelete(Long roleId, Set<Long> deletePermissionIds);
}
