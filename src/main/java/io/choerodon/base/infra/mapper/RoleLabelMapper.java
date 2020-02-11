package io.choerodon.base.infra.mapper;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.infra.dto.RoleLabelDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author superlee
 */
public interface RoleLabelMapper extends Mapper<RoleLabelDTO> {
    void deleteByLabelId(@Param("labelId") Long labelId,
                         @Param("roleId") Long roleId);
}
