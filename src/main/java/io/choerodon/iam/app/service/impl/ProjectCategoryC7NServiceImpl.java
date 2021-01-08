package io.choerodon.iam.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.ProjectCategoryC7nService;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.enums.ProjectCategoryEnum;
import io.choerodon.iam.infra.mapper.ProjectCategoryMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @since 2020/4/15
 *
 */
@Service
public class ProjectCategoryC7NServiceImpl implements ProjectCategoryC7nService {

    private ProjectCategoryMapper projectCategoryMapper;

    public ProjectCategoryC7NServiceImpl(ProjectCategoryMapper projectCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
    }

    @Override
    public List<ProjectCategoryDTO> list() {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        projectCategoryDTO.setDisplayFlag(true);
        return projectCategoryMapper.select(projectCategoryDTO)
                .stream()
                .filter(category -> ProjectCategoryEnum.contains(category.getCode()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProjectCategoryDTO> pagingQuery(PageRequest pageRequest, String name, String code, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> projectCategoryMapper.fuzzyQuery(name, code, param));
    }

    @Override
    public ProjectCategoryDTO create(ProjectCategoryDTO projectTypeDTO) {
        if (projectCategoryMapper.insertSelective(projectTypeDTO) != 1) {
            throw new CommonException("error.projectType.insert");
        }
        return projectCategoryMapper.selectByPrimaryKey(projectTypeDTO.getId());
    }

    @Override
    public ProjectCategoryDTO update(Long id, ProjectCategoryDTO projectTypeDTO) {
        Assert.notNull(projectTypeDTO.getObjectVersionNumber(), "error.objectVersionNumber.null");
        if (projectCategoryMapper.selectByPrimaryKey(id) == null) {
            throw new CommonException("error.projectType.not.exist");
        }
        projectTypeDTO.setCode(null);
        if (projectCategoryMapper.updateByPrimaryKeySelective(projectTypeDTO) != 1) {
            throw new CommonException("error.projectType.update");
        }
        return projectCategoryMapper.selectByPrimaryKey(id);
    }

    @Override
    public void check(ProjectCategoryDTO projectTypeDTO) {
        Assert.notNull(projectTypeDTO.getCode(), "error.projectType.code.illegal");
        boolean updateCheck = !ObjectUtils.isEmpty(projectTypeDTO.getId());
        ProjectCategoryDTO example = new ProjectCategoryDTO();
        example.setCode(projectTypeDTO.getCode());
        if (updateCheck) {
            long id = projectTypeDTO.getId();
            ProjectCategoryDTO dto = projectCategoryMapper.selectOne(example);
            if (!ObjectUtils.isEmpty(dto) && !dto.getId().equals(id)) {
                throw new CommonException("error.projectType.code.existed");
            }
        } else {
            if (!projectCategoryMapper.select(example).isEmpty()) {
                throw new CommonException("error.projectType.code.existed");
            }
        }
    }

    @Override
    public List<ProjectDTO> filterCategory(List<ProjectDTO> projectDTOS) {
        if (!CollectionUtils.isEmpty(projectDTOS)) {
            projectDTOS.forEach(projectDTO -> {

                List<ProjectCategoryDTO> categories = projectDTO.getCategories();
                if (!CollectionUtils.isEmpty(categories)) {
                    List<ProjectCategoryDTO> filteredCategory = categories.stream().filter(c -> ProjectCategoryEnum.listNewCategories().contains(c)).collect(Collectors.toList());
                    projectDTO.setCategories(filteredCategory);
                }
            });
        }
        return projectDTOS;
    }
}
