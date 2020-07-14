package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Permission;

/**
 * @author scp
 * @since 2020/4/16
 *
 */
public interface PermissionC7nMapper  {
    List<Permission> selectByRoleId(@Param("roleId") Long roleId,
                                    @Param("params") String params);
}
