package io.choerodon.iam.app.service;

import io.choerodon.iam.infra.dto.LdapAutoDTO;
import io.choerodon.iam.infra.dto.payload.LdapAutoTaskEventPayload;
import org.hzero.iam.domain.entity.Ldap;

/**
 * @author wuguokai
 */
public interface LdapC7nService {

    LdapAutoDTO createLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    LdapAutoDTO updateLdapAuto(Long organizationId, LdapAutoDTO ldapAutoDTO);

    Ldap validateLdap(Long organizationId, Long id);

    LdapAutoDTO queryLdapAutoDTO(Long organizationId);

    void handleLdapAutoTask(LdapAutoTaskEventPayload ldapAutoTaskEventPayload);

    Ldap queryByOrganizationId(Long organizationId);
}
