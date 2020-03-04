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

    /**
     * 查询与角色层级不匹配的接口权限
     */
    List<RolePermissionDTO> selectInvalidData();

}
