package io.choerodon.iam.infra.mapper;


import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.TenantConfig;

/**
 * @author scp
 * @date 2020/5/23
 * @description
 */
public interface TenantConfigC7nMapper {
    TenantConfig queryTenantConfigByTenantIdAndKey(@Param("tenantId") Long tenantId,
                                                   @Param("key") String key);
}
