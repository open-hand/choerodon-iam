package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.RoleVO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/4/22 10:10
 */
public interface OrganizationRoleService {
    /**
     * 创建自定义角色
     * @param organizationId
     * @param roleVO
     */
    void create(Long organizationId, RoleVO roleVO);
}
