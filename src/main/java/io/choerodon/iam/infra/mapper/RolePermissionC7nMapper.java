package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.RolePermission;


/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 13:55
 */
public interface RolePermissionC7nMapper {
    /**
     * 批量删除权限
     *
     * @param roleId
     * @param deletePermissionIds
     */
    void batchDelete(@Param("roleId") Long roleId,
                     @Param("deletePermissionIds") Set<Long> deletePermissionIds);

    List<Menu> listRolePermissionByRoleIdAndLabels(@Param("roleId") Long roleId,
                                                   @Param("labelNames") Set<String> labelNames);

    List<RolePermission> listRolePermissionIds(@Param("roleId") Long roleId);

    void batchInsert(@Param("rolePermissionList") List<RolePermission> rolePermissionList);

    void batchDeleteById(@Param("delPsIds") Set<Long> delPsIds);

    void deleteByRoleId(@Param("roleId") Long roleId);
}
