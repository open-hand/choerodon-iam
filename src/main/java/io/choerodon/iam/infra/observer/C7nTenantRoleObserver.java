package io.choerodon.iam.infra.observer;

import java.util.List;

import javax.annotation.Nonnull;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.hzero.iam.saas.domain.service.TenantObserver;
import org.springframework.beans.factory.annotation.Autowired;

import io.choerodon.iam.infra.mapper.RoleC7nMapper;

/**
 * 〈功能简述〉
 * 〈更新角色为预定义角色〉
 *
 * @author wanghao
 * @since 2020/12/28 17:37
 */
public class C7nTenantRoleObserver implements TenantObserver<List<Role>> {

    @Autowired
    private RoleC7nMapper roleC7nMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public int order() {
        return 60;
    }

    @Override
    public List<Role> tenantCreate(@Nonnull Tenant tenant) {
        // 更新角色为预定义
        List<Role> roleList = roleC7nMapper.listByTenantId(tenant.getTenantId());
        roleList.forEach(role -> {
            role.setBuiltIn(true);
            roleMapper.updateByPrimaryKeySelective(role);
        });
        return roleList;
    }

}
