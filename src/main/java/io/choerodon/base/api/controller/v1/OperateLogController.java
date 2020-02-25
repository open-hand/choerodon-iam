package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.OrgAdministratorVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.dto.OperateLogDTO;
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
@RequestMapping("/v1/operatelog/{organization_id}")
public class OperateLogController {
    @Autowired
    private OperateLogService operateLogService;

    @GetMapping("/new/operate/log")
    @ApiOperation("组织概览最新的操作记录")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    public ResponseEntity<List<OperateLogDTO>> listNewOperateLod(
            @PathVariable(value = "organization_id") Long organizationId) {
        return new ResponseEntity<>(operateLogService.listNewOperateLod(organizationId), HttpStatus.OK);
    }
}
