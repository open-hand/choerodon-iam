package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.api.vo.agile.MigrateWorkGroupDataVO;
import io.choerodon.iam.app.service.WorkGroupMigrateService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author superlee
 * @since 2022-07-21
 */

@Api(tags = C7nSwaggerApiConfig.CHOERODON_WORK_GROUP)
@RestController
@RequestMapping(value = "/choerodon/v1/work_group/migrate")
public class WorkGroupMigrateController {

    @Autowired
    private WorkGroupMigrateService workGroupMigrateService;

    @Permission(permissionWithin = true)
    @ApiOperation("迁移敏捷工作组数据")
    @PostMapping
    public ResponseEntity migrate(@RequestBody MigrateWorkGroupDataVO migrateWorkGroupDataVO) {
        workGroupMigrateService.migrate(migrateWorkGroupDataVO);
        return new ResponseEntity(HttpStatus.OK);
    }

}
