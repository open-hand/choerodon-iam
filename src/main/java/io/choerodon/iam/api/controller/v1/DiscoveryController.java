package io.choerodon.iam.api.controller.v1;

import java.util.Set;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.DiscoveryService;
import io.choerodon.swagger.annotation.Permission;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/1/13
 * @Modified By:
 */
@RestController
@RequestMapping(value = "/choerodon/v1")
public class DiscoveryController {
    @Autowired
    private DiscoveryService domainC7nService;

    @GetMapping(value = "/servers")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation(value = "获取已经部署的所有模块")
    public ResponseEntity<Set<String>> listServices() {
        return Results.success(domainC7nService.getServiceInstance());
    }
}
