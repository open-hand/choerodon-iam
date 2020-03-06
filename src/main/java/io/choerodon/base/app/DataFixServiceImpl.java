package io.choerodon.base.app;

import java.util.List;

import org.springframework.stereotype.Service;

import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.app.service.DataFixService;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.mapper.OrganizationMapper;
import io.choerodon.base.infra.mapper.ProjectMapper;

/**
 * @author: 25499
 * @date: 2020/3/3 19:41
 * @description:
 */
@Service
public class DataFixServiceImpl implements DataFixService {

    private OrganizationMapper organizationMapper;
    private ProjectMapper projectMapper;

    public DataFixServiceImpl(OrganizationMapper organizationMapper, ProjectMapper projectMapper) {
        this.organizationMapper = organizationMapper;
        this.projectMapper = projectMapper;
    }

    @Override
    public List<ProjectDTO> getAllproList() {
        return projectMapper.selectAll();
    }

    @Override
    public List<OrganizationSimplifyDTO> getAllOrgsList() {
        return organizationMapper.selectAllOrgIdAndName();
    }
}
