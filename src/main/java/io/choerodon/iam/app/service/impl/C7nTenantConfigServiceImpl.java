package io.choerodon.iam.app.service.impl;

import java.util.List;
import javax.annotation.Nullable;

import org.hzero.iam.domain.entity.TenantConfig;
import org.hzero.iam.domain.repository.TenantConfigRepository;
import org.hzero.mybatis.domian.Condition;
import org.hzero.mybatis.util.Sqls;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.C7nTenantConfigService;
import io.choerodon.iam.infra.enums.TenantConfigEnum;

/**
 * @author zmf
 * @since 2020/5/22
 */
@Service
public class C7nTenantConfigServiceImpl implements C7nTenantConfigService {
    @Autowired
    private TenantConfigRepository tenantConfigRepository;

    @Nullable
    @Override
    public String queryCertainConfigValue(Long tenantId, TenantConfigEnum tenantConfigEnum) {
        List<TenantConfig> tenantConfigs = doQueryByKey(tenantId, tenantConfigEnum);
        if (CollectionUtils.isEmpty(tenantConfigs)) {
            return null;
        }
        return checkSizeAndReturnFirstValue(tenantConfigs, tenantId, tenantConfigEnum);
    }

    @Override
    public String queryNonNullCertainConfigValue(Long tenantId, TenantConfigEnum tenantConfigEnum) {
        List<TenantConfig> tenantConfigs = doQueryByKey(tenantId, tenantConfigEnum);
        if (CollectionUtils.isEmpty(tenantConfigs)) {
            throw new CommonException("error.query.tenant.config", tenantId, tenantConfigEnum.value());
        }
        return checkSizeAndReturnFirstValue(tenantConfigs, tenantId, tenantConfigEnum);
    }

    private String checkSizeAndReturnFirstValue(List<TenantConfig> results, Long tenantId, TenantConfigEnum tenantConfigEnum) {
        if (results.size() > 1) {
            throw new CommonException("error.multiple.tenant.config", tenantId, tenantConfigEnum.value(), results.size());
        }

        return results.get(0).getConfigValue();
    }

    private List<TenantConfig> doQueryByKey(Long tenantId, TenantConfigEnum tenantConfigEnum) {
        return tenantConfigRepository.selectByCondition(
                Condition.builder(TenantConfig.class)
                        .where(Sqls.custom()
                                .andEqualTo(TenantConfig.FIELD_TENANT_ID, tenantId)
                                .andEqualTo(TenantConfig.FIELD_CONFIG_KEY, tenantConfigEnum.value())
                        )
                        .build());
    }
}
