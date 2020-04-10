package io.choerodon.iam.service.impl;

import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.app.service.LdapService;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.repository.LdapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.LdapC7nService;
import io.choerodon.base.infra.asserts.LdapAssertHelper;

/**
 * @author scp
 * @date 2020/4/2
 * @description
 */
@Service
public class LdapC7nServiceImpl implements LdapC7nService {
    @Autowired
    private LdapService ldapService;
    @Autowired
    private LdapRepository ldapRepository;
    @Autowired
    private LdapAssertHelper ldapAssertHelper;


    @Override
    public void enableLdap(Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        ldap.setEnabled(true);
        ldapService.enableLdap(ldap);
    }


    @Override
    public void disableLdap(Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        ldap.setEnabled(false);
        ldapService.disableLdap(ldap);
    }

    @Override
    public LdapConnectionDTO testConnect(Long organizationId, LdapAccountDTO ldapAccount) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        return ldapService.testConnect(organizationId, ldap.getId(), ldapAccount);
    }

    @Override
    public void syncLdapUser(Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        ldapService.syncLdapUser(organizationId, ldap.getId());
    }
}
