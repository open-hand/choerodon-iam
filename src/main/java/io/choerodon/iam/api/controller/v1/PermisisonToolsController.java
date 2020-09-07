package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.swagger.annotation.Permission;


@RestController
@RequestMapping(value = "/choerodon/v1/permission")
public class PermisisonToolsController {

    @Autowired
    private PermissionC7nService permissionC7nService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("同步子角色权限")
    @PutMapping("/role_permission_async")
    public ResponseEntity<Void> asyncRolePermission() {
        permissionC7nService.asyncRolePermission();
        return ResponseEntity.noContent().build();
    }
}
