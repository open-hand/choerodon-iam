package io.choerodon.base.app.service.impl;

import java.util.*;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.base.api.vo.ProjectCategoryEDTO;
import io.choerodon.base.app.service.ProjectCategoryService;
import io.choerodon.base.infra.dto.*;
import io.choerodon.base.infra.enums.CategoryResourceLevel;
import io.choerodon.base.infra.enums.MenuType;
import io.choerodon.base.infra.mapper.*;
import io.choerodon.core.exception.CommonException;
import io.choerodon.web.util.PageableHelper;

/**
 * @author jiameng.cao
 * @since 2019/6/4
 */
@Component
public class ProjectCategoryServiceImpl implements ProjectCategoryService {

    private ProjectCategoryMapper projectCategoryMapper;
    private CategoryMenuMapper categoryMenuMapper;
    private ProjectMapper projectMapper;
    private MenuMapper menuMapper;
    private ProjectMapCategoryMapper projectMapCategoryMapper;

    @Autowired
    private OrganizationCategoryServiceImpl organizationCategoryService;

    public static final String ERROR_PROJECT_CATEGORY_CANNOT_UPDATE = "error.projectCategory.update.cannot";

    public ProjectCategoryServiceImpl(ProjectCategoryMapper projectCategoryMapper, CategoryMenuMapper categoryMenuMapper, ProjectMapper projectMapper, MenuMapper menuMapper, ProjectMapCategoryMapper projectMapCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
        this.categoryMenuMapper = categoryMenuMapper;
        this.projectMapper = projectMapper;
        this.menuMapper = menuMapper;
        this.projectMapCategoryMapper = projectMapCategoryMapper;
    }

    @Override
    public PageInfo<ProjectCategoryDTO> getCategoriesByOrgId(Long organizationId, int page, int size, String param, ProjectCategoryDTO projectCategoryDTO) {
        return PageMethod.startPage(page, size).doSelectPageInfo(() -> projectCategoryMapper.selectProjectCategoriesByOrgId(organizationId, param, projectCategoryDTO));
    }

