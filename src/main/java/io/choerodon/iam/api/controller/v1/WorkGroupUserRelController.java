package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.WorkGroupUserRelParamVO;
import io.choerodon.iam.api.vo.WorkGroupUserRelVO;
import io.choerodon.iam.api.vo.WorkHoursSearchVO;
import io.choerodon.iam.app.service.WorkGroupUserRelService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;

/**
 * @author zhaotianxin
 * @date 2021-11-08 19:29
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/work_group_user_rel")
public class WorkGroupUserRelController {
    @Autowired
    private WorkGroupUserRelService workGroupUserRelService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("批量加入成员")
    @PostMapping("/batch_insert")
    public ResponseEntity batchInsert(@ApiParam(value = "组织Id", required = true)
                                      @PathVariable(name = "organization_id") Long organizationId,
                                      @ApiParam(value = "工作组用户关联vo", required = true)
                                      @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        workGroupUserRelService.batchInsertRel(organizationId, workGroupUserRelParamVO);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("批量移除成员")
    @PostMapping("/batch_delete")
    public ResponseEntity batchDelete(@ApiParam(value = "组织Id", required = true)
                                      @PathVariable(name = "organization_id") Long organizationId,
                                      @ApiParam(value = "工作组用户关联vo", required = true)
                                      @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        workGroupUserRelService.batchDeleteRel(organizationId, workGroupUserRelParamVO);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询工作组下面关联的成员")
    @PostMapping("/page")
    public ResponseEntity<Page<WorkGroupUserRelVO>> pageByQuery(@ApiParam(value = "组织Id", required = true)
                                                                @PathVariable(name = "organization_id") Long organizationId,
                                                                @ApiParam(value = "分页信息", required = true)
                                                                PageRequest pageRequest,
                                                                @ApiParam(value = "查询参数", required = true)
                                                                @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        return Optional.ofNullable(workGroupUserRelService.pageByQuery(organizationId, pageRequest, workGroupUserRelParamVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.user.rel.query"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询工作组未关联的成员")
    @PostMapping("/unlink")
    public ResponseEntity<Page<WorkGroupUserRelVO>> pageUnlinkUser(@ApiParam(value = "组织Id", required = true)
                                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                                   @ApiParam(value = "分页信息", required = true)
                                                                   PageRequest pageRequest,
                                                                   @ApiParam(value = "查询参数", required = true)
                                                                   @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        return Optional.ofNullable(workGroupUserRelService.pageUnlinkUser(organizationId, pageRequest, workGroupUserRelParamVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.user.rel.query"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询未关联工作组的成员")
    @PostMapping("/page_unassignee")
    public ResponseEntity<Page<WorkGroupUserRelVO>> pageUnAssignee(@ApiParam(value = "组织Id", required = true)
                                                                   @PathVariable(name = "organization_id") Long organizationId,
                                                                   @ApiParam(value = "分页信息", required = true)
                                                                   PageRequest pageRequest,
                                                                   @ApiParam(value = "查询参数", required = true)
                                                                   @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        return Optional.ofNullable(workGroupUserRelService.pageUnAssignee(organizationId, pageRequest, workGroupUserRelParamVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.user.rel.query"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiModelProperty("根据工作组id查用户id(敏捷用)")
    @PostMapping("/list_user_ids")
    public ResponseEntity<Set<Long>> listUserIdsByWorkGroupIds(@ApiParam(value = "组织Id", required = true)
                                                               @PathVariable(name = "organization_id") Long organizationId,
                                                               @ApiParam(value = "用户id集合", required = true)
                                                               @RequestBody WorkHoursSearchVO workHoursSearchVO) {
        return new ResponseEntity<>(workGroupUserRelService.listUserIdsByWorkGroupIds(organizationId, workHoursSearchVO), HttpStatus.OK);
    }
}
