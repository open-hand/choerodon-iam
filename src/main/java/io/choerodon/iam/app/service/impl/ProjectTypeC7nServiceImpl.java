package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.ProjectTypeC7nService;
import io.choerodon.iam.infra.dto.ProjectTypeDTO;
import io.choerodon.iam.infra.mapper.ProjectTypeMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/4/15
 * @description
 */
@Service
public class ProjectTypeC7nServiceImpl implements ProjectTypeC7nService {

    private ProjectTypeMapper projectTypeMapper;

    public ProjectTypeC7nServiceImpl(ProjectTypeMapper projectTypeMapper) {
        this.projectTypeMapper = projectTypeMapper;
    }

    @Override
    public List<ProjectTypeDTO> list() {
        return projectTypeMapper.selectAll();
    }

    @Override
    public Page<ProjectTypeDTO> pagingQuery(PageRequest pageRequest, String name, String code, String param) {
        return PageHelper.doPageAndSort(pageRequest, () -> projectTypeMapper.fuzzyQuery(name, code, param));
    }

    @Override
    public ProjectTypeDTO create(ProjectTypeDTO projectTypeDTO) {
        if (projectTypeMapper.insertSelective(projectTypeDTO) != 1) {
            throw new CommonException("error.projectType.insert");
        }
        return projectTypeMapper.selectByPrimaryKey(projectTypeDTO.getId());
    }

    @Override
    public ProjectTypeDTO update(Long id, ProjectTypeDTO projectTypeDTO) {
        Assert.notNull(projectTypeDTO.getObjectVersionNumber(), "error.objectVersionNumber.null");
        if (projectTypeMapper.selectByPrimaryKey(id) == null) {
            throw new CommonException("error.projectType.not.exist");
        }
        projectTypeDTO.setCode(null);
        if (projectTypeMapper.updateByPrimaryKeySelective(projectTypeDTO) != 1) {
            throw new CommonException("error.projectType.update");
        }
        return projectTypeMapper.selectByPrimaryKey(id);
    }

    @Override
    public void check(ProjectTypeDTO projectTypeDTO) {
        Assert.notNull(projectTypeDTO.getCode(), "error.projectType.code.illegal");
        boolean updateCheck = !ObjectUtils.isEmpty(projectTypeDTO.getId());
        ProjectTypeDTO example = new ProjectTypeDTO();
        example.setCode(projectTypeDTO.getCode());
        if (updateCheck) {
            long id = projectTypeDTO.getId();
            ProjectTypeDTO dto = projectTypeMapper.selectOne(example);
            if (!ObjectUtils.isEmpty(dto) && !dto.getId().equals(id)) {
                throw new CommonException("error.projectType.code.existed");
            }
        } else {
            if (!projectTypeMapper.select(example).isEmpty()) {
                throw new CommonException("error.projectType.code.existed");
            }
        }
    }
}
