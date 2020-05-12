package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.iam.api.dto.TenantDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.app.service.OrganizationService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * User: Mr.Wang
 * Date: 2020/5/11
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_USER_SELF)
@RestController
@RequestMapping(value = "/choerodon/v1")
public class UserSelfC7nController extends BaseController {
    @Autowired
    private OrganizationService organizationService;


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
        return new ResponseEntity<>(organizationService.selectSelfTenants(params), HttpStatus.OK);
    }
}
