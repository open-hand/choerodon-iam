package io.choerodon.base.app.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.choerodon.base.api.validator.MenuValidator;
import io.choerodon.base.app.service.MenuService;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.asserts.DetailsHelperAssert;
import io.choerodon.base.infra.dto.MenuDTO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.enums.MenuType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;

/**
 * @author wuguokai
 * @author superlee
 */
@Service
public class MenuServiceImpl implements MenuService {

    private static final Logger logger = LoggerFactory.getLogger(MenuServiceImpl.class);

    private OrganizationMapper organizationMapper;
    private MenuMapper menuMapper;
    private ProjectMapCategoryMapper projectMapCategoryMapper;
    private ProjectMapper projectMapper;
    private UserMapper userMapper;


    private Boolean enableOrganizationCategory;

    public MenuServiceImpl(OrganizationMapper organizationMapper,
                           MenuMapper menuMapper,
                           ProjectMapCategoryMapper projectMapCategoryMapper,
                           UserMapper userMapper,
                           ProjectMapper projectMapper,
                           @Value("${choerodon.category.organization.enabled:false}") Boolean enableOrganizationCategory) {
        this.organizationMapper = organizationMapper;
        this.menuMapper = menuMapper;
        this.userMapper = userMapper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
        this.projectMapper = projectMapper;
        this.enableOrganizationCategory = enableOrganizationCategory;
    }

    @Override
    public MenuDTO query(Long id) {
        return menuMapper.selectByPrimaryKey(id);
    }

    @Override
    public MenuDTO menus(String code, Long sourceId) {
        MenuDTO topMenu = getTopMenuByCode(code);
        String level = topMenu.getResourceLevel();
        CustomUserDetails userDetails = DetailsHelperAssert.userDetailNotExisted();
        Long userId = userDetails.getUserId();
        boolean isAdmin = userDetails.getAdmin() == null ? false : userDetails.getAdmin();
        String parentCategory = null;
        Long organizationId = null;
        if (ResourceType.isProject(level)) {
            OrganizationDTO organization = getOrganizationCategoryByProjectId(sourceId);
            if(!ObjectUtils.isEmpty(organization)){
                organizationId = organization.getId();
                parentCategory = organization.getCategory();
            }
        }
        if (ResourceType.isOrganization(level)) {
            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(sourceId);
            if (organizationDTO != null) {
                organizationId = organizationDTO.getId();
            }
        }
        if(!isAdmin){
            boolean isOrgAdmin = userMapper.isOrgAdministrator(organizationId,userId);
            isAdmin = isOrgAdmin;
        }
        Set<MenuDTO> menus = new HashSet<>(menuMapper.selectMenusByPermissionAndCategory(isAdmin, userId, sourceId, level, getCategories(level, sourceId), parentCategory));
        toTreeMenu(topMenu, menus, true);
        return topMenu;
    }

    /**
     * 获取 category
     * organization 与 category 一对一，存于 FD_ORGANIZATION 表中
     * project 与 category 一对多，存于 FD_PROJECT_MAP_CATEGORY 表中
     */
    private List<String> getCategories(String level, Long sourceId) {
        List<String> categories = new ArrayList<>();
        if (ResourceType.isProject(level)) {
            categories.addAll(projectMapCategoryMapper.selectProjectCategories(sourceId));
        }
        if (ResourceType.isOrganization(level) && enableOrganizationCategory) {
            OrganizationDTO organizationDTO = organizationMapper.selectByPrimaryKey(sourceId);
            if (organizationDTO != null) {
                categories.add(organizationDTO.getCategory());
            }
        }
        return categories;
    }

    private OrganizationDTO getOrganizationCategoryByProjectId(Long projectId) {
        ProjectDTO project = projectMapper.selectByPrimaryKey(projectId);
        if (project == null) {
            throw new CommonException("error.project.not.exist", projectId);
        }
        OrganizationDTO organization = organizationMapper.selectByPrimaryKey(project.getOrganizationId());
        if (organization == null) {
            throw new CommonException("error.organization.not.exist");
        }
        if (!enableOrganizationCategory){
            organization.setCategory(null);
        }
        return organization;
    }

    @Override
    public MenuDTO menuConfig(String code) {
        MenuDTO menu = getTopMenuByCode(code);
        String level = menu.getResourceLevel();
        Set<MenuDTO> menus = new HashSet<>(menuMapper.selectMenusWithPermission(level));
        menus = menus.stream().filter(t -> t.getId() != null).collect(Collectors.toSet());
        toTreeMenu(menu, menus, true);
        return menu;
    }

