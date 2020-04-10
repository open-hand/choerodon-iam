package io.choerodon.iam.service.impl;

import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.UserC7nService;
import io.choerodon.base.infra.asserts.DetailsHelperAssert;
import io.choerodon.base.infra.asserts.OrganizationAssertHelper;
import io.choerodon.core.oauth.CustomUserDetails;

/**
 * @author scp
 * @date 2020/4/1
 * @description
 */
@Service
public class UserC7nServiceImpl implements UserC7nService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrganizationAssertHelper organizationAssertHelper;


    @Override
    public User querySelf() {
        CustomUserDetails customUserDetails = DetailsHelperAssert.userDetailNotExisted();
        Long userId = customUserDetails.getUserId();
        User userDTO = userMapper.selectByPrimaryKey(userId);
        if (userDTO != null) {
            Tenant organizationDTO = organizationAssertHelper.notExisted(userDTO.getOrganizationId());
            userDTO.setTenantName(organizationDTO.getTenantName());
            userDTO.setTenantNum(organizationDTO.getSourceCode());
            if (userDTO.getPhone() == null || userDTO.getPhone().isEmpty()) {
                userDTO.setInternationalTelCode("");
            }
        }
        return userDTO;
    }
}
