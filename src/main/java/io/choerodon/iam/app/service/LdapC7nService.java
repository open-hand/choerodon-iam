package io.choerodon.iam.app.service;

import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;

/**
 * @author scp
 * @date 2020/4/2
 * @description
 */
public interface LdapC7nService {
    void enableLdap(Long organizationId);

    void disableLdap(Long organizationId);

    LdapConnectionDTO testConnect(Long organizationId, LdapAccountDTO ldapAccount);

    void syncLdapUser(Long organizationId);

}
