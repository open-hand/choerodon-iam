package io.choerodon.base.infra.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

import io.choerodon.base.infra.dto.AppOrganizationRefDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/21
 */
public interface AppOrganizationRefMapper extends Mapper<AppOrganizationRefDTO> {

    Set<Long> selectServiceByOrgId(@Param("organizationId") Long organizationId,
                                   @Param("appType") String appType);

    Set<Long> selectSvcVersionByOrgId(@Param("organizationId") Long organizationId,
                                      @Param("appType") String appType);
}
