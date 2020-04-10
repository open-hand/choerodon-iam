package io.choerodon.iam.infra.asserts;

import org.springframework.stereotype.Component;

import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.mapper.ProjectMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;

/**
 * 项目断言帮助类
 *
 * @author superlee
 * @since 2019-05-13
 */
@Component
public class ProjectAssertHelper extends AssertHelper {

    private ProjectMapper projectMapper;

    public ProjectAssertHelper(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    public ProjectDTO projectNotExisted(Long id) {
        return projectNotExisted(id, "error.project.not.exist");
    }

    public ProjectDTO projectNotExisted(Long id, String message) {
        ProjectDTO dto = projectMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException(message, id);
        }
        return dto;
    }

    public void codeExisted(String code, Long organizationId) {
        codeExisted(code, organizationId, "error.project.code.exist");
    }

    public void codeExisted(String code, Long organizationId, String message) {
        ProjectDTO dto = new ProjectDTO();
        dto.setCode(code);
        dto.setOrganizationId(organizationId);
        if (projectMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException(message);
        }
    }


}
