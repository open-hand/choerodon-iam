package io.choerodon.base.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.base.app.service.InstanceService;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;

/**
 * @author flyleft
 */
@RestController
@RequestMapping(value = "/v1/instances")
public class InstanceController {

    private InstanceService instanceService;

    @Autowired
    public InstanceController(InstanceService instanceService) {
        this.instanceService = instanceService;
    }


    /**
     * 查询服务列表
     *
     * @return 服务列表
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation("查询服务列表")
    @GetMapping
    public ResponseEntity<List<String>> listAll() {
        return new ResponseEntity<>(instanceService.listAll(), HttpStatus.OK);
    }

}
