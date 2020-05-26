package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.OperateLogVO;
import io.choerodon.iam.app.service.OperateLogService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/choerodon/v1/site/{source_id}")
public class SiteOperateLogController {
    private OperateLogService operateLogService;


    public SiteOperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @GetMapping("/operate/log")
    @ApiOperation("加载更多操作记录")
    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    public ResponseEntity<Page<OperateLogVO>> listMoreOperateLog(
            @PathVariable(value = "source_id") Long sourceId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return new ResponseEntity<>(operateLogService.listOperateLog(pageRequest, sourceId), HttpStatus.OK);
    }

}
