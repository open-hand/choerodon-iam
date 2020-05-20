package io.choerodon.iam.app.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.iam.api.dto.MenuSearchDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.MenuMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.app.service.OrganizationRoleC7nService;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.enums.MenuLabelEnum;
import io.choerodon.iam.infra.mapper.MenuC7nMapper;
import io.choerodon.iam.infra.mapper.ProjectMapCategoryMapper;

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
    private static final String USER_MENU = "USER_MENU";

    // 查询菜单的线程池
    private final ThreadPoolExecutor SELECT_MENU_POOL = new ThreadPoolExecutor(20, 180, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(2000), new ThreadFactoryBuilder().setNameFormat("C7n-selMenuPool-%d").build());

    private MenuC7nMapper menuC7nMapper;
    private OrganizationRoleC7nService organizationRoleC7nService;
    private RoleRepository roleRepository;
    private MenuRepository menuRepository;
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    private RoleC7nService roleC7nService;
    private MenuMapper menuMapper;

    public MenuC7nServiceImpl(MenuC7nMapper menuC7nMapper,
                              OrganizationRoleC7nService organizationRoleC7nService,
                              ProjectMapCategoryMapper projectMapCategoryMapper,
                              MenuRepository menuRepository,
                              RoleRepository roleRepository,
                              RoleC7nService roleC7nService,
                              MenuMapper menuMapper) {
        this.menuC7nMapper = menuC7nMapper;
        this.organizationRoleC7nService = organizationRoleC7nService;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.roleC7nService = roleC7nService;
        this.menuMapper = menuMapper;
    }

    @Override
    public List<Menu> listPermissionSetTree(Long tenantId, String menuLevel) {
        // 查询组织下的组织管理员账户
        Role tenantAdminRole = roleC7nService.getTenantAdminRole(tenantId);
        MenuSearchDTO menuParams = new MenuSearchDTO();
        menuParams.setTenantId(tenantId);
        menuParams.setupOrganizationQueryLevel();
        menuParams.setRoleId(tenantAdminRole.getId());
        Set<String> labels = new HashSet<>();
        if (ResourceLevel.ORGANIZATION.value().equals(menuLevel)) {
            labels.add(MenuLabelEnum.TENANT_MENU.value());
        }
        if (ResourceLevel.PROJECT.value().equals(menuLevel)) {
            labels.add(MenuLabelEnum.GENERAL_MENU.value());
        }
        menuParams.setLabels(labels);
        // 根据层级查询组织管理员的有权限的菜单列表
        CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> menuMapper.selectMenusByCondition(menuParams), SELECT_MENU_POOL);

        CompletableFuture<List<Menu>> cf = f1
                // 转换成树形结构
                .thenApply((menus) -> HiamMenuUtils.formatMenuListToTree(menus, Boolean.FALSE))
                .exceptionally((e) -> {
                    LOGGER.warn("select menus error, ex = {}", e.getMessage(), e);
                    return Collections.emptyList();
                });
        return cf.join();

    }

    @Override
    public List<Menu> listNavMenuTree(Set<String> labels, Long projectId) {
        if (labels == null && projectId == null) {
            throw new CommonException("error.menu.params");
        }
        if (projectId != null) {
            labels = new HashSet<>();
            List<ProjectCategoryDTO> list = projectMapCategoryMapper.selectProjectCategoryNames(projectId);
            if (CollectionUtils.isEmpty(list)) {
                throw new CommonException("error.project.category");
            }
            for (ProjectCategoryDTO t : list) {
                labels.add(t.getLabelCode());
            }
        }
        String finalLang = LanguageHelper.language();

        if (labels.contains(USER_MENU)) {
            CompletableFuture<List<Menu>> f1;
            Set<String> finalLabels = new HashSet<>(labels);
            f1 = CompletableFuture.supplyAsync(() -> menuC7nMapper.selectUserMenus(finalLang, finalLabels), SELECT_MENU_POOL);
            CompletableFuture<List<Menu>> cf = f1
                    // 转换成树形结构
                    .thenApply((menus) -> HiamMenuUtils.formatMenuListToTree(menus, Boolean.FALSE))
                    .exceptionally((e) -> {
                        LOGGER.warn("select menus error, ex = {}", e.getMessage(), e);
                        return Collections.emptyList();
                    });
            return cf.join();
        } else {
            // todo 等权限刷新进去 使用hzero方法 将mapper 方法也删除
//            return menuRepository.selectRoleMenuTree(null, null, labels);
            CustomUserDetails self = UserUtils.getUserDetails();
            List<Long> roleIds = self.roleMergeIds();
            Long tenantId = self.getTenantId();

            // 查询角色关联的菜单
            Set<String> finalLabels1 = labels;
            CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> menuC7nMapper.selectRoleMenus(roleIds, tenantId, finalLang, finalLabels1), SELECT_MENU_POOL);

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

    @Override
    public List<Menu> listMenuByLabel(Set<String> labels) {
        return menuC7nMapper.listMenuByLabel(labels);
    }
}
