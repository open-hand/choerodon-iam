package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.ApplicationServiceRefDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;


/**
 * @author Eugen
 */
public interface ApplicationServiceRefMapper extends Mapper<ApplicationServiceRefDTO> {

    Set<Long> selectServiceByOrgId(@Param("organizationId") Long organizationId, @Param("appType") String appType);

}
