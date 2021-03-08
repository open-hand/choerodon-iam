package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.api.vo.ProjectWithUserVO;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.ProjectPermissionService;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author superlee
 * @since 2021-03-08
 */
@RestController
@RequestMapping(value = "/choerodon/v1/agile")
public class AgileFeignController {


    @Autowired
    private ProjectC7nService projectService;

    @Autowired
    private ProjectPermissionService projectPermissionService;

    @Permission(permissionWithin = true)
    @GetMapping(value = "/projects/all")
    @ApiOperation(value = "查询所有项目")
    public ResponseEntity<List<ProjectDTO>> listAll(@RequestParam Boolean enabled) {
        return new ResponseEntity<>(projectService.listAll(enabled), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation("根据项目id集合查询项目下的项目所有者")
    @PostMapping("/projects/list_owner")
    public ResponseEntity<List<ProjectWithUserVO>> listProjectOwnerByIds(@RequestBody Set<Long> projectIds) {
        return ResponseEntity.ok(projectPermissionService.listProjectOwnerByIds(projectIds));
    }
}
