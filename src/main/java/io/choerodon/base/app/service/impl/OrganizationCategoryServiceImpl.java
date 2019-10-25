package io.choerodon.base.app.service.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.choerodon.base.api.vo.MenuCodeDTO;
import io.choerodon.base.api.vo.OrganizationCategoryEDTO;
import io.choerodon.base.app.service.OrganizationCategoryService;
import io.choerodon.base.infra.dto.CategoryMenuDTO;
import io.choerodon.base.infra.dto.OrganizationCategoryDTO;
import io.choerodon.base.infra.dto.OrganizationDTO;
import io.choerodon.base.infra.enums.CategoryResourceLevel;
import io.choerodon.base.infra.mapper.CategoryMenuMapper;
import io.choerodon.base.infra.mapper.OrganizationCategoryMapper;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.web.util.PageableHelper;

/**
 * @author jiameng.cao
 * @since 2019/6/5
 */
@Component
public class OrganizationCategoryServiceImpl implements OrganizationCategoryService {


    private OrganizationCategoryMapper organizationCategoryMapper;
    private CategoryMenuMapper categoryMenuMapper;
    private OrganizationMapper organizationMapper;

    public static final String ERROR_ORG_CATEGORY_CANNOT_UPDATE = "error.orgCategory.update.cannot";


    public OrganizationCategoryServiceImpl(OrganizationCategoryMapper organizationCategoryMapper, CategoryMenuMapper categoryMenuMapper, OrganizationMapper organizationMapper) {
        this.organizationCategoryMapper = organizationCategoryMapper;
        this.categoryMenuMapper = categoryMenuMapper;
        this.organizationMapper = organizationMapper;
    }

