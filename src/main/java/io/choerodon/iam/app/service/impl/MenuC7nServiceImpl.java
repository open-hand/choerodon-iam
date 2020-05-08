package io.choerodon.iam.app.service.impl;

import java.util.List;
import java.util.Set;

import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.constant.LabelConstants;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.app.service.OrganizationRoleC7nService;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 17:36
 */
@Service
public class MenuC7nServiceImpl implements MenuC7nService {

    private static String DEFAULT_LANG="zh_CN";

    private MenuC7nMapper menuC7nMapper;
    private OrganizationRoleC7nService organizationRoleC7nService;
    private RoleRepository roleRepository;
    private MenuRepository menuRepository;

    public MenuC7nServiceImpl(MenuC7nMapper menuC7nMapper,
                              OrganizationRoleC7nService organizationRoleC7nService,
                              MenuRepository menuRepository,
                              RoleRepository roleRepository) {
        this.menuC7nMapper = menuC7nMapper;
        this.menuRepository = menuRepository;
        this.organizationRoleC7nService = organizationRoleC7nService;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Menu> listPermissionSetTree(Long organizationId, String menuLevel) {
        // 查询组织下的组织管理员账户
        Role orgAdmin = organizationRoleC7nService.getByTenantIdAndLabel(organizationId, LabelConstants.TENANT_ADMIN);

        // 根据层级查询组织管理员的有权限的菜单列表
        return roleRepository.selectRolePermissionSetTree(orgAdmin.getId(), null);
    }

    @Override
    public List<Menu> listNavMenuTree(Long roleId, Set<String> labels, Long projectId) {
        List<Menu> menuList = menuRepository.selectRoleMenuTree(roleId, DEFAULT_LANG, labels);
        return null;
    }
}
