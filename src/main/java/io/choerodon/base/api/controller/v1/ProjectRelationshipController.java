package io.choerodon.base.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.ProjectRelationshipService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.ProjectRelationshipDTO;

/**
 * @author Eugen
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/project_relations")
public class ProjectRelationshipController {

    private ProjectRelationshipService projectRelationshipService;

    public ProjectRelationshipController(ProjectRelationshipService projectRelationshipService) {
        this.projectRelationshipService = projectRelationshipService;
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "查询项目群下的子项目(默认查所有子项目，可传参只查启用的子项目)")
    @GetMapping(value = "/{project_id}/{parent_id}")
    public ResponseEntity<List<ProjectRelationshipDTO>> getProjUnderGroup(@PathVariable(name = "organization_id") Long orgId,
                                                                          @PathVariable(name = "parent_id") Long id,
                                                                          @RequestParam(name = "only_select_enable", required = false, defaultValue = "false") Boolean onlySelectEnable) {
        return new ResponseEntity<>(projectRelationshipService.getProjUnderGroup(id, onlySelectEnable), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "项目群下移除项目")
    @DeleteMapping("/{project_id}/{relationship_id}")
    public ResponseEntity delete(@PathVariable(name = "organization_id") Long orgId,
                                 @PathVariable(name = "relationship_id") Long id) {
        projectRelationshipService.removesAProjUnderGroup(orgId, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "项目群下批量更新（添加/修改/启停用）子项目")
    @PutMapping("/{project_id}")
    public ResponseEntity<List<ProjectRelationshipDTO>> create(@PathVariable(name = "organization_id") Long orgId,
                                                               @RequestBody @Valid List<ProjectRelationshipDTO> projectRelationshipDTOList) {
        return new ResponseEntity<>(projectRelationshipService.batchUpdateRelationShipUnderProgram(orgId, projectRelationshipDTOList), HttpStatus.OK);
    }
}
