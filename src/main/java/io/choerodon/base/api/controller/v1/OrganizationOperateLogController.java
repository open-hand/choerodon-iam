package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/v1/organization/{source_id}")
public class OrganizationOperateLogController {
    private OperateLogService operateLogService;

    public OrganizationOperateLogController(OperateLogService operateLogService) {
        this.operateLogService = operateLogService;
    }

    @GetMapping("/operate/log")
    @ApiOperation("加载更多操作记录")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @CustomPageRequest
    public ResponseEntity<PageInfo<OperateLogVO>> listMoreOperateLog(
            @PathVariable(value = "source_id") Long sourceId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(operateLogService.listOperateLog(pageable, sourceId), HttpStatus.OK);
    }

}
