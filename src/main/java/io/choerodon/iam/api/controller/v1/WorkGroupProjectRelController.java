package io.choerodon.iam.api.controller.v1;

import io.choerodon.iam.app.service.WorkGroupProjectRelService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/work_group_project_rel")
public class WorkGroupProjectRelController {
    @Autowired
    private WorkGroupProjectRelService workGroupProjectRelService;

    @ApiModelProperty("查出与指定项目相关联的所有工作组信息")
    @PostMapping("/list_work_group_by_project_ids")
    @Permission(permissionWithin = true)
    public ResponseEntity<Map<Long, String>> listByProjectIds(@ApiParam(value = "组织Id", required = true)
                                                              @PathVariable(name = "organization_id") Long organizationId,
                                                              @RequestBody Set<Long> projectIds) {
        return Results.success(workGroupProjectRelService.listByProjectIds(organizationId, projectIds));
    }

    @ApiOperation("查出与指定工作组有关联的所有项目id")
    @PostMapping("/list_related_project_ids")
    @Permission(permissionWithin = true)
    public ResponseEntity<List<Long>> listProjectIdsByWorkGroupId(@RequestBody List<Long> workGroupIds) {
        return Results.success(workGroupProjectRelService.listProjectIdsByWorkId(workGroupIds));
    }
}
