package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OperateLogService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/choerodon/v1/organization/{source_id}")
public class OrganizationRetryController {
    private OperateLogService operateLogService;

    public OrganizationRetryController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation("组织层重试事务实例")
    @PutMapping("/{id}/org/retry")
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    public void orgRetry(
            @Encrypt @PathVariable(value = "source_id") Long sourceId,
            @Encrypt @PathVariable(value = "id") long id) {
        operateLogService.orgRetry(sourceId, id);
    }

}
