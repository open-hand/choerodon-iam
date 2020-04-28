package io.choerodon.iam.api.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiOperation;
import org.hzero.iam.app.service.PermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.PermissionC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/choerodon/v1/permissions")
public class PermissionC7nController {

    private PermissionService permissionService;
    private PermissionC7nService permissionC7nService;

    public PermissionC7nController(PermissionService permissionService,
                                   PermissionC7nService permissionC7nService) {
        this.permissionService = permissionService;
        this.permissionC7nService = permissionC7nService;
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("通过角色查询权限列表")
    @PostMapping
    public ResponseEntity<Set<org.hzero.iam.domain.entity.Permission>> queryByRoleIds(@RequestBody List<Long> roleIds) {
        return new ResponseEntity<>(permissionC7nService.queryByRoleIds(roleIds), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("组织层通过角色查询权限列表")
    @PostMapping("/through_roles_at_org/{organization_id}")
    public ResponseEntity<Set<org.hzero.iam.domain.entity.Permission>> queryByRoleIdsAtOrg(@PathVariable(name = "organization_id") Long organizationId, @RequestBody List<Long> roleIds) {
        return new ResponseEntity<>(permissionC7nService.queryByRoleIds(roleIds), HttpStatus.OK);
    }



    /**
     * 根据传入的permission code，与最新更新的Instance抓取的swagger json对比，如果已经废弃了，就删除，没有废弃抛异常
     *
     * @param code the code of permission
     * @return
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("根据permission code删除permission, 只能删除废弃的permission")
    @DeleteMapping
    public ResponseEntity deleteByCode(@RequestParam String code) {
        List<org.hzero.iam.domain.entity.Permission> permissions = new ArrayList<>();
        org.hzero.iam.domain.entity.Permission permission = new org.hzero.iam.domain.entity.Permission();
        permission.setCode(code);
        permissions.add(permission);
        permissionService.deleteApis(permissions);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
