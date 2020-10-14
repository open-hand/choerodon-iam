package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.TenantDTO;
import org.hzero.iam.api.dto.UserPasswordDTO;
import org.hzero.iam.domain.vo.UserVO;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/5/11
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_USER_SELF)
@RestController
@RequestMapping(value = "/choerodon/v1")
public class UserSelfC7nController extends BaseController {
    @Autowired
    private TenantC7nService tenantC7nService;

    @Autowired
    private UserC7nService userC7nService;


    @ApiOperation("登录用户 - 查询可访问的租户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantNum", value = "租户编号", paramType = "query"),
            @ApiImplicitParam(name = "tenantName", value = "租户名称", paramType = "query")
    })
    @Permission(permissionLogin = true)
    @GetMapping("/users/self-tenants")
    public ResponseEntity<List<TenantVO>> selfTenant(String tenantNum, String tenantName) {
        TenantDTO params = new TenantDTO();
        params.setTenantNum(tenantNum);
        params.setTenantName(tenantName);
        return new ResponseEntity<>(tenantC7nService.selectSelfTenants(params), HttpStatus.OK);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "登录用户 - 查询自身基础信息")
    @GetMapping(value = "/users/self")
    public ResponseEntity<UserVO> selectSelf() {
        return Results.success(userC7nService.selectSelf());
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "个人信息页面 - 查询个人信息")
    @GetMapping(value = "/users/personal")
    public ResponseEntity<UserDTO> queryPersonalInfo() {
        return Results.success(userC7nService.queryPersonalInfo());
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "修改密码")
    @PutMapping(value = "/users/{id}/password")
    public ResponseEntity<Void> selfUpdatePassword(@Encrypt @PathVariable Long id,
                                                   @RequestBody UserPasswordDTO userPasswordDTO) {
        userC7nService.selfUpdatePassword(id, userPasswordDTO, true, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/switch/site")
    @Permission(permissionLogin = true)
    @ApiOperation(value = "组织切换到平台")
    public ResponseEntity<Void> switchSite() {
        userC7nService.switchSite();
        return ResponseEntity.noContent().build();
    }
}
