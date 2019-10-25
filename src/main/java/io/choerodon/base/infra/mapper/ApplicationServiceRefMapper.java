package io.choerodon.base.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

import io.choerodon.base.infra.dto.ApplicationServiceRefDTO;
import io.choerodon.mybatis.common.Mapper;


/**
 * @author Eugen
 */
public interface ApplicationServiceRefMapper extends Mapper<ApplicationServiceRefDTO> {

    Set<Long> selectServiceByOrgId(@Param("organizationId") Long organizationId, @Param("appType") String appType);

}