    private MenuDTO getTopMenuByCode(String code) {
        MenuDTO dto = new MenuDTO();
        dto.setCode(code);
        MenuDTO menu = menuMapper.selectOne(dto);
        if (menu == null) {
            throw new CommonException("error.menu.top.not.existed");
        }
        return menu;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMenuConfig(String code, List<MenuDTO> menus) {
        MenuDTO topMenu = getTopMenuByCode(code);
        validate(code, menus);
        String level = topMenu.getResourceLevel();
        // 传入的菜单列表
        List<MenuDTO> submitMenuList = menuTreeToList(menus);
        // 数据库已存在的菜单
        List<MenuDTO> existMenus = getMenuByResourceLevel(level);
        // 实际要插入的菜单
        List<MenuDTO> insertMenus = submitMenuList.stream().filter(item -> item.getId() == null).collect(Collectors.toList());
        // 传入的更新菜单列表
        List<MenuDTO> submitUpdateMenus = submitMenuList.stream().filter(item -> item.getId() != null).collect(Collectors.toList());
        // 实际要更新的菜单
        List<MenuDTO> updateMenus = new ArrayList<>();
        // 实际要删除的菜单
        List<MenuDTO> deleteMenus = new ArrayList<>();
        // 数据库已存在的菜单跟传入的更新菜单做对比  如果已存在的菜单不在更新菜单列表里 表示菜单已被删除 否则表示菜单需更新
        if (!CollectionUtils.isEmpty(existMenus)) {
            for (MenuDTO existMenu : existMenus) {
                boolean needToDelete = true;
                for (MenuDTO submitMenu : submitUpdateMenus) {
                    if (existMenu.getId().equals(submitMenu.getId())) {
                        updateMenus.add(submitMenu);
                        needToDelete = false;
                        break;
                    }
                }
                if (needToDelete && MenuType.isMenu(existMenu.getType())) {
                    boolean isNotDefaultMenu = existMenu.getDefault() != null && !existMenu.getDefault();
                    // 追溯到自设目录的根目录 只有与传入根目录相同的才删除
                    if (isNotDefaultMenu) {
                        MenuDTO deleteTopMenu = getTopMenu(existMenu);
                        if (deleteTopMenu != null && topMenu.getCode().equalsIgnoreCase(deleteTopMenu.getCode())) {
                            deleteMenus.add(existMenu);
                        }
                    }
                }
            }
        }
        //do insert
        if (!CollectionUtils.isEmpty(insertMenus)) {
            for (MenuDTO insertMenu : insertMenus) {
                MenuValidator.insertValidate(insertMenu, level);
                menuMapper.insertSelective(insertMenu);
            }
        }
        // do update
        if (!CollectionUtils.isEmpty(updateMenus)) {
            for (MenuDTO updateMenu : updateMenus) {
                boolean isNotDefault = MenuType.isMenu(updateMenu.getType()) && updateMenu.getDefault() != null && !updateMenu.getDefault();
                // only self menu can update name and icon
                MenuDTO menuDTO = new MenuDTO();
                if (isNotDefault) {
                    menuDTO.setName(updateMenu.getName());
                    menuDTO.setIcon(updateMenu.getIcon());
                }
                menuDTO.setSort(updateMenu.getSort());
                menuDTO.setParentCode(updateMenu.getParentCode());
                menuDTO.setId(updateMenu.getId());
                menuDTO.setObjectVersionNumber(updateMenu.getObjectVersionNumber());
                menuMapper.updateByPrimaryKeySelective(menuDTO);
            }
        }
        // do delete
        if (!CollectionUtils.isEmpty(deleteMenus)) {
            for (MenuDTO deleteMenu : deleteMenus) {
                MenuValidator.deleteValidate(deleteMenu);
                menuMapper.deleteByPrimaryKey(deleteMenu);
            }
        }
    }

    private void validate(String code, List<MenuDTO> menus) {
        menus.forEach(m -> {
            String parentCode = m.getParentCode();
            //由于菜单目前只能是两层结构，所以所有的menu父节点必须是top menu
            if (MenuType.isMenu(m.getType()) && !code.equals(parentCode)) {
                throw new CommonException("error.menu.illegal.parent.code", m.getCode());
            }
        });

    }

    @Override
    public List<MenuDTO> list() {
        return menuMapper.selectAll();
    }

    /**
     * 根据自设目录追溯到根目录.
     *
     * @param menuDTO 自设目录
     * @return 根目录
     */
    private MenuDTO getTopMenu(MenuDTO menuDTO) {
        if (MenuType.isTop(menuDTO.getType())) {
            return menuDTO;
        }
        MenuDTO result = new MenuDTO();
        result.setCode(menuDTO.getParentCode());
        result = menuMapper.selectOne(result);
        if (result == null) {
            logger.warn("parent menu {} does not exist", menuDTO.getParentCode());
            return null;
        }
        if (!MenuType.isTop(result.getType())) {
            result = getTopMenu(result);
        }
        return result;
    }

    /**
     * 根据资源层级查询菜单列表.
     *
     * @param level 资源层级
     * @return 菜单列表
     */
    private List<MenuDTO> getMenuByResourceLevel(String level) {
        MenuDTO menuDTO = new MenuDTO();
        menuDTO.setResourceLevel(level);
        return menuMapper.select(menuDTO);
    }

    /**
     * 树形菜单转换为List菜单.
     *
     * @param menus 树形菜单
     * @return List菜单
     */
    private List<MenuDTO> menuTreeToList(List<MenuDTO> menus) {
        List<MenuDTO> menuList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(menus)) {
            doProcessMenu(menus, menuList);
        }
        return menuList;
    }

