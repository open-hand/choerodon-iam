package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.validator.UserValidator;
import io.choerodon.base.api.vo.UserNumberVO;
import io.choerodon.base.app.service.ExcelService;
import io.choerodon.base.app.service.OrganizationUserService;
import io.choerodon.base.app.service.UploadHistoryService;
import io.choerodon.base.app.service.UserService;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.UploadHistoryDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.List;

/**
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}")
public class OrganizationUserController extends BaseController {

    private OrganizationUserService organizationUserService;

    private UserService userService;

    private ExcelService excelService;
    private UploadHistoryService uploadHistoryService;

    public OrganizationUserController(OrganizationUserService organizationUserService,
                                      UserService userService,
                                      ExcelService excelService,
                                      UploadHistoryService uploadHistoryService) {
        this.organizationUserService = organizationUserService;
        this.userService = userService;
        this.excelService = excelService;
        this.uploadHistoryService = uploadHistoryService;
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "组织层分页查询用户列表（包括用户信息以及所分配的组织角色信息）")
    @GetMapping(value = "/users/search")
    @CustomPageRequest
    public ResponseEntity<PageInfo<UserDTO>> pagingQueryUsersWithRolesOnOrganizationLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                                          @ApiIgnore
                                                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                                          @RequestParam(required = false) String loginName,
                                                                                          @RequestParam(required = false) String realName,
                                                                                          @RequestParam(required = false) String roleName,
                                                                                          @RequestParam(required = false) Boolean enabled,
                                                                                          @RequestParam(required = false) Boolean locked,
                                                                                          @RequestParam(required = false) String params) {
        return new ResponseEntity<>(organizationUserService.pagingQueryUsersWithRolesOnOrganizationLevel(organizationId, pageable, loginName, realName, roleName,
                enabled, locked, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建用户并分配角色")
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUserWithRoles(@PathVariable(name = "organization_id") Long organizationId,
                                                       @RequestBody @Validated UserDTO userDTO) {
        userDTO.setOrganizationId(organizationId);
        //新增用户不能创建ldap用户
        userDTO.setLdap(false);
        return new ResponseEntity<>(organizationUserService.createUserWithRoles(organizationId, userDTO, true, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改用户")
    @PutMapping(value = "/users/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable(name = "organization_id") Long organizationId,
                                          @PathVariable Long id,
                                          @RequestBody @Validated({UserValidator.UserGroup.class}) UserDTO userDTO) {
        //不能更新admin字段
        userDTO.setAdmin(null);
        //不能更新ldap字段
        userDTO.setLdap(null);
        //不能更新登录名
        userDTO.setLoginName(null);
        userDTO.setId(id);
        return new ResponseEntity<>(organizationUserService.update(organizationId, userDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "重置用户密码")
    @PutMapping(value = "/users/{id}/reset")
    public ResponseEntity<UserDTO> resetUserPassword(@PathVariable(name = "organization_id") Long organizationId, @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.resetUserPassword(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询组织下的用户")
    @GetMapping(value = "/users/{id}")
    public ResponseEntity<UserDTO> query(@PathVariable(name = "organization_id") Long organizationId,
                                         @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.query(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "解锁用户")
    @PutMapping(value = "/users/{id}/unlock")
    public ResponseEntity<UserDTO> unlock(@PathVariable(name = "organization_id") Long organizationId,
                                          @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.unlock(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "启用用户")
    @PutMapping(value = "/users/{id}/enable")
    public ResponseEntity<UserDTO> enableUser(@PathVariable(name = "organization_id") Long organizationId,
                                              @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.enableUser(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "禁用用户")
    @PutMapping(value = "/users/{id}/disable")
    public ResponseEntity<UserDTO> disableUser(@PathVariable(name = "organization_id") Long organizationId,
                                               @PathVariable Long id) {
        return new ResponseEntity<>(organizationUserService.disableUser(organizationId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "用户信息重名校验")
    @PostMapping(value = "/users/check")
    public ResponseEntity check(@PathVariable(name = "organization_id") Long organizationId,
                                @RequestBody UserDTO user) {
        userService.check(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("从excel里面批量导入用户")
    @PostMapping("/users/batch_import")
    public ResponseEntity importUsersFromExcel(@PathVariable(name = "organization_id") Long id,
                                               @RequestPart MultipartFile file) {
        excelService.importUsers(id, file);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("下载导入用户的模板文件")
    @GetMapping("/users/download_templates")
    public ResponseEntity<Resource> downloadTemplates(@PathVariable(name = "organization_id") Long id) {
        HttpHeaders headers = excelService.getHttpHeaders();
        Resource resource = excelService.getUserTemplates();
        //excel2007
        return ResponseEntity.ok().headers(headers).contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")).body(resource);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("查询最新的导入历史")
    @GetMapping("/users/{user_id}/upload/history")
    public ResponseEntity<UploadHistoryDTO> latestHistory(@PathVariable(name = "organization_id") Long organizationId,
                                                          @PathVariable(name = "user_id") Long userId) {
        return new ResponseEntity<>(uploadHistoryService.latestHistory(userId, "user", organizationId, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
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
        return new ResponseEntity<>(userService.listProjectsByUserId(organizationId, userId, projectDTO, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "组织人数统计")
    @GetMapping(value = "/users/count_by_date")
    public ResponseEntity<UserNumberVO> countByDate(@PathVariable(name = "organization_id") Long organizationId,
                                                      @RequestParam(value = "start_time") Date startTime,
                                                      @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(userService.countByDate(organizationId, startTime, endTime));
    }
}
