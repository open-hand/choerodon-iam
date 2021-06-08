package io.choerodon.iam.app.service;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.service.RootUserService;
import org.hzero.iam.domain.service.user.impl.DefaultUserDetailsService;
import org.hzero.iam.infra.mapper.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

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
    @Lazy
    @Autowired
    private UserC7nService userC7nService;

    @Override
    public void storeUserTenant(Long tenantId) {
        if (tenantId != null) {
            CustomUserDetails self = DetailsHelper.getUserDetails();
            if (!RootUserService.isRootUser() && !userC7nService.checkIsOrgRoot(tenantId, self.getUserId())) {
                Tenant tenant = tenantMapper.selectByPrimaryKey(tenantId);
                if (tenant.getEnabledFlag() != null && tenant.getEnabledFlag() != 1) {
                    throw new CommonException("error.tenant.enable");
                }
            }
            super.storeUserTenant(tenantId);
        }
    }
}
