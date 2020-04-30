package io.choerodon.base.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.ProjectCategoryService;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.base.infra.enums.ProjectCategoryEnum;
import io.choerodon.base.infra.mapper.ProjectCategoryMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/30 10:27
 */
@Service
public class ProjectCategoryServiceImpl implements ProjectCategoryService {

    private ProjectCategoryMapper projectCategoryMapper;

    public ProjectCategoryServiceImpl(ProjectCategoryMapper projectCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
    }

    @Override
    public List<ProjectCategoryDTO> list() {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setBuiltInFlag(true);
        projectCategoryDTO.setDisplayFlag(true);
        return projectCategoryMapper.select(projectCategoryDTO)
                .stream()
                .filter(category -> ProjectCategoryEnum.contains(category.getCode()))
                .collect(Collectors.toList());
    }
}
