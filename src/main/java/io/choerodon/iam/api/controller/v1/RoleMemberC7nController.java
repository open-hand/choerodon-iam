package io.choerodon.iam.api.controller.v1;

import java.util.List;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.iam.api.dto.RoleDTO;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.agile.RoleVO;
import io.choerodon.iam.app.service.ProjectUserService;
import io.choerodon.iam.app.service.RoleC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;


/**
 * @author superlee
 * @author wuguokai
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_ROLE_MEMBER)
@RestController
@RequestMapping(value = "/choerodon/v1")
public class RoleMemberC7nController extends BaseController {

    private RoleC7nService roleC7nService;
    private ProjectUserService projectUserService;

    public RoleMemberC7nController(RoleC7nService roleC7nService, ProjectUserService projectUserService) {
        this.roleC7nService = roleC7nService;
        this.projectUserService = projectUserService;
    }

    /**
     * 查询project层角色,附带该角色下分配的用户数
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目层查询角色列表以及该角色下的用户数量")
    @PostMapping(value = "/projects/{project_id}/role_members/users/count")
    public ResponseEntity<List<RoleVO>> listRolesWithUserCountOnProjectLevel(
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return ResponseEntity.ok(roleC7nService.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchDTO));
    }

    /** 项目层分页查询角色下的用户
     * @param roleId
     * @param projectId
     * @param roleAssignmentSearchDTO
     * @param doPage                  是否分页，如果为false，则不分页
     * @return
     */
    @Permission(level = ResourceLevel.PROJECT)
    @ApiOperation(value = "项目层分页查询角色下的用户")
    @CustomPageRequest
    @PostMapping(value = "/projects/{project_id}/role_members/users")
    public ResponseEntity<Page<UserDTO>> pagingQueryUsersByRoleIdOnProjectLevel(
            @PathVariable(name = "project_id") Long projectId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @RequestParam(name = "role_id") Long roleId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO,
            @RequestParam(defaultValue = "true") boolean doPage) {
        return ResponseEntity.ok(projectUserService.pagingQueryUsersByRoleIdOnProjectLevel(
                pageRequest, roleAssignmentSearchDTO, roleId, projectId, doPage));
    }

    /**
     * 查询用户在项目下拥有的角色
     */
    @Permission(level = ResourceLevel.PROJECT, permissionLogin = true)
    @ApiOperation(value = "查询用户在项目下拥有的角色")
    @GetMapping(value = "/projects/{project_id}/role_members/users/{user_id}")
    public ResponseEntity<List<RoleDTO>> getUserRolesByUserIdAndProjectId(@PathVariable(name = "project_id") Long projectId,
                                                                           @PathVariable(name = "user_id") Long userId) {
        return ResponseEntity.ok(projectUserService.listRolesByProjectIdAndUserId(projectId, userId));
    }
}