    public PageInfo<OrganizationCategoryDTO> getOrgCategories(Pageable pageable, String param, OrganizationCategoryDTO organizationCategoryDTO) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize(), PageableHelper.getSortSql(pageable.getSort())).doSelectPageInfo(() -> organizationCategoryMapper.selectByParam(param, organizationCategoryDTO));
    }

    @Override
    @Transactional
    public OrganizationCategoryEDTO createOrgCategory(OrganizationCategoryEDTO createDTO, String proResourceLevel, String orgResourceLevel) {
        OrganizationCategoryDTO organizationCategoryDTO = new OrganizationCategoryDTO();
        BeanUtils.copyProperties(createDTO, organizationCategoryDTO);
        if (organizationCategoryMapper.insertSelective(organizationCategoryDTO) != 1) {
            throw new CommonException("error.orgCategories.create");
        }
        String code = createDTO.getCode();
        CategoryMenuDTO proPermissionDTO = new CategoryMenuDTO();
        CategoryMenuDTO orgPermissionDTO = new CategoryMenuDTO();
        createCategoryPermission(proPermissionDTO, code, proResourceLevel, createDTO.getMenuProCodes());
        createCategoryPermission(orgPermissionDTO, code, orgResourceLevel, createDTO.getMenuOrgCodes());
        return createDTO;
    }

    @Override
    @Transactional
    public Boolean deleteOrgCategory(Long id) {
        String code = organizationCategoryMapper.selectByPrimaryKey(id).getCode();
        OrganizationDTO organizationDTO = new OrganizationDTO();
        CategoryMenuDTO categoryPermissionDTO = new CategoryMenuDTO();
        organizationDTO.setCategory(code);
        categoryPermissionDTO.setCategoryCode(code);
        if (organizationMapper.selectOne(organizationDTO) != null) {
            throw new CommonException("error.orgCategory.delete.isUsed");
        }
        if (organizationCategoryMapper.deleteByPrimaryKey(id) != 1) {
            throw new CommonException("error.orgCategory.delete");
        }
        if (categoryMenuMapper.selectByCode(code).size() > 0 && categoryMenuMapper.delete(categoryPermissionDTO) < 1) {
            throw new CommonException("error.orgCategory.delete.catePermission");
        }
        return true;
    }

    @Override
    public void checkOrgCategory(OrganizationCategoryEDTO organizationCategoryEDTO) {
        Boolean checkCode = !StringUtils.isEmpty(organizationCategoryEDTO.getCode());
        if (!checkCode) {
            throw new CommonException("error.organization.category.code.empty");
        } else {
            checkCode(organizationCategoryEDTO);
        }
    }

    private void checkCode(OrganizationCategoryEDTO organizationCategoryEDTO) {
        Boolean createCheck = StringUtils.isEmpty(organizationCategoryEDTO.getId());
        String code = organizationCategoryEDTO.getCode();
        OrganizationCategoryDTO organizationCategoryDTO = new OrganizationCategoryDTO();
        organizationCategoryDTO.setCode(code);
        if (createCheck) {
            Boolean existed = organizationCategoryMapper.selectOne(organizationCategoryDTO) != null;
            if (existed) {
                throw new CommonException("error.organization.category.code.exist");
            }
        } else {
            Long id = organizationCategoryEDTO.getId();
            OrganizationCategoryDTO organizationCategoryDTO1 = organizationCategoryMapper.selectOne(organizationCategoryDTO);
            Boolean existed = organizationCategoryDTO1 != null && !id.equals(organizationCategoryDTO1.getId());
            if (existed) {
                throw new CommonException("error.organization.category.code.exist");
            }
        }
    }

    @Override
    @Transactional
    public OrganizationCategoryEDTO updateOrgCategory(OrganizationCategoryEDTO updateDTO) {
        OrganizationCategoryDTO organizationCategoryDTO = organizationCategoryMapper.selectByPrimaryKey(updateDTO.getId());
        if (organizationCategoryDTO.getBuiltInFlag()) {
            throw new CommonException("error.orgCategory.update.builtFlag");
        }
        if (!updateDTO.getCode().equals(organizationCategoryDTO.getCode())) {
            throw new CommonException(ERROR_ORG_CATEGORY_CANNOT_UPDATE);
        }
        OrganizationCategoryDTO orgCategoryDTO = new OrganizationCategoryDTO();
        BeanUtils.copyProperties(updateDTO, orgCategoryDTO);
        if (organizationCategoryMapper.updateByPrimaryKey(orgCategoryDTO) != 1) {
            throw new CommonException("error.orgCategory.update");
        }
        List<CategoryMenuDTO> submitList = categoryMenuMapper.selectByCode(updateDTO.getCode());
        String code = updateDTO.getCode();
        handleUpdateMenuCodes(updateDTO.getMenuOrgCodes(), code, submitList, CategoryResourceLevel.ORGANIZATION.value());
        handleUpdateMenuCodes(updateDTO.getMenuProCodes(), code, submitList, CategoryResourceLevel.ORGANIZATION_PROJECT.value());

        for (CategoryMenuDTO deleteDTO : submitList) {
            if (categoryMenuMapper.deleteByPrimaryKey(deleteDTO) != 1) {
                throw new CommonException("error.orgCategory.delete.catePermission");
            }
        }
        return updateDTO;
    }


    @Override
    public OrganizationCategoryEDTO getOrgCategoriesById(Long id) {
        OrganizationCategoryEDTO organizationCategoryEDTO = new OrganizationCategoryEDTO();
        OrganizationCategoryDTO organizationCategoryDTO = organizationCategoryMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(organizationCategoryDTO, organizationCategoryEDTO);
        String code = organizationCategoryDTO.getCode();
        organizationCategoryEDTO.setMenuProCodes(categoryMenuMapper.selectPermissionCodeIdsByCode(code, CategoryResourceLevel.ORGANIZATION_PROJECT.value()));
        organizationCategoryEDTO.setMenuOrgCodes(categoryMenuMapper.selectPermissionCodeIdsByCode(code, CategoryResourceLevel.ORGANIZATION.value()));
        return organizationCategoryEDTO;
    }

    public void handleUpdateMenuCodes(List<MenuCodeDTO> menuDTOList, String code, List<CategoryMenuDTO> submitList, String resourceLevel) {
        if (menuDTOList.size() > 0) {
            for (MenuCodeDTO menuCode : menuDTOList) {
                CategoryMenuDTO categoryDTO = new CategoryMenuDTO();
                categoryDTO.setCategoryCode(code);
                categoryDTO.setMenuCode(menuCode.getMenuCode());
                categoryDTO.setResourceLevel(resourceLevel);
                CategoryMenuDTO categoryMenuDTO = categoryMenuMapper.selectOne(categoryDTO);
                if (categoryMenuDTO == null) {
                    if (categoryMenuMapper.insert(categoryDTO) != 1) {
                        throw new CommonException("error.categoryMenu.insert");
                    }
                } else {
                    for (int i = 0; i < submitList.size(); i++) {
                        if (submitList.get(i).getId().equals(categoryMenuDTO.getId())) {
                            submitList.remove(i);
                            break;
                        }
                    }

                }
            }
        }
    }

    public void createCategoryPermission(CategoryMenuDTO permissionDTO, String code, String resourceLevel, List<MenuCodeDTO> menuCodeDTOS) {
        permissionDTO.setCategoryCode(code);
        permissionDTO.setResourceLevel(resourceLevel);
        if (menuCodeDTOS.size() > 0) {
            for (MenuCodeDTO menuCodeDTO : menuCodeDTOS) {
                permissionDTO.setId(null);
                permissionDTO.setMenuCode(menuCodeDTO.getMenuCode());
                if (categoryMenuMapper.insertSelective(permissionDTO) != 1) {
                    throw new CommonException("error.categoryPermission.create");
                }
            }
        }
    }

}
