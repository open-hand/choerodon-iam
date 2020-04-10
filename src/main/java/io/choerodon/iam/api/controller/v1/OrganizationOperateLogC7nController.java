package io.choerodon.iam.api.controller.v1;

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

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogC7nService;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/v1")
public class OrganizationOperateLogC7nController {
    private OperateLogC7nService operateLogC7nService;

    public OrganizationOperateLogC7nController(OperateLogC7nService operateLogC7nService) {
        this.operateLogC7nService = operateLogC7nService;
    }

    @GetMapping("/organization/{source_id}/operate/log")
    @ApiOperation("加载更多操作记录")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @CustomPageRequest
    public ResponseEntity<Page<OperateLogVO>> listMoreOperateLog(
            @PathVariable(value = "source_id") Long sourceId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return new ResponseEntity<>(operateLogC7nService.listOperateLog(pageRequest, sourceId), HttpStatus.OK);
    }

    @GetMapping("/site/{source_id}/operate/log")
    @ApiOperation("加载更多操作记录")
    @Permission(level = ResourceLevel.SITE)
    @CustomPageRequest
    public ResponseEntity<Page<OperateLogVO>> listMoreOperateLogSite(
            @PathVariable(value = "source_id") Long sourceId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return new ResponseEntity<>(operateLogC7nService.listOperateLog(pageRequest, sourceId), HttpStatus.OK);
    }
}
