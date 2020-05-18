package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.exception.NotLoginException;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<Page<RoleC7nDTO>> listSelfRole(@PathVariable("organizationId") Long organizationId,
                                                         @ApiIgnore
                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return Results.success(roleC7nService.listRole(organizationId,
                Optional.ofNullable(DetailsHelper.getUserDetails()).orElseThrow(NotLoginException::new).getUserId(),
                pageRequest));
    }
}
