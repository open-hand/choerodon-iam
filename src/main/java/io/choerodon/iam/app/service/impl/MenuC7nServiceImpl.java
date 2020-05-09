package io.choerodon.iam.app.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.constant.LabelConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import io.choerodon.core.oauth.CustomUserDetails;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(MenuC7nServiceImpl.class);
    private static final String CHOERODON_MENU = "CHOERODON_MENU";

    // 查询菜单的线程池
    private final ThreadPoolExecutor SELECT_MENU_POOL = new ThreadPoolExecutor(20, 180, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000), new ThreadFactoryBuilder().setNameFormat("C7n-selMenuPool-%d").build());

    private MenuC7nMapper menuC7nMapper;
    private OrganizationRoleC7nService organizationRoleC7nService;
    private RoleRepository roleRepository;

    public MenuC7nServiceImpl(MenuC7nMapper menuC7nMapper,
                              OrganizationRoleC7nService organizationRoleC7nService,
                              RoleRepository roleRepository) {
        this.menuC7nMapper = menuC7nMapper;
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
    public List<Menu> listNavMenuTree(Set<String> labels, Long projectId) {
        CustomUserDetails self = UserUtils.getUserDetails();
        List<Long> roleIds = self.roleMergeIds();
        Long tenantId = self.getTenantId();
        labels.add(CHOERODON_MENU);
        String finalLang = LanguageHelper.language();

        // 查询角色关联的菜单
        CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> menuC7nMapper.selectRoleMenus(roleIds, tenantId, projectId, finalLang, labels), SELECT_MENU_POOL);

        // 查询安全组关联的菜单

        CompletableFuture<List<Menu>> cf = f1

                // 转换成树形结构
                .thenApply((menus) -> HiamMenuUtils.formatMenuListToTree(menus, Boolean.FALSE))
                .exceptionally((e) -> {
                    LOGGER.warn("select menus error, ex = {}", e.getMessage(), e);
                    return Collections.emptyList();
                });

        return cf.join();
    }
}
