package io.choerodon.iam.app.service;


import java.util.List;

import org.springframework.data.domain.Pageable;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.ProjectTypeDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

public interface ProjectTypeC7nService {

    List<ProjectTypeDTO> list();

    Page<ProjectTypeDTO> pagingQuery(PageRequest Pageable, String name, String code, String param);

    ProjectTypeDTO create(ProjectTypeDTO projectTypeDTO);

    ProjectTypeDTO update(Long id, ProjectTypeDTO projectTypeDTO);

    void check(ProjectTypeDTO projectTypeDTO);
}
