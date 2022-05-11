package io.choerodon.iam.app.service.impl;

import org.hzero.iam.app.service.impl.PasswordPolicyServiceImpl;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.iam.domain.repository.TenantRepository;
import org.hzero.iam.domain.service.user.UserCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author scp
 * @since 2022/5/11
 */
@Component
@Primary
public class PasswordPolicyServiceC7nImpl extends PasswordPolicyServiceImpl {
    @Autowired
    private UserCheckService userCheckService;

    public PasswordPolicyServiceC7nImpl(TenantRepository tenantRepository, PasswordPolicyRepository passwordPolicyRepository) {
        super(tenantRepository, passwordPolicyRepository);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordPolicy createPasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy) {
        PasswordPolicy result = super.createPasswordPolicy(tenantId, passwordPolicy);
        userCheckService.checkPasswordPolicy(passwordPolicy.getOriginalPassword(), tenantId);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PasswordPolicy updatePasswordPolicy(Long tenantId, PasswordPolicy passwordPolicy) {
        PasswordPolicy result = super.updatePasswordPolicy(tenantId, passwordPolicy);
        userCheckService.checkPasswordPolicy(passwordPolicy.getOriginalPassword(), tenantId);
        return passwordPolicy;
    }
}
