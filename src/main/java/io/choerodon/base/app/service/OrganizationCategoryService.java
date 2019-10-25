package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.api.vo.OrganizationCategoryEDTO;
import io.choerodon.base.infra.dto.OrganizationCategoryDTO;


/**
 * @author jiameng.cao
 * @date 2019/6/5
 */
public interface OrganizationCategoryService {

    PageInfo<OrganizationCategoryDTO> getOrgCategories(Pageable Pageable, String param, OrganizationCategoryDTO organizationCategoryDTO);

    OrganizationCategoryEDTO createOrgCategory(OrganizationCategoryEDTO createDTO, String proResourceLevel, String orgResourceLevel);

    Boolean deleteOrgCategory(Long id);

    void checkOrgCategory(OrganizationCategoryEDTO organizationCategoryEDTO);

    OrganizationCategoryEDTO updateOrgCategory(OrganizationCategoryEDTO updateDTO);

    OrganizationCategoryEDTO getOrgCategoriesById(Long id);
}
