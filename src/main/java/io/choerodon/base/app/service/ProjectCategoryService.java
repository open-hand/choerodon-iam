package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.api.vo.ProjectCategoryEDTO;
import io.choerodon.base.infra.dto.MenuDTO;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;


/**
 * @author jiameng.cao
 * @date 2019/6/4
 */
public interface ProjectCategoryService {
    PageInfo<ProjectCategoryDTO> getCategoriesByOrgId(Long organizationId, int page, int size, String param, ProjectCategoryDTO projectCategoryDTO);

    PageInfo<ProjectCategoryDTO> getCategories(Pageable Pageable, String param, ProjectCategoryDTO projectCategoryDTO);

    ProjectCategoryEDTO createProjectCategory(ProjectCategoryEDTO createDTO, String resourceLevel);

    void checkProCategory(ProjectCategoryEDTO createDTO);

    Boolean deleteProjectCategory(Long id);

    ProjectCategoryEDTO updateProjectCategory(ProjectCategoryEDTO updateDTO);

    ProjectCategoryEDTO getProCategoriesById(Long id);

    MenuDTO getProjectCategoryMenu(Long organizationId, String code);

    PageInfo<ProjectCategoryDTO> pagingProjectCategoryList(Long organizationId, int page, int size, String param);
}
