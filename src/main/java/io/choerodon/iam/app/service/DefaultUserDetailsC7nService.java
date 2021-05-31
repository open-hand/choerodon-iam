package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.service.user.impl.DefaultUserDetailsService;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/5/31
 * @Modified By:
 */
@Service
public class DefaultUserDetailsC7nService extends DefaultUserDetailsService {
    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public void storeUserTenant(Long tenantId) {
        if (tenantId != null) {
            Tenant tenant = tenantMapper.selectByPrimaryKey(tenantId);
            if (tenant.getEnabledFlag() != null && tenant.getEnabledFlag() != 1) {
                throw new CommonException("error.tenant.enable");
            }
            super.storeUserTenant(tenantId);
        }
    }
}
