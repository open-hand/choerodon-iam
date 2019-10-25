package io.choerodon.base.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.base.app.service.PasswordPolicyService;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.base.infra.dto.PasswordPolicyDTO;
import io.choerodon.core.exception.ext.InsertException;
import io.choerodon.core.exception.ext.UpdateException;
import io.choerodon.base.infra.mapper.PasswordPolicyMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author wuguokai
 */
@Component
public class PasswordPolicyServiceImpl implements PasswordPolicyService {

    private OrganizationAssertHelper organizationAssertHelper;
    private PasswordPolicyMapper passwordPolicyMapper;

    public PasswordPolicyServiceImpl(OrganizationAssertHelper organizationAssertHelper,
                                     PasswordPolicyMapper passwordPolicyMapper) {
        this.organizationAssertHelper = organizationAssertHelper;
        this.passwordPolicyMapper = passwordPolicyMapper;
    }

    @Override
    public PasswordPolicyDTO create(Long orgId, PasswordPolicyDTO passwordPolicyDTO) {
        organizationAssertHelper.notExisted(orgId);
        passwordPolicyDTO.setOrganizationId(orgId);
        if (passwordPolicyMapper.insertSelective(passwordPolicyDTO) != 1) {
            throw new InsertException("error.passwordPolicy.create");
        }
        return passwordPolicyMapper.selectByPrimaryKey(passwordPolicyDTO.getId());
    }

    @Override
    public PasswordPolicyDTO queryByOrgId(Long orgId) {
        PasswordPolicyDTO dto = new PasswordPolicyDTO();
        dto.setOrganizationId(orgId);
        return passwordPolicyMapper.selectOne(dto);
    }

    @Override
    public PasswordPolicyDTO query(Long id) {
        return passwordPolicyMapper.selectByPrimaryKey(id);
    }

    @Override
    public PasswordPolicyDTO update(Long orgId, Long id, PasswordPolicyDTO passwordPolicyDTO) {
        organizationAssertHelper.notExisted(orgId);
        PasswordPolicyDTO old = passwordPolicyMapper.selectByPrimaryKey(id);
        if (!orgId.equals(old.getOrganizationId())) {
            throw new CommonException("error.passwordPolicy.organizationId.not.same");
        }
        passwordPolicyDTO.setId(id);
        if (StringUtils.isEmpty(passwordPolicyDTO.getRegularExpression())){
            passwordPolicyDTO.setRegularExpression(null);
        }
        passwordPolicyDTO.setOrganizationId(old.getOrganizationId());
        passwordPolicyDTO.setCode(old.getCode());
        if (passwordPolicyMapper.updateByPrimaryKey(passwordPolicyDTO) != 1) {
            throw new UpdateException("error.passwordPolicy.update");
        }
        return passwordPolicyMapper.selectByPrimaryKey(passwordPolicyDTO.getId());
    }
}
