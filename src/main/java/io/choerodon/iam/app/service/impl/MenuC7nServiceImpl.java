package io.choerodon.iam.app.service.impl;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.iam.api.dto.MenuTreeQueryDTO;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Menu;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.MenuRepository;
import org.hzero.iam.domain.repository.RoleRepository;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.common.utils.HiamMenuUtils;
import org.hzero.iam.infra.common.utils.UserUtils;
import org.hzero.iam.infra.mapper.MenuMapper;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.MenuType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.app.service.OrganizationRoleC7nService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.enums.MenuLabelEnum;
import io.choerodon.iam.infra.mapper.*;

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
//    private RoleC7nService roleC7nService;
    private UserRepository userRepository;
    private MenuMapper menuMapper;
    private MemberRoleC7nMapper memberRoleC7nMapper;
    private RoleC7nMapper roleC7nMapper;
    private ProjectUserMapper projectUserMapper;
    private ProjectC7nService projectC7nService;
    private UserC7nMapper userC7nMapper;

    public MenuC7nServiceImpl(MenuC7nMapper menuC7nMapper,
                              @Lazy OrganizationRoleC7nService organizationRoleC7nService,
                              ProjectMapCategoryMapper projectMapCategoryMapper,
                              MenuRepository menuRepository,
                              RoleRepository roleRepository,
//                              RoleC7nService roleC7nService,
                              MenuMapper menuMapper,
                              UserRepository userRepository,
                              MemberRoleC7nMapper memberRoleC7nMapper,
                              RoleC7nMapper roleC7nMapper,
                              ProjectUserMapper projectUserMapper,
                              ProjectC7nService projectC7nService,
                              UserC7nMapper userC7nMapper) {
        this.menuC7nMapper = menuC7nMapper;
        this.organizationRoleC7nService = organizationRoleC7nService;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
//        this.roleC7nService = roleC7nService;
        this.menuMapper = menuMapper;
        this.userRepository = userRepository;
        this.memberRoleC7nMapper = memberRoleC7nMapper;
        this.roleC7nMapper = roleC7nMapper;
        this.projectUserMapper = projectUserMapper;
        this.projectC7nService = projectC7nService;
        this.userC7nMapper = userC7nMapper;
    }

    @Override
    public List<Menu> listPermissionSetTree(Long tenantId, String menuLevel) {
        // 查询组织下的组织管理员账户
//        Role tenantAdminRole = roleC7nService.getTenantAdminRole(tenantId);
//        MenuSearchDTO menuParams = new MenuSearchDTO();
//        menuParams.setTenantId(tenantId);
//        menuParams.setupOrganizationQueryLevel();
//        menuParams.setRoleId(tenantAdminRole.getId());
        Set<String> labels = new HashSet<>();
        if (ResourceLevel.ORGANIZATION.value().equals(menuLevel)) {
            labels.add(MenuLabelEnum.TENANT_MENU.value());
            labels.add(MenuLabelEnum.TENANT_GENERAL.value());
        }
        if (ResourceLevel.PROJECT.value().equals(menuLevel)) {
            labels.add(MenuLabelEnum.GENERAL_MENU.value());
            labels.add(MenuLabelEnum.AGILE_MENU.value());
            labels.add(MenuLabelEnum.PROGRAM_MENU.value());
            labels.add(MenuLabelEnum.OPERATIONS_MENU.value());
        }
//        menuParams.setLabels(labels);
        SecurityTokenHelper.close();
        Set<String> typeNames = new HashSet<>();
        typeNames.add(MenuType.ROOT.value());
        typeNames.add(MenuType.MENU.value());
        typeNames.add(MenuType.DIR.value());
        List<Menu> menus = menuC7nMapper.listMenuByLabelAndType(labels, typeNames);
        SecurityTokenHelper.clear();

        Set<Long> ids = menus.stream().map(Menu::getId).collect(Collectors.toSet());
        List<Menu> permissionSetList = menuC7nMapper.listPermissionSetByParentIds(ids);
        menus.addAll(permissionSetList);

        return menus;

    }

    @Override
    public List<Menu> listNavMenuTree(Set<String> labels, Long projectId) {
        if (labels == null && projectId == null) {
            throw new CommonException("error.menu.params");
        }
        String finalLang = LanguageHelper.language();
        // 查询项目层菜单，（可以考虑单独抽出一个新接口）
        if (projectId != null) {
            ProjectDTO projectDTO = projectC7nService.checkNotExistAndGet(projectId);
            // 查询用户在项目下的角色
            CustomUserDetails userDetails = DetailsHelper.getUserDetails();
            List<Long> roleIds = new ArrayList<>();

            if (Boolean.TRUE.equals(userDetails.getAdmin())
                    || Boolean.TRUE.equals(userC7nMapper.isOrgAdministrator(projectDTO.getOrganizationId(), userDetails.getUserId()))) {
                Role tenantAdminRole = roleC7nMapper.getTenantAdminRole(projectDTO.getOrganizationId());
                roleIds.add(tenantAdminRole.getId());
            } else {
                List<RoleDTO> roleDTOS = projectUserMapper.listRolesByProjectIdAndUserId(projectId, userDetails.getUserId());
                if (CollectionUtils.isEmpty(roleDTOS)) {
                    throw new CommonException("error.not.project.member");
                }
                roleIds = roleDTOS.stream().map(RoleDTO::getId).collect(Collectors.toList());
            }
            // 添加项目类型

            List<ProjectCategoryDTO> list = projectMapCategoryMapper.selectProjectCategoryNames(projectId);
            if (CollectionUtils.isEmpty(list)) {
                throw new CommonException("error.project.category");
            }

            // 查询角色的菜单
            Set<String> finalLabels1 = list.stream().map(ProjectCategoryDTO::getLabelCode).collect(Collectors.toSet());
            List<Long> finalRoleIds = roleIds;
            CompletableFuture<List<Menu>> f1 = CompletableFuture.supplyAsync(() -> {
                SecurityTokenHelper.close();
                List<Menu> menus = this.menuMapper.selectRoleMenus(finalRoleIds, projectDTO.getOrganizationId(), finalLang, finalLabels1, true);
                SecurityTokenHelper.clear();
                return menus;
            }, SELECT_MENU_POOL);
            CompletableFuture<List<Menu>> cf = f1
                    // 转换成树形结构
                    .thenApply((menus) -> HiamMenuUtils.formatMenuListToTree(menus, Boolean.FALSE))
                    .exceptionally((e) -> {
                        LOGGER.warn("select menus error, ex = {}", e.getMessage(), e);
                        return Collections.emptyList();
                    });
            return cf.join();
        }

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
            // 组织平台层菜单调用
            MenuTreeQueryDTO menuTreeQueryDTO = new MenuTreeQueryDTO();
            menuTreeQueryDTO.setLabels(labels);
            menuTreeQueryDTO.setLang(finalLang);
            menuTreeQueryDTO.setUnionLabel(true);
            return menuRepository.selectRoleMenuTree(menuTreeQueryDTO);
        }

    }

    @Override
    public List<Menu> listMenuByLabel(Set<String> labels) {
        return menuC7nMapper.listMenuByLabel(labels);
    }

    @Override
    public List<Menu> listUserInfoMenuOnlyTypeMenu() {

        return menuC7nMapper.listUserInfoMenuOnlyTypeMenu();
    }

    @Override
    public List<Menu> listMenuByLabelAndType(Set<String> labelNames, String type) {
        Set<String> typeNames = new HashSet<>();
        typeNames.add(type);
        return menuC7nMapper.listMenuByLabelAndType(labelNames, typeNames);
    }


    @Override
    public List<Menu> listMenuByLevel(String code) {
        code = getCode(code);
        Set<String> labelNames = new HashSet<>();
        if (ResourceLevel.SITE.value().equals(code)) {
            labelNames.add(MenuLabelEnum.SITE_MENU.value());
        }
        if (ResourceLevel.ORGANIZATION.value().equals(code)) {
            labelNames.add(MenuLabelEnum.TENANT_MENU.value());
        }
        if (ResourceLevel.PROJECT.value().equals(code)) {
            labelNames.add(MenuLabelEnum.GENERAL_MENU.value());
            labelNames.add(MenuLabelEnum.AGILE_MENU.value());
            labelNames.add(MenuLabelEnum.OPERATIONS_MENU.value());
            labelNames.add(MenuLabelEnum.PROGRAM_MENU.value());

        }
        if (ResourceLevel.USER.value().equals(code)) {
            labelNames.add(MenuLabelEnum.USER_MENU.value());
        }
        Set<String> typeNames = new HashSet<>();
        typeNames.add(MenuType.MENU.value());
        return menuC7nMapper.listMenuByLabelAndType(labelNames, typeNames);
    }

    @Override
    public Boolean hasSiteMenuPermission() {
        CustomUserDetails userDetails = UserUtils.getUserDetails();
        User user = userRepository.selectByPrimaryKey(userDetails.getUserId());

        // root用户能够访问
        if (Boolean.TRUE.equals(user.getAdmin())) {
            return true;
        }

        // 拥有平台层菜单也能访问
        List<Role> roleList = memberRoleC7nMapper.listRoleByUserIdAndLevel(user.getId(), ResourceLevel.SITE.value());
        if (CollectionUtils.isEmpty(roleList)) {
            return false;
        }

        Set<Long> roleIds = roleList.stream().map(Role::getId).collect(Collectors.toSet());
        long menuCount = menuC7nMapper.countPermissionSetByRoleIdsAndLevel(roleIds, ResourceLevel.SITE.value());
        return menuCount > 0;
    }

    private String getCode(String code) {
        int index = code.lastIndexOf('.');
        return code.substring(index + 1);
    }
}
