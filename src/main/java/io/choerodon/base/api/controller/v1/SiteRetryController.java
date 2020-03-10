package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/v1/site/{source_id}")
public class SiteRetryController {
    private OperateLogService operateLogService;

    public SiteRetryController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @ApiOperation("平台层重试事务实例")
    @PutMapping("/{id}/site/retry")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    public void siteRetry(
            @PathVariable(value = "source_id") Long sourceId,
            @PathVariable(value = "id") long id) {
        operateLogService.siteRetry(sourceId, id);
    }
}
