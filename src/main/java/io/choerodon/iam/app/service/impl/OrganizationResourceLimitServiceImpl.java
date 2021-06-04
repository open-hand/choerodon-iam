package io.choerodon.iam.app.service.impl;

import org.springframework.stereotype.Service;

import io.choerodon.iam.api.vo.ResourceLimitVO;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.infra.dto.ProjectDTO;

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
    public Boolean checkOrganizationIsSaas(Long tenantId) {
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

    @Override
    public void checkEnableImportUserOrThrowE(Long organizationId, Long userId, int userNum) {
        // do nothing
    }

    @Override
    public ResourceLimitVO queryResourceLimit(Long organizationId) {
        return null;
    }

    @Override
    public void checkEnableCreateProjectType(Long organizationId, ProjectDTO projectDTO) {
        // do nothing
    }

    @Override
    public void checkEnableAddMember(Long tenantId) {
        // do nothing
    }
}
