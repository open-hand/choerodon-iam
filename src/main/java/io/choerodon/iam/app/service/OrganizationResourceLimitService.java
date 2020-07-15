package io.choerodon.iam.app.service;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/19 22:08
 */
public interface OrganizationResourceLimitService {
    /**
     * 检查是否还能创建组织用户
     *
     * @param organizationId
     * @return true 或者 false
     */
    Boolean checkEnableCreateOrganizationUser(Long organizationId);

    /**
     * 检查是否还能创建项目用户
     *
     * @param projectId
     * @return true 或者 false
     */
    Boolean checkEnableCreateProjectUser(Long projectId);

    /**
     * 校验组织是否是注册组织
     *
     * @param tenantId
     * @return 默认值false
     */
    Boolean checkOrganizationIsRegister(Long tenantId);

    /**
     * 校验组织是否还能创建项目
     *
     * @param organizationId
     */
    void checkEnableCreateProjectOrThrowE(Long organizationId);

    /**
     * 检查组织下是否还能创建项目
     *
     * @param organizationId
     * @return true 或者 false
     */
    Boolean checkEnableCreateProject(Long organizationId);

    /**
     * 检查组织层是否还能创建用户
     *
     * @param organizationId
     * @param userNum
     */
    void checkEnableCreateUserOrThrowE(Long organizationId, int userNum);
}
