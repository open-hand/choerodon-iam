package io.choerodon.iam.app.service;

import javax.annotation.Nullable;

import io.choerodon.iam.infra.enums.TenantConfigEnum;

/**
 * @author zmf
 * @since 2020/5/22
 */
public interface C7nTenantConfigService {
    /**
     * 获取特定的tenant的配置值（结果可为空）
     *
     * @param tenantId         租户id
     * @param tenantConfigEnum 配置的key
     * @return 可能为空的配置值
     */
    @Nullable
    String queryCertainConfigValue(Long tenantId, TenantConfigEnum tenantConfigEnum);

    /**
     * 获取特定的tenant的配置值
     *
     * @param tenantId         租户id
     * @param tenantConfigEnum 配置的key
     * @return 不能为空的配置值，为空时抛出异常
     */
    String queryNonNullCertainConfigValue(Long tenantId, TenantConfigEnum tenantConfigEnum);
}
