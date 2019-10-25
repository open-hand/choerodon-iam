package io.choerodon.base.api.controller.v1;

import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.DomainService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;


/**
 * @author superlee
 * @since 2019-06-19
 */
@RestController
@RequestMapping(value = "/v1/domain")
public class DomainController extends BaseController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    @PostMapping(value = "/check")
    @Permission(type = ResourceType.SITE, permissionPublic = true)
    @ApiOperation(value = "检查域名是否有效")
    public ResponseEntity<Boolean> check(@RequestBody Map<String, String> map) {
        return ResponseEntity.ok(domainService.check(map.get("url")));
    }

}
