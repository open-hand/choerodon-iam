package io.choerodon.iam.api.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.PermissionService;
import org.hzero.iam.domain.repository.PermissionRepository;
import org.hzero.iam.domain.vo.PermissionVO;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.base.api.dto.CheckPermissionDTO;
import io.choerodon.base.app.service.PermissionC7nService;
import io.choerodon.base.infra.dto.PermissionDTO;
import io.choerodon.core.domain.Page;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/v1/permissions")
public class PermissionC7nController {

    private PermissionService permissionService;
    private PermissionRepository permissionRepository;
    private PermissionC7nService permissionC7nService;

    public PermissionC7nController(PermissionService permissionService,
                                   PermissionC7nService permissionC7nService,
                                   PermissionRepository permissionRepository) {
        this.permissionService = permissionService;
        this.permissionC7nService = permissionC7nService;
        this.permissionRepository = permissionRepository;
    }

    @PostMapping(value = "/checkPermission")
    @ApiOperation("通过permission code鉴权，判断用户是否有查看的权限")
    @Permission(permissionLogin = true)
    public ResponseEntity<List<CheckPermissionDTO>> checkPermission(@RequestBody List<CheckPermissionDTO> checkPermissions) {
        return new ResponseEntity<>(permissionService.checkPermission(checkPermissions), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("通过层级查询权限列表")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<PermissionVO>> pagingQuery(@ApiIgnore
                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                          @RequestParam("level") String level,
                                                          @RequestParam(required = false) String param) {
        return Results.success(permissionRepository.pagePermission(param, level, pageRequest));
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation("通过角色查询权限列表")
    @PostMapping
    public ResponseEntity<Set<PermissionDTO>> queryByRoleIds(@RequestBody List<Long> roleIds) {
        return new ResponseEntity<>(permissionService.queryByRoleIds(roleIds), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("组织层通过角色查询权限列表")
    @PostMapping("/through_roles_at_org/{organization_id}")
    public ResponseEntity<Set<PermissionDTO>> queryByRoleIdsAtOrg(@PathVariable(name = "organization_id") Long organizationId, @RequestBody List<Long> roleIds) {
        return new ResponseEntity<>(permissionService.queryByRoleIds(roleIds), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("通过层级，服务名，code查询Permission列表")
    @GetMapping("/permissionList")
    public ResponseEntity<List<org.hzero.iam.domain.entity.Permission>> query(@RequestParam("level") String level,
                                                                              @RequestParam(value = "service_name", required = false) String serviceName,
                                                                              @RequestParam(value = "code", required = false) String code) {
        return new ResponseEntity<>(permissionC7nService.query(level, serviceName, code), HttpStatus.OK);
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
        SecurityTokenHelper.validToken(permissions);
        permissionService.deleteApis(permissions);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
