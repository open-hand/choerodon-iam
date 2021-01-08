package io.choerodon.iam.app.service;


import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public interface ProjectCategoryC7nService {

    List<ProjectCategoryDTO> list();

    Page<ProjectCategoryDTO> pagingQuery(PageRequest Pageable, String name, String code, String param);

    ProjectCategoryDTO create(ProjectCategoryDTO projectCategoryDTO);

    ProjectCategoryDTO update(Long id, ProjectCategoryDTO projectCategoryDTO);

    void check(ProjectCategoryDTO projectCategoryDTO);

    /**
     * 过滤项目类型，
     * @param projectDTOS
     * @return
     */
    List<ProjectDTO> filterCategory(List<ProjectDTO> projectDTOS);
}
