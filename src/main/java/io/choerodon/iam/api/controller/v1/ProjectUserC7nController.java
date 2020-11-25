package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.OnlineUserStatistics;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.ProjectPermissionService;
import io.choerodon.iam.app.service.RoleMemberService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectPermissionDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.dto.UserWithGitlabIdDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Api(tags = C7nSwaggerApiConfig.CHOERODON_PROJECT_USER)
@RestController
@RequestMapping(value = "/choerodon/v1/projects")
public class ProjectUserC7nController extends BaseController {

    private final ProjectPermissionService projectPermissionService;
    private final OrganizationResourceLimitService organizationResourceLimitService;
    private final RoleMemberService roleMemberService;

    private final ProjectC7nService projectC7nService;

    public ProjectUserC7nController(ProjectPermissionService projectPermissionService,
                                    RoleMemberService roleMemberService,
                                    OrganizationResourceLimitService organizationResourceLimitService,
                                    ProjectC7nService projectC7nService) {
        this.projectPermissionService = projectPermissionService;
        this.roleMemberService = roleMemberService;
        this.organizationResourceLimitService = organizationResourceLimitService;
        this.projectC7nService = projectC7nService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）")
    @GetMapping(value = "/{project_id}/users/search")
    @CustomPageRequest
    public ResponseEntity<Page<UserDTO>> pagingQueryUsersWithRolesOnProjectLevel(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(name = "project_id") Long projectId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @ApiParam(value = "登录名")
            @RequestParam(required = false) String loginName,
            @ApiParam(value = "用户名")
            @RequestParam(required = false) String realName,
            @ApiParam(value = "角色名")
            @RequestParam(required = false) String roleName,
            @ApiParam(value = "是否启用")
            @RequestParam(required = false) Boolean enabled,
            @ApiParam(value = "查询参数")
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(projectPermissionService.pagingQueryUsersWithRolesOnProjectLevel(projectId, pageRequest, loginName, realName, roleName,
                enabled, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询用户列表（包括用户信息以及所分配的项目角色信息）排除自己")
    @GetMapping(value = "/{project_id}/users/search/list")
    public ResponseEntity<List<UserDTO>> listUsersWithRolesOnProjectLevel(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(name = "project_id") Long projectId,
            @ApiParam(value = "登录名")
            @RequestParam(required = false) String loginName,
            @ApiParam(value = "用户名")
            @RequestParam(required = false) String realName,
            @ApiParam(value = "角色名")
            @RequestParam(required = false) String roleName,
            @ApiParam(value = "查询参数")
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(projectPermissionService.listUsersWithRolesOnProjectLevel(projectId, loginName, realName, roleName, params), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据多个id查询用户（包括用户信息以及所分配的项目角色信息以及GitlabUserId）")
    @PostMapping(value = "/{project_id}/users/list_by_ids")
    public ResponseEntity<List<UserWithGitlabIdDTO>> listUsersWithRolesAndGitlabUserIdByIds(
            @ApiParam(value = "项目id", required = true)
            @PathVariable(name = "project_id") Long projectId,
            @ApiParam(value = "多个用户id", required = true)
            @Encrypt @RequestBody Set<Long> userIds) {
        return new ResponseEntity<>(projectPermissionService.listUsersWithRolesAndGitlabUserIdByIdsInProject(projectId, userIds), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目下指定角色的用户列表")
    @GetMapping(value = "/{project_id}/users/{role_lable}")
    public ResponseEntity<List<UserDTO>> listProjectUsersByProjectIdAndRoleLable(
            @ApiParam(value = "项目id", required = true)
            @PathVariable("project_id") Long projectId,
            @ApiParam(value = "角色标签", required = true)
            @PathVariable("role_lable") String roleLable) {
        return ResponseEntity.ok(projectPermissionService.listProjectUsersByProjectIdAndRoleLabel(projectId, roleLable));
    }


    /**
     * 根据projectId和param模糊查询loginName和realName两列
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目下的用户列表(根据登录名或真实名称搜索)")
    @GetMapping(value = "/{project_id}/users/search_by_name")
    public ResponseEntity<List<UserDTO>> listUsersByName(@PathVariable(name = "project_id") Long projectId,
                                                         @RequestParam(required = false) String param) {
        return ResponseEntity.ok(projectPermissionService.listUsersByName(projectId, param));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("根据项目id查询项目下的项目所有者")
    @GetMapping("/{project_id}/owner/list")
    public ResponseEntity<List<UserDTO>> listProjectOwnerById(@PathVariable(name = "project_id") Long projectId) {
        return ResponseEntity.ok(projectPermissionService.listProjectOwnerById(projectId));
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目下的用户列表，根据真实名称或登录名搜索(限制20个)")
    @GetMapping(value = "/{project_id}/users/search_by_name/with_limit")
    public ResponseEntity<List<UserDTO>> listUsersByNameWithLimit(@PathVariable(name = "project_id") Long projectId,
                                                                  @RequestParam(name = "param", required = false) String param) {
        return ResponseEntity.ok(projectPermissionService.listUsersByNameWithLimit(projectId, param));
    }


    @Permission(permissionLogin = true)
    @ApiOperation(value = "检查是否还能创建用户")
    @GetMapping("/{project_id}/users/check_enable_create")
    public ResponseEntity<Boolean> checkEnableCreateUser(@PathVariable(name = "project_id") Long projectId) {
        return ResponseEntity.ok(organizationResourceLimitService.checkEnableCreateProjectUser(projectId));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "敏捷分页模糊查询项目下的用户和分配issue的用户接口")
    @PostMapping(value = "/{project_id}/agile_users")
    @CustomPageRequest
    public ResponseEntity<Page<UserDTO>> agileUsers(@PathVariable(name = "project_id") Long id,
                                                    @ApiIgnore
                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                            PageRequest pageable,
                                                    @Encrypt @RequestBody Set<Long> userIds,
                                                    @RequestParam(required = false) String param) {
        return new ResponseEntity<>(projectC7nService.agileUsers(id, pageable, userIds, param), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层批量分配用户角色")
    @PostMapping(value = "/{project_id}/users/assign_roles")
    public ResponseEntity<Void> assignUsersRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                               @RequestBody List<ProjectPermissionDTO> projectUserDTOList) {
        projectPermissionService.assignUsersProjectRoles(projectId, projectUserDTOList);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层更新用户角色")
    @PutMapping(value = "/{project_id}/users/{user_id}/assign_roles")
    public ResponseEntity<Void> updateUserRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                              @RequestParam(name = "sync_all", required = false, defaultValue = "false") Boolean syncAll,
                                                              @Encrypt @PathVariable(name = "user_id") Long userId,
                                                              @Encrypt @RequestBody Set<Long> roleIds) {
        projectPermissionService.updateUserRoles(userId, projectId, roleIds, syncAll);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("项目层从excel里面批量导入用户角色关系")
    @PostMapping("/{project_id}/role_members/batch_import")
    public ResponseEntity<Void> import2MemberRoleOnProject(@PathVariable(name = "project_id") Long projectId,
                                                           @RequestPart MultipartFile file) {
        roleMemberService.import2MemberRole(projectId, ResourceLevel.PROJECT.value(), file);
        return ResponseEntity.noContent().build();
    }

    /**
     * 在project层根据id删除角色
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层批量移除用户")
    @PostMapping(value = "/{project_id}/users/{user_id}/role_members/delete")
    public ResponseEntity<Void> deleteOnProjectLevel(@PathVariable(name = "project_id") Long sourceId,
                                                     @RequestParam(name = "sync_all", required = false, defaultValue = "false") Boolean syncAll,
                                                     @Encrypt @PathVariable(name = "user_id") Long userId) {
        roleMemberService.deleteOnProjectLevel(sourceId, userId, syncAll);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * 统计项目下活跃成员
     *
     * @param projectId   项目id
     * @param pageRequest 分页参数
     * @return 在线用户信息
     */
    @ApiOperation("项目下活跃成员统计")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/{project_id}/user_count")
    public ResponseEntity<OnlineUserStatistics> getUserCount(
            @ApiParam(value = "项目id", required = true)
            @PathVariable("project_id") Long projectId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest
    ) {
        return Optional.ofNullable(projectPermissionService.getUserCount(projectId, pageRequest))
                .map(target -> new ResponseEntity<>(target, HttpStatus.OK))
                .orElseThrow(() -> new CommonException("error.iam.project.overview.user"));
    }
}
