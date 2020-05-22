package io.choerodon.iam.app.service;

import io.choerodon.iam.infra.dto.LdapAutoDTO;

/**
 * @author wuguokai
 */
public interface LdapC7nService {

    LdapAutoDTO createLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    LdapAutoDTO updateLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    LdapAutoDTO queryLdapAutoDTO(Long organizationId);
}
