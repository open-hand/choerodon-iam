package io.choerodon.base.app.service;

import java.util.List;

import io.choerodon.base.api.dto.OrganizationSimplifyDTO;
import io.choerodon.base.infra.dto.ProjectDTO;

/**
 * @author: 25499
 * @date: 2020/3/3 19:41
 * @description:
 */
public interface DataFixService {


    /**
     * 查询所有组织
     * @return
     */
    List<OrganizationSimplifyDTO> getAllOrgsList();

    List<ProjectDTO> getAllproList();
}