    /**
     * 递归解析树形菜单为List菜单.
     *
     * @param menus    树形菜单
     * @param menuList List菜单
     */
    private void doProcessMenu(List<MenuDTO> menus, List<MenuDTO> menuList) {
        for (MenuDTO menuDTO : menus) {
            menuList.add(menuDTO);
            if (menuDTO.getSubMenus() != null) {
                doProcessMenu(menuDTO.getSubMenus(), menuList);
            }
        }
    }

    /**
     * 转换树形菜单.
     * 情况1：用户菜单不显示空目录
     * 情况2：菜单配置显示空目录
     *
     * @param parentMenu      父级菜单
     * @param menus           所有菜单列表
     * @param isShowEmptyMenu 是否显示空目录
     */
    private void toTreeMenu(MenuDTO parentMenu, Set<MenuDTO> menus, Boolean isShowEmptyMenu) {
        //建立映射索引
        Map<String, MenuDTO> menuMap = menus.stream().collect(Collectors.toMap(MenuDTO::getCode, menu -> menu));
        menuMap.put(parentMenu.getCode(), parentMenu);
        //生成菜单树
        for (MenuDTO menu : menuMap.values()) {
            MenuDTO currentParentMenu = menuMap.get(menu.getParentCode());
            if (currentParentMenu == null) {
                continue;
            }
            if (currentParentMenu.getSubMenus() == null) {
                currentParentMenu.setSubMenus(new ArrayList<>());
            }
            currentParentMenu.getSubMenus().add(menu);
            currentParentMenu.getSubMenus().sort(Comparator.comparing(MenuDTO::getSort));
        }
        //去除空菜单节点，如果需要
        if (!isShowEmptyMenu) {
            for (MenuDTO menu : menuMap.values()) {
                MenuDTO currentMenu = menu;
                //循环去除，因为存在C菜单为空，从B删除C后B也变为空也需要删除的情况
                while (MenuType.isMenu(currentMenu.getType()) && CollectionUtils.isEmpty(currentMenu.getSubMenus())) {
                    currentMenu = menuMap.get(currentMenu.getParentCode());
                    currentMenu.getSubMenus().remove(menu);
                }
            }
        }
    }

    @Override
    public void check(MenuDTO menu) {
        if (StringUtils.isEmpty(menu.getCode())) {
            throw new CommonException("error.menu.code.empty");
        }
        checkCode(menu);
    }

    private void checkCode(MenuDTO menu) {
        boolean createCheck = menu.getId() == null;
        MenuDTO dto = new MenuDTO();
        dto.setCode(menu.getCode());
        if (createCheck) {
            if (!menuMapper.select(dto).isEmpty()) {
                throw new CommonException("error.menu.code-level-type.exist");
            }
        } else {
            Long id = menu.getId();
            MenuDTO menuDTO = menuMapper.selectOne(dto);
            boolean existed = menuDTO != null && !id.equals(menuDTO.getId());
            if (existed) {
                throw new CommonException("error.menu.code-level-type.exist");
            }
        }
    }
}
