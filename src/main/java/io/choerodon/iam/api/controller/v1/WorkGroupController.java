package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.MoveWorkGroupVO;
import io.choerodon.iam.api.vo.WorkGroupTreeVO;
import io.choerodon.iam.api.vo.WorkGroupVO;
import io.choerodon.iam.app.service.WorkGroupService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * @author zhaotianxin
 * @date 2021-11-08 19:29
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_WORK_GROUP)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/work_group")
public class WorkGroupController {
    @Autowired
    private WorkGroupService workGroupService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询工作组树形结构")
    @GetMapping(value = "/query_tree")
    public ResponseEntity<WorkGroupTreeVO> pageWorkHoursLogByProjectIds(@ApiParam(value = "组织Id", required = true)
                                                                        @PathVariable(name = "organization_id") Long organizationId) {
        return Optional.ofNullable(workGroupService.queryWorkGroupTree(organizationId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.tree.get"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询工作组详情")
    @GetMapping(value = "/{work_group_id}")
    public ResponseEntity<WorkGroupVO> queryById(@ApiParam(value = "组织Id", required = true)
                                                 @PathVariable(name = "organization_id") Long organizationId,
                                                 @ApiParam(value = "工作组id", required = true)
                                                 @PathVariable(name = "work_group_id") Long workGroupId) {
        return Optional.ofNullable(workGroupService.queryById(organizationId, workGroupId))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.query"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("创建工作组")
    @PostMapping
    public ResponseEntity<WorkGroupVO> create(@ApiParam(value = "组织Id", required = true)
                                              @PathVariable(name = "organization_id") Long organizationId,
                                              @ApiParam(value = "工作组创建vo", required = true)
                                              @RequestBody WorkGroupVO workGroupVO) {
        return Optional.ofNullable(workGroupService.create(organizationId, workGroupVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.work.group.create"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("更改工作组")
    @PutMapping
    public ResponseEntity<WorkGroupVO> update(@ApiParam(value = "组织Id", required = true)
                                              @PathVariable(name = "organization_id") Long organizationId,
                                              @ApiParam(value = "工作组更新vo", required = true)
                                              @RequestBody WorkGroupVO workGroupVO) {
        return Optional.ofNullable(workGroupService.update(organizationId, workGroupVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.work.group.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("删除工作组")
    @DeleteMapping("/{work_group_id}")
    public ResponseEntity delete(@ApiParam(value = "组织Id", required = true)
                                 @PathVariable(name = "organization_id") Long organizationId,
                                 @ApiParam(value = "工作组id", required = true)
                                 @PathVariable(name = "work_group_id") @Encrypt Long workGroupId) {
        workGroupService.delete(organizationId, workGroupId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("调整工作组顺序")
    @PostMapping("/move")
    public ResponseEntity<WorkGroupVO> move(@ApiParam(value = "组织Id", required = true)
                                            @PathVariable(name = "organization_id") Long organizationId,
                                            @ApiParam(value = "父级id", required = true)
                                            @RequestParam @Encrypt(ignoreValue = {"0"}) Long parentId,
                                            @ApiParam(value = "工作组移动vo", required = true)
                                            @RequestBody MoveWorkGroupVO moveWorkGroupVO) {
        return Optional.ofNullable(workGroupService.moveWorkGroup(organizationId, parentId, moveWorkGroupVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.CREATED))
                .orElseThrow(() -> new CommonException("error.work.group.update"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("校验名称")
    @GetMapping("/check_name")
    public ResponseEntity<Boolean> checkName(@ApiParam(value = "组织Id", required = true)
                                             @PathVariable(name = "organization_id") Long organizationId,
                                             @ApiParam(value = "父级id", required = true)
                                             @RequestParam @Encrypt(ignoreValue = {"0"}) Long parentId,
                                             @ApiParam(value = "名称", required = true)
                                             @RequestParam String name) {
        return new ResponseEntity<>(workGroupService.checkName(organizationId, parentId, name), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiModelProperty("查询用户的工作组")
    @PostMapping("/list_by_user_ids")
    public ResponseEntity<List<WorkGroupVO>> listWorkGroupByUserIds(@ApiParam(value = "组织Id", required = true)
                                                                    @PathVariable(name = "organization_id") Long organizationId,
                                                                    @ApiParam(value = "用户id集合", required = true)
                                                                    @RequestBody List<Long> userIds) {
        return new ResponseEntity<>(workGroupService.listWorkGroupByUserIds(organizationId, userIds), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiModelProperty("查询组织下所有工作组")
    @GetMapping("/list")
    public ResponseEntity<List<WorkGroupVO>> listWorkGroups(@ApiParam(value = "组织Id", required = true)
                                                            @PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(workGroupService.listWorkGroups(organizationId), HttpStatus.OK);
    }
}
