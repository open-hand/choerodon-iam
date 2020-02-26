package io.choerodon.base.api.controller.v1;

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@RestController
@RequestMapping("/v1/operatelog/{source_id}")
public class OperateLogController {
    @Autowired
    private OperateLogService operateLogService;

    @GetMapping("/new/operate/log")
    @ApiOperation("概览8条最新的操作记录")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<List<OperateLogVO>> listNewOperateLog(
            @PathVariable(value = "source_id") Long sourceId) {
        return new ResponseEntity<>(operateLogService.listNewOperateLog(sourceId), HttpStatus.OK);
    }

    @GetMapping("/more/operate/log")
    @ApiOperation("加载更多100条操作记录")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.SITE_ADMINISTRATOR})
    public ResponseEntity<List<OperateLogVO>> listMoreOperateLog(
            @PathVariable(value = "source_id") Long sourceId) {
        return new ResponseEntity<>(operateLogService.listMoreOperateLog(sourceId), HttpStatus.OK);
    }
}
