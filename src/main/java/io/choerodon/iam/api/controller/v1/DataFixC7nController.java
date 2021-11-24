package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

@Api(tags = C7nSwaggerApiConfig.CHOERODON_DATA_FIX)
@RestController
@RequestMapping(value = "/choerodon/v1/fix")
public class DataFixC7nController {
    @Autowired
    private TenantC7nService tenantC7nService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "同步组织多语言")
    @GetMapping("/tenant_tl")
    public ResponseEntity<Void> syncTenantTl() {
        tenantC7nService.syncTenantTl();
        return Results.success();
    }
}