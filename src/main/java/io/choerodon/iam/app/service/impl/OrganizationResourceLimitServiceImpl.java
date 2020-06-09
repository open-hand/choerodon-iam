package io.choerodon.iam.app.service.impl;

import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.OrganizationResourceLimitService;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/19 22:08
 */
@Service
public class OrganizationResourceLimitServiceImpl implements OrganizationResourceLimitService {


    @Override
    public Boolean checkEnableCreateOrganizationUser(Long organizationId) {
        return true;
    }

    @Override
    public Boolean checkEnableCreateProjectUser(Long projectId) {
        return true;
    }

    @Override
    public Boolean checkOrganizationIsRegister(Long tenantId) {
        return false;
    }

    @Override
    public void checkEnableCreateProjectOrThrowE(Long organizationId) {
        // do nothing
    }

    @Override
    public Boolean checkEnableCreateProject(Long organizationId) {
        return true;
    }

    @Override
    public void checkEnableCreateUserOrThrowE(Long organizationId, int userNum) {
        // do nothing
    }
}
