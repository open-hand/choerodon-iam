package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.RolePermission;


/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 13:55
 */
public interface RolePermissionC7nMapper extends BaseMapper<RolePermission> {
    /**
     * 批量删除权限
     * @param roleId
     * @param deletePermissionIds
     */
    void batchDelete(@Param("roleId") Long roleId,
                     @Param("deletePermissionIds") Set<Long> deletePermissionIds);

    List<RolePermission> listRolePermissionByRoleIdAndLabels(@Param("roleId") Long roleId,
                                                             @Param("labelNames") Set<String> labelNames);
}
