package io.choerodon.base.app.service;


import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.ProjectTypeDTO;

import java.util.List;

public interface ProjectTypeService {

    List<ProjectTypeDTO> list();

    PageInfo<ProjectTypeDTO> pagingQuery(Pageable Pageable, String name, String code, String param);

    ProjectTypeDTO create(ProjectTypeDTO projectTypeDTO);

    ProjectTypeDTO update(Long id, ProjectTypeDTO projectTypeDTO);

    void check(ProjectTypeDTO projectTypeDTO);
}
