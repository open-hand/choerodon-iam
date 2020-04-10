package io.choerodon.iam.api.controller.v1;

import java.util.Map;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.base.app.service.DomainC7nService;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;


/**
 * @author superlee
 * @since 2019-06-19
 */
@RestController
@RequestMapping(value = "/v1/domain")
public class DomainC7nController extends BaseController {

    private final DomainC7nService domainC7nService;

    public DomainC7nController(DomainC7nService domainC7nService) {
        this.domainC7nService = domainC7nService;
    }

    @PostMapping(value = "/check")
    @Permission(level = ResourceLevel.SITE, permissionPublic = true)
    @ApiOperation(value = "检查域名是否有效")
    public ResponseEntity<Boolean> check(@RequestBody Map<String, String> map) {
        return ResponseEntity.ok(domainC7nService.check(map.get("url")));
    }

}
