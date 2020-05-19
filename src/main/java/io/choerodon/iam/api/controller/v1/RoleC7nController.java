package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

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
    public ResponseEntity<Page<RoleC7nDTO>> listSelfRole(@ApiIgnore
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
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页查询角色")
    @GetMapping(value = "/search")
    @CustomPageRequest
    public ResponseEntity<Page<RoleDTO>> pagedSearch(@ApiIgnore
                                                  @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                     @RequestParam(required = false) String name,
                                                     @RequestParam(required = false) String code,
                                                     @RequestParam(required = false) String level,
                                                     @RequestParam(required = true) Long tenantId,
                                                     @RequestParam(required = false) Boolean builtIn,
                                                     @RequestParam(required = false) Boolean enabled,
                                                     @RequestParam(required = false) String params) {
        return new ResponseEntity<>(roleC7nService.pagingSearch(pageRequest,tenantId, name, code, level, builtIn, enabled, params), HttpStatus.OK);
    }

}
