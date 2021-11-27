package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OrganizationProjectC7nService;
import io.choerodon.iam.app.service.ProjectPermissionService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.swagger.annotation.Permission;

/**
 * @Author: scp 给prod-code-service的接口
 * @Description:
 * @Date: Created in 2021/2/26
 * @Modified By:
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_PROD)
@RestController
@RequestMapping(value = "/v1/prod")
public class ProdController {
    @Autowired
    private OrganizationProjectC7nService organizationProjectC7nService;
    @Autowired
    private ProjectPermissionService projectPermissionService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "查询组织下所有项目")
    @GetMapping(value = "/organizations/{organization_id}/projects/all")
    public ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId,
                                                                @RequestParam(required = false) String category,
                                                                @RequestParam(required = false) Boolean enabled) {
        return new ResponseEntity<>(organizationProjectC7nService.listProjectsByOrgId(organizationId, category, enabled), HttpStatus.OK);
    }
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("根据项目id查询项目下的项目所有者")
    @GetMapping("/projects/{project_id}/owner/list")
    public ResponseEntity<List<UserDTO>> listProjectOwnerById(@PathVariable(name = "project_id") Long projectId,
                                                              @RequestParam(name = "param", required = false) String param) {
        return ResponseEntity.ok(projectPermissionService.listProjectOwnerById(projectId, param));
    }

}
