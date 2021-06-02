package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.UserPermissionVO;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

@RestController
@RequestMapping("/choerodon/v1")
public class RoleC7nController {


    @Autowired
    private RoleC7nService roleC7nService;

    //
    // 角色查询
    // ------------------------------------------------------------------------------
    @ApiOperation("角色查询 - 查询当前用户自己的角色")
    @Permission(permissionLogin = true)
    @GetMapping("/{organizationId}/roles/self/roles")
    public ResponseEntity<Page<UserPermissionVO>> listSelfRole(@ApiIgnore
                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                               @PathVariable("organizationId") Long organizationId,
                                                               @RequestParam(required = false) String name,
                                                               @RequestParam(required = false) String level,
                                                               @RequestParam(required = false) String params) {
        return Results.success(roleC7nService.listRole(pageRequest, organizationId, name, level, params));
    }

    /**
     * 分页查询角色
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询角色")
    @GetMapping(value = "/roles/search")
    @CustomPageRequest
    public ResponseEntity<Page<io.choerodon.iam.api.vo.RoleVO>> pagedSearch(@ApiIgnore
                                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                            @RequestParam(required = false) String name,
                                                                            @RequestParam(required = false) String code,
                                                                            @RequestParam(required = false) String roleLevel,
                                                                            @RequestParam(value = "tenantId") Long tenantId,
                                                                            @RequestParam(required = false) Boolean builtIn,
                                                                            @RequestParam(required = false) Boolean enabled,
                                                                            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(roleC7nService.pagingSearch(pageRequest, tenantId, name, code, roleLevel, builtIn, enabled, params), HttpStatus.OK);
    }

    /**
     * 根据标签查询角色
     *
     * @param tenantId  组织id
     * @param labelName 标签名称
     * @return LdapAutoDTO对象
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据标签查询角色")
    @GetMapping(value = "/roles/search_by_label")
    public ResponseEntity<List<Role>> listByLabelName(@RequestParam(value = "tenantId") Long tenantId,
                                                      @RequestParam(value = "labelName") String labelName) {
        return ResponseEntity.ok(roleC7nService.listByLabelNames(tenantId, labelName));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据code查询平台层角色")
    @GetMapping(value = "/roles/site/search_by_code")
    public ResponseEntity<Role> getSiteRoleByCode(@RequestParam(value = "code") String code) {
        return ResponseEntity.ok(roleC7nService.getSiteRoleByCode(code));
    }

    //
    // 查询所有平台维护用户
    // ------------------------------------------------------------------------------
    @ApiOperation("查询所有平台维护用户")
    @Permission(permissionWithin = true)
    @GetMapping("/list_vindicators")
    public ResponseEntity<List<User>> listVindicators() {
        return Results.success(roleC7nService.listVindicators());
    }

}