    @Override
    public PageInfo<ProjectCategoryDTO> getCategories(Pageable pageable, String param, ProjectCategoryDTO projectCategoryDTO) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(() -> projectCategoryMapper.selectByParam(param, projectCategoryDTO));
    }

    @Override
    @Transactional
    public ProjectCategoryEDTO createProjectCategory(ProjectCategoryEDTO createDTO, String resourceLevel) {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        BeanUtils.copyProperties(createDTO, projectCategoryDTO);
        if (projectCategoryMapper.insertSelective(projectCategoryDTO) != 1) {
            throw new CommonException("error.projectCategory.create");
        }
        CategoryMenuDTO categoryPermissionDTO = new CategoryMenuDTO();
        organizationCategoryService.createCategoryPermission(categoryPermissionDTO, createDTO.getCode(), resourceLevel, createDTO.getMenuCodes());
        return createDTO;
    }

    @Override
    public void checkProCategory(ProjectCategoryEDTO createDTO) {
        Boolean checkCode = !StringUtils.isEmpty(createDTO.getCode());
        if (!checkCode) {
            throw new CommonException("error.project.category.code.empty");
        } else {
            checkCode(createDTO);
        }
    }

    private void checkCode(ProjectCategoryEDTO createDTO) {
        Boolean createCheck = StringUtils.isEmpty(createDTO.getId());
        String code = createDTO.getCode();
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setCode(code);
        if (createCheck) {
            Boolean existed = projectCategoryMapper.selectOne(projectCategoryDTO) != null;
            if (existed) {
                throw new CommonException("error.project.category.code.exist");
            }
        } else {
            Long id = createDTO.getId();
            ProjectCategoryDTO projectCategoryDTO1 = projectCategoryMapper.selectOne(projectCategoryDTO);
            Boolean existed = projectCategoryDTO1 != null && !id.equals(projectCategoryDTO1.getId());
            if (existed) {
                throw new CommonException("error.project.category.code.exist");
            }
        }
    }

    @Override
    @Transactional
    public Boolean deleteProjectCategory(Long id) {
        String code = projectCategoryMapper.selectByPrimaryKey(id).getCode();
        ProjectDTO projectDTO = new ProjectDTO();
        CategoryMenuDTO categoryPermissionDTO = new CategoryMenuDTO();
        projectDTO.setCategory(code);
        categoryPermissionDTO.setCategoryCode(code);
        if (projectMapper.selectOne(projectDTO) != null) {
            throw new CommonException("error.projectCategory.delete.isUsed");
        }
        if (projectCategoryMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.projectCategory.delete");
        }
        if (categoryMenuMapper.selectByCode(code).size() > 0 && categoryMenuMapper.delete(categoryPermissionDTO) < 1) {
            throw new CommonException("error.projectCategory.delete.catePermission");
        }

        ProjectMapCategoryDTO projectMapCategoryDTO = new ProjectMapCategoryDTO();
        projectMapCategoryDTO.setCategoryId(id);
        if (projectMapCategoryMapper.select(projectMapCategoryDTO).size() > 0 && projectMapCategoryMapper.delete(projectMapCategoryDTO) < 1) {
            throw new CommonException("error.projectMapCategory.delete");
        }
        return true;
    }

    @Override
    public ProjectCategoryEDTO updateProjectCategory(ProjectCategoryEDTO updateDTO) {
        ProjectCategoryDTO projectCategoryDTO = projectCategoryMapper.selectByPrimaryKey(updateDTO.getId());
        if (projectCategoryDTO.getBuiltInFlag()) {
            throw new CommonException("error.projectCategory.update.builtFlag");
        }

        if (!updateDTO.getDisplayFlag().equals(projectCategoryDTO.getDisplayFlag())) {
            throw new CommonException(ERROR_PROJECT_CATEGORY_CANNOT_UPDATE);
        }

        if (!updateDTO.getCode().equals(projectCategoryDTO.getCode())) {
            throw new CommonException(ERROR_PROJECT_CATEGORY_CANNOT_UPDATE);
        }
        if (!updateDTO.getOrganizationId().equals(projectCategoryDTO.getOrganizationId())) {
            throw new CommonException(ERROR_PROJECT_CATEGORY_CANNOT_UPDATE);
        }

        ProjectCategoryDTO proCategoryDTO = new ProjectCategoryDTO();
        BeanUtils.copyProperties(updateDTO, proCategoryDTO);
        if (projectCategoryMapper.updateByPrimaryKey(proCategoryDTO) != 1) {
            throw new CommonException("error.projectCategory.update");
        }
        List<CategoryMenuDTO> submitList = categoryMenuMapper.selectByCode(updateDTO.getCode());
        organizationCategoryService.handleUpdateMenuCodes(updateDTO.getMenuCodes(), updateDTO.getCode(), submitList, CategoryResourceLevel.PROJECT.value());
        for (CategoryMenuDTO deleteDTO : submitList) {
            if (categoryMenuMapper.deleteByPrimaryKey(deleteDTO) != 1) {
                throw new CommonException("error.proCategory.delete.catePermission");
            }
        }
        return updateDTO;
    }

    @Override
    public ProjectCategoryEDTO getProCategoriesById(Long id) {
        ProjectCategoryEDTO projectCategoryEDTO = new ProjectCategoryEDTO();
        ProjectCategoryDTO projectCategoryDTO = projectCategoryMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(projectCategoryDTO, projectCategoryEDTO);
        String code = projectCategoryDTO.getCode();
        projectCategoryEDTO.setMenuCodes(categoryMenuMapper.selectPermissionCodeIdsByCode(code, CategoryResourceLevel.PROJECT.value()));
        return projectCategoryEDTO;
    }

    @Override
    public MenuDTO getProjectCategoryMenu(Long organizationId, String code) {
        List<String> menuCodes = categoryMenuMapper.getMenuCodesByOrgId(organizationId, CategoryResourceLevel.ORGANIZATION_PROJECT.value());
        MenuDTO menu = getTopMenuByCode(code);
        String level = menu.getResourceLevel();
        int i;
        Set<MenuDTO> menus = new HashSet<>(menuMapper.selectMenusWithPermission(level));
        Set<MenuDTO> menuCode = new HashSet<>();
        for (MenuDTO menuDTO : menus) {
            for (i = 0; i < menuCodes.size(); i++) {
                if (menuDTO.getCode().equals(menuCodes.get(i))) {
                    menuCode.add(menuDTO);
                    break;
                }
            }
        }
        toTreeMenu(menu, menuCode, true);
        return menu;
    }

    @Override
    public PageInfo<ProjectCategoryDTO> pagingProjectCategoryList(Long organizationId, int page, int size, String param) {
        return PageMethod.startPage(page, size).doSelectPageInfo(() -> projectCategoryMapper.selectProjectCategoriesListByOrgId(organizationId, param));
    }

    private void toTreeMenu(MenuDTO parentMenu, Set<MenuDTO> menus, Boolean isShowEmptyMenu) {
        String code = parentMenu.getCode();
        List<MenuDTO> subMenus = new ArrayList<>();
        for (MenuDTO menu : menus) {
            if (code.equalsIgnoreCase(menu.getParentCode())) {
                // 如果是叶子菜单 直接放到父级目录的子菜单列表里面
                if (MenuType.isMenuItem(menu.getType())) {
                    subMenus.add(menu);
                }
                if (MenuType.isMenu(menu.getType())) {
                    toTreeMenu(menu, menus, isShowEmptyMenu);
                    if (isShowEmptyMenu) {
                        subMenus.add(menu);
                    } else {
                        // 目录有叶子菜单 放到父级目录的子目录里面(过滤空目录)
                        if (!CollectionUtils.isEmpty(menu.getSubMenus())) {
                            subMenus.add(menu);
                        }
                    }
                }
            }
        }
        if (CollectionUtils.isEmpty(subMenus)) {
            parentMenu.setSubMenus(null);
        } else {
            subMenus.sort(Comparator.comparing(MenuDTO::getSort));
            parentMenu.setSubMenus(subMenus);
        }
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

}
