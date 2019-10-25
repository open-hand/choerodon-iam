package io.choerodon.base.app.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.choerodon.base.api.dto.RelationshipCheckDTO;
import io.choerodon.base.infra.dto.ProjectRelationshipDTO;

/**
 * @author Eugen
 */
public interface ProjectRelationshipService {

    /**
     * 查询一个项目群下的子项目(默认查所有子项目，可传参只查启用的子项目).
     *
     * @param parentId         父级Id
     * @param onlySelectEnable 是否只查启用项目
     * @return 项目群下的子项目列表
     */
    List<ProjectRelationshipDTO> getProjUnderGroup(Long parentId, Boolean onlySelectEnable);

    /**
     * 项目组下移除项目
     *
     * @param orgId   组织Id
     * @param groupId 项目群关系Id
     */
    void removesAProjUnderGroup(Long orgId, Long groupId);

    /**
     * 批量修改/新增/启停用项目组
     *
     * @param list
     * @return
     */
    List<ProjectRelationshipDTO> batchUpdateRelationShipUnderProgram(Long orgId, List<ProjectRelationshipDTO> list);
}
