package io.choerodon.base.api.controller.v1;

import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/v1/organization")
public class OrganizationRetryController {
    private OperateLogService operateLogService;

    public OrganizationRetryController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation("组织层重试事务实例")
    @PutMapping("/{id}/org/retry")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    public void orgRetry(
            @PathVariable(value = "source_id") Long sourceId,
            @PathVariable(value = "id") long id) {
        operateLogService.orgRetry(sourceId, id);
    }

}
