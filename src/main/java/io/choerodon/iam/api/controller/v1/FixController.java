package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.FixService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/choerodon/v1/fix")
public class FixController {
    @Autowired
    private FixService fixService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建ldap自动同步")
    @GetMapping("/project-category")
    public ResponseEntity<Void> fixProjectCateGory() {
        fixService.fixProjectCateGory();
        return Results.success();
    }

}
