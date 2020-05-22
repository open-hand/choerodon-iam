package io.choerodon.iam.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = C7nSwaggerApiConfig.CHOERODON_ORGANIZATION_ADMIN)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}")
public class OrganizationAdminC7nController {

    @Autowired
    private UserC7nService userC7nService;

    @PostMapping("/org_administrator")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "添加组织管理员角色")
    public ResponseEntity<Void> createOrgAdministrator(@PathVariable(name = "organization_id") Long organizationId,
                                                       @RequestParam(name = "id") List<Long> userIds) {
        userC7nService.createOrgAdministrator(userIds, organizationId);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/org_administrator/{id}")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除该User在本组织的组织管理员角色")
    public ResponseEntity<Void> deleteOrgAdministrator(@PathVariable(name = "organization_id") Long organizationId,
                                                       @PathVariable(name = "id") Long userId) {
        userC7nService.deleteOrgAdministrator(organizationId, userId);
        return ResponseEntity.noContent().build();

    }
}
