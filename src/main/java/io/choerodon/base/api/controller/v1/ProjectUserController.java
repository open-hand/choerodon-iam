package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.base.BaseController;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

import java.util.List;


@RestController
@RequestMapping(value = "/v1/projects")
public class ProjectUserController extends BaseController {

    private UserService userService;

    public ProjectUserController(UserService userService) {
        this.userService = userService;
    }


    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）")
    @GetMapping(value = "/{project_id}/users/search")
    @CustomPageRequest
    public ResponseEntity<PageInfo<UserDTO>> pagingQueryUsersWithRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                                     @ApiIgnore
                                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                     @RequestParam(required = false) String loginName,
                                                                                     @RequestParam(required = false) String realName,
                                                                                     @RequestParam(required = false) String roleName,
                                                                                     @RequestParam(required = false) Boolean enabled,
                                                                                     @RequestParam(required = false) String params) {
        return new ResponseEntity<>(userService.pagingQueryUsersWithRolesOnProjectLevel(projectId, Pageable, loginName, realName, roleName,
                enabled, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询项目下指定角色的用户列表")
    @GetMapping(value = "/{project_id}/users/{role_lable}")
    public ResponseEntity<List<UserDTO>> listProjectUsersByProjectIdAndRoleLable(@PathVariable("project_id") Long projectId,
                                                                                 @PathVariable("role_lable") String roleLable) {
        return ResponseEntity.ok(userService.listProjectUsersByProjectIdAndRoleLable(projectId, roleLable));
    }

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询项目下的用户列表(根据登录名或真实名称搜索)")
    @GetMapping(value = "/{project_id}/users/search_by_name")
    public ResponseEntity<List<UserDTO>> listUsersByName(@PathVariable(name = "project_id") Long projectId,
                                                         @RequestParam(required = false) String param) {
        return ResponseEntity.ok(userService.listUsersByName(projectId, param));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation("根据项目id查询项目下的项目所有者")
    @GetMapping("/{project_id}/owner/list")
    public ResponseEntity<List<UserDTO>> listProjectOwnerById(@PathVariable(name = "project_id") Long projectId) {
        return ResponseEntity.ok(userService.listProjectOwnerById(projectId));
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询项目下的用户列表，根据真实名称或登录名搜索(限制20个)")
    @GetMapping(value = "/{project_id}/users/search_by_name/with_limit")
    public ResponseEntity<List<UserDTO>> listUsersByNameWithLimit(@PathVariable(name = "project_id") Long projectId,
                                                                  @RequestParam(name = "param", required = false) String param) {
        return ResponseEntity.ok(userService.listUsersByNameWithLimit(projectId, param));
    }
}
