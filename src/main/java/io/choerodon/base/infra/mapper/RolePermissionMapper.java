package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.RolePermissionDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author wuguokai
 */
public interface RolePermissionMapper extends Mapper<RolePermissionDTO> {

    List<Long> queryExistingPermissionIdsByRoleIds(@Param("list") List<Long> roleIds);
}
