package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.OperateLogService;
import io.choerodon.swagger.annotation.Permission;
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
@RequestMapping("/choerodon/v1/site/{source_id}")
public class SiteRetryController {
    private OperateLogService operateLogService;

    public SiteRetryController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation("平台层重试事务实例")
    @PutMapping("/{id}/site/retry")
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public void siteRetry(
            @PathVariable(value = "source_id") Long sourceId,
            @PathVariable(value = "id") long id) {
        operateLogService.siteRetry(sourceId, id);
    }
}
