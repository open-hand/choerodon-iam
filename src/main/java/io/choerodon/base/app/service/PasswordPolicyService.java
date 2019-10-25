package io.choerodon.base.app.service;


import io.choerodon.base.infra.dto.PasswordPolicyDTO;

/**
 * @author wuguokai
 */
public interface PasswordPolicyService {
    PasswordPolicyDTO create(Long orgId, PasswordPolicyDTO passwordPolicyDTO);

    PasswordPolicyDTO queryByOrgId(Long orgId);

    PasswordPolicyDTO query(Long id);

    PasswordPolicyDTO update(Long orgId, Long id, PasswordPolicyDTO passwordPolicyDTO);
}
