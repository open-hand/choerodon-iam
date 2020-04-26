package io.choerodon.iam.infra.mapper;

import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.RolePermission;

import io.choerodon.mybatis.common.BaseMapper;

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
     * @param roleId
     * @param deletePermissionIds
     */
    void batchDelete(@Param("roleId") Long roleId,
                     @Param("deletePermissionIds") Set<Long> deletePermissionIds);

}
