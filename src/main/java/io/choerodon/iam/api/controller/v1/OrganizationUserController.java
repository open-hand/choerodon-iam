package io.choerodon.iam.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.user.UserType;
import org.hzero.core.util.Results;
import org.hzero.iam.domain.entity.User;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.UserNumberVO;
import io.choerodon.iam.api.vo.UserWithGitlabIdVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author superlee
 */
@Api(tags = C7nSwaggerApiConfig.ORGANIZATION_USER)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}")
public class OrganizationUserController extends BaseController {

    private OrganizationUserService organizationUserService;

    private UserC7nService userC7nService;

    private ExcelService excelService;

    private UploadHistoryService uploadHistoryService;

    private OrganizationResourceLimitService organizationResourceLimitService;

    public OrganizationUserController(OrganizationUserService organizationUserService,
                                      UploadHistoryService uploadHistoryService,
                                      ExcelService excelService,
                                      UserC7nService userC7nService,
                                      OrganizationResourceLimitService organizationResourceLimitService) {
        this.organizationUserService = organizationUserService;
        this.userC7nService = userC7nService;
        this.excelService = excelService;
        this.uploadHistoryService = uploadHistoryService;
        this.organizationResourceLimitService = organizationResourceLimitService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）")
    @GetMapping(value = "/users/search")
    @CustomPageRequest
    public ResponseEntity<Page<User>> pagingQueryUsersWithRolesOnOrganizationLevel(
            @PathVariable(name = "organization_id") Long organizationId,
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable,
            @RequestParam(required = false) String loginName,
            @RequestParam(required = false) String realName,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) Boolean locked,
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(organizationUserService.pagingQueryUsersWithRolesOnOrganizationLevel(organizationId, pageable, loginName, realName, roleName,
                enabled, locked, params), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "根据多个id查询用户（包括用户信息以及所分配的组织角色信息以及GitlabUserId）")
    @PostMapping(value = "/users/list_by_ids")
    public ResponseEntity<List<UserWithGitlabIdVO>> listUsersWithRolesAndGitlabUserIdByIds(
            @ApiParam(value = "组织id", required = true)
            @PathVariable(name = "organization_id") Long organizationId,
            @ApiParam(value = "多个用户id", required = true)
            @RequestBody Set<Long> userIds) {
        return new ResponseEntity<>(userC7nService.listUsersWithRolesAndGitlabUserIdByIdsInOrg(organizationId, userIds), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建用户并分配角色")
    @PostMapping("/users")
    public ResponseEntity<User> createUserWithRoles(@PathVariable(name = "organization_id") Long organizationId,
                                                    @RequestBody User user) {
        user.setUserType(UserType.ofDefault(user.getUserType()).value());
        user.setOrganizationId(organizationId);
        validObject(user);
        return Results.success(organizationUserService.createUserWithRoles(DetailsHelper.getUserDetails().getUserId(), user));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/users/{id}")
    public ResponseEntity<Void> update(@PathVariable(name = "organization_id") Long organizationId,
                                       @PathVariable Long id,
                                       @RequestBody User user) {
        user.setOrganizationId(organizationId);
        SecurityTokenHelper.validToken(user, false);
        organizationUserService.updateUser(organizationId, user);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "重置用户密码")
    @PutMapping(value = "/users/{id}/reset")
    public ResponseEntity<User> resetUserPassword(@PathVariable(name = "organization_id") Long organizationId, @PathVariable Long id) {
        return Results.success(organizationUserService.resetUserPassword(organizationId, id));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织下的用户")
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<User> queryUserInOrganization(@PathVariable(name = "organization_id") Long organizationId,
                                                        @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.query(organizationId, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "解锁用户")
    @PutMapping(value = "/users/{id}/unlock")
    public ResponseEntity<User> unlock(@PathVariable(name = "organization_id") Long organizationId,
                                       @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.unlock(organizationId, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "启用用户")
    @PutMapping(value = "/users/{id}/enable")
    public ResponseEntity<User> enableUser(@PathVariable(name = "organization_id") Long organizationId,
                                           @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.enableUser(organizationId, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用用户")
    @PutMapping(value = "/users/{id}/disable")
    public ResponseEntity<User> disableUser(@PathVariable(name = "organization_id") Long organizationId,
                                            @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.disableUser(organizationId, id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "用户信息重名校验")
    @PostMapping(value = "/users/check")
    public ResponseEntity check(@PathVariable(name = "organization_id") Long organizationId,
                                @RequestBody User user) {
        userC7nService.check(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("从excel里面批量导入用户")
    @PostMapping("/users/batch_import")
    public ResponseEntity importUsersFromExcel(@PathVariable(name = "organization_id") Long id,
                                               @RequestPart MultipartFile file) {
        excelService.importUsers(id, file);
        return ResponseEntity.noContent().build();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("下载导入用户的模板文件")
    @GetMapping("/users/download_templates")
    public ResponseEntity<Resource> downloadTemplates(@PathVariable(name = "organization_id") Long id) {
        HttpHeaders headers = excelService.getHttpHeaders();
        Resource resource = excelService.getUserTemplates();
        //excel2007
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(resource);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查询最新的导入历史")
    @GetMapping("/users/{user_id}/upload/history")
    public ResponseEntity<UploadHistoryDTO> latestHistory(@PathVariable(name = "organization_id") Long organizationId,
                                                          @PathVariable(name = "user_id") Long userId) {
        return new ResponseEntity<>(uploadHistoryService.latestHistory(userId, "user", organizationId, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询当前组织下用户的项目列表")
    @GetMapping(value = "/users/{user_id}/projects")
    public ResponseEntity<List<ProjectDTO>> listProjectsByUserId(@PathVariable(name = "organization_id") Long organizationId,
                                                                 @PathVariable(name = "user_id") Long userId,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String code,
                                                                 @RequestParam(required = false) String category,
                                                                 @RequestParam(required = false) Boolean enabled,
                                                                 @RequestParam(required = false) Long createdBy,
                                                                 @RequestParam(required = false) String params) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(name);
        projectDTO.setCode(code);
        projectDTO.setCategory(category);
        projectDTO.setEnabled(enabled);
        projectDTO.setCreatedBy(createdBy);
        return new ResponseEntity<>(userC7nService.listProjectsByUserId(organizationId, userId, projectDTO, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询当前组织下用户的项目列表")
    @GetMapping(value = "/users/{user_id}/projects/paging")
    public ResponseEntity<Page<ProjectDTO>> pagingProjectsByUserId(@PathVariable(name = "organization_id") Long organizationId,
                                                                 @PathVariable(name = "user_id") Long userId,
                                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageable,
                                                                 @RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) String code,
                                                                 @RequestParam(required = false) String category,
                                                                 @RequestParam(required = false) Boolean enabled,
                                                                 @RequestParam(required = false) Long createdBy,
                                                                 @RequestParam(required = false) String params) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(name);
        projectDTO.setCode(code);
        projectDTO.setCategory(category);
        projectDTO.setEnabled(enabled);
        projectDTO.setCreatedBy(createdBy);
        return new ResponseEntity<>(userC7nService.pagingProjectsByUserId(organizationId, userId, projectDTO, params, pageable), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "组织人数统计")
    @GetMapping(value = "/users/count_by_date")
    public ResponseEntity<UserNumberVO> countByDateInOrganization(@PathVariable(name = "organization_id") Long organizationId,
                                                                  @RequestParam(value = "start_time") Date startTime,
                                                                  @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(userC7nService.countByDate(organizationId, startTime, endTime));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiOperation(value = "判断用户是否是组织管理员")
    @GetMapping(value = "/users/{user_id}/check_is_root")
    public ResponseEntity<Boolean> checkIsOrgRoot(@PathVariable(name = "organization_id") Long organizationId,
                                                  @PathVariable(name = "user_id") Long userId) {
        return ResponseEntity.ok(userC7nService.checkIsOrgRoot(organizationId, userId));
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "检查是否还能创建用户")
    @GetMapping("/users/check_enable_create")
    public ResponseEntity<Boolean> checkEnableCreateUser(@PathVariable(name = "organization_id") Long organizationId) {
        return ResponseEntity.ok(organizationResourceLimitService.checkEnableCreateOrganizationUser(organizationId));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询用户有权限的项目-devops用")
    @GetMapping("/users/{user_id}/owned_projects")
    public ResponseEntity<List<ProjectDTO>> listOwnedProjects(@PathVariable(name = "organization_id") Long organizationId,
                                                              @PathVariable(name = "user_id") Long userId) {
        return ResponseEntity.ok(userC7nService.listOwnedProjects(organizationId, userId));
    }

}
