package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.WorkGroupTreeVO;
import io.choerodon.iam.api.vo.WorkGroupUserRelParamVO;
import io.choerodon.iam.app.service.WorkGroupService;
import io.choerodon.iam.app.service.WorkGroupUserRelService;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author zhaotianxin
 * @date 2021/11/26 11:11
 */
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/work_bench")
public class WorkBenchBusinessController {

    @Autowired
    private WorkGroupUserRelService workGroupUserRelService;

    @Autowired
    private WorkGroupService workGroupService;

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("查询按工作组筛选的成员")
    @PostMapping("/work_group_user_rel/page_by_groups")
    public ResponseEntity<Page<UserDTO>> pageByGroups(@ApiParam(value = "组织Id", required = true)
                                                      @PathVariable(name = "organization_id") Long organizationId,
                                                      @ApiParam(value = "分页信息", required = true)
                                                              PageRequest pageRequest,
                                                      @ApiParam(value = "查询参数", required = true)
                                                      @RequestBody WorkGroupUserRelParamVO workGroupUserRelParamVO) {
        return Optional.ofNullable(workGroupUserRelService.pageByGroups(organizationId, pageRequest, workGroupUserRelParamVO))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.user.query.by.groups"));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("查询工作组树形结构")
    @GetMapping(value = "/work_group/query_tree")
    public ResponseEntity<WorkGroupTreeVO> pageWorkHoursLogByProjectIds(@ApiParam(value = "组织Id", required = true)
                                                                        @PathVariable(name = "organization_id") Long organizationId,
                                                                        @RequestParam(name = "with_extra_items", required = false, defaultValue = "true") boolean withExtraItems) {
        return Optional.ofNullable(workGroupService.queryWorkGroupTree(organizationId, withExtraItems))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.work.group.tree.get"));
    }

}
