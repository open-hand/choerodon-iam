package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.RoleVO;
import io.choerodon.iam.app.service.OrganizationRoleService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @Date 2020/4/22 10:05
 */
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/roles")
public class OrganizationRoleC7nController {

    private OrganizationRoleService organizationRoleService;

    public OrganizationRoleC7nController(OrganizationRoleService organizationRoleService) {
        this.organizationRoleService = organizationRoleService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建角色并分配权限")
    @PostMapping
    public ResponseEntity<Void> create(
            @PathVariable("organization_id") Long organizationId,
            @RequestBody RoleVO roleVO) {
        organizationRoleService.create(organizationId, roleVO);
        return ResponseEntity.noContent().build();
    }
}
