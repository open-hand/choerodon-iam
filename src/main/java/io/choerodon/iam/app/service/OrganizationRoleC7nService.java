package io.choerodon.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.Role;

import io.choerodon.iam.api.vo.RoleVO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/22 10:10
 */
public interface OrganizationRoleC7nService {
    /**
     * 创建自定义角色
     *
     * @param organizationId
     * @param roleVO
     */
    void create(Long organizationId, RoleVO roleVO);

    /**
     * 修改角色信息
     *
     * @param organizationId
     * @param roleId
     * @param roleVO
     */
    void update(Long organizationId, Long roleId, RoleVO roleVO);


    /**
     * 根据标签查询组织下角色
     *
     * @param tenantId  组织id
     * @param labelName 标签名
     * @return Role列表
     */
    List<Role> getByTenantIdAndLabel(Long tenantId, String labelName);

    RoleVO queryById(Long organizationId, Long roleId);

    /**
     * 删除自定义角色
     *
     * @param organizationId
     * @param roleId
     */
    void delete(Long organizationId, Long roleId);

    Boolean checkCodeExist(Long organizationId, String code);
}
