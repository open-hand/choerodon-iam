package io.choerodon.base.app.service.impl;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import io.choerodon.base.app.service.ProjectTypeService;
import io.choerodon.base.infra.dto.ProjectTypeDTO;
import io.choerodon.base.infra.mapper.ProjectTypeMapper;
import io.choerodon.core.exception.CommonException;

/**
 * @author superlee
 */
@Service
public class ProjectTypeServiceImpl implements ProjectTypeService {

    private ProjectTypeMapper projectTypeMapper;

    public ProjectTypeServiceImpl(ProjectTypeMapper projectTypeMapper) {
        this.projectTypeMapper = projectTypeMapper;
    }

    @Override
    public List<ProjectTypeDTO> list() {
        return projectTypeMapper.selectAll();
    }

    @Override
    public PageInfo<ProjectTypeDTO> pagingQuery(Pageable pageable, String name, String code, String param) {
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                .doSelectPageInfo(() -> projectTypeMapper.fuzzyQuery(name, code, param));
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
