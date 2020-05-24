package io.choerodon.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.ProjectCategoryC7nService;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.mapper.ProjectCategoryMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
@Service
public class ProjectCategoryC7NServiceImpl implements ProjectCategoryC7nService {

    private ProjectCategoryMapper projectCategoryMapper;

    public ProjectCategoryC7NServiceImpl(ProjectCategoryMapper projectCategoryMapper) {
        this.projectCategoryMapper = projectCategoryMapper;
    }

    @Override
    public List<ProjectCategoryDTO> list() {
        return projectCategoryMapper.selectAll();
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
}
