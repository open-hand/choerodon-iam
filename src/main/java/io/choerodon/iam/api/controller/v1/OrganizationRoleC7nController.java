package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.iam.config.SwaggerApiConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.app.service.OrganizationRoleC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/4/22 10:05
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_MENU_ROLE)
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/roles")
public class OrganizationRoleC7nController {

    private OrganizationRoleC7nService organizationRoleC7nService;

    public OrganizationRoleC7nController(OrganizationRoleC7nService organizationRoleC7nService) {
        this.organizationRoleC7nService = organizationRoleC7nService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建角色并分配权限")
    @PostMapping
    public ResponseEntity<Void> create(
            @PathVariable("organization_id") Long organizationId,
            @RequestBody RoleVO roleVO) {
        organizationRoleC7nService.create(organizationId, roleVO);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改角色信息")
    @PutMapping("/{role_id}")
    public ResponseEntity<Void> update(
            @PathVariable("organization_id") Long organizationId,
            @PathVariable("role_id") Long roleId,
            @RequestBody RoleVO roleVO) {
        organizationRoleC7nService.update(organizationId, roleId, roleVO);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织下的角色列表")
    @GetMapping
    public ResponseEntity<List<RoleVO>> list(
            @PathVariable("organization_id") Long organizationId) {
        return ResponseEntity.ok(organizationRoleC7nService.list(organizationId));
    }

}
