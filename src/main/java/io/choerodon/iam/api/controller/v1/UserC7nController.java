package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.NotFoundException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.*;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.*;
import org.hzero.iam.infra.mapper.PasswordPolicyMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.PasswordPolicyMapper;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.util.Results;
import org.hzero.iam.app.service.UserService;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.PasswordPolicyMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.*;

/**
 * @author superlee
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_USER)
@RestController
@RequestMapping(value = "/choerodon/v1/users")
public class UserC7nController extends BaseController {

    private UserService userService;
    private UserC7nService userC7nService;
    private PasswordPolicyMapper passwordPolicyMapper;
    private TenantC7nService tenantC7nService;

    public UserC7nController(UserService userService,
                             UserC7nService userC7nService,
                             PasswordPolicyMapper passwordPolicyMapper,
                             TenantC7nService tenantC7nService) {
        this.userService = userService;
        this.userC7nService = userC7nService;
        this.passwordPolicyMapper = passwordPolicyMapper;
        this.tenantC7nService = tenantC7nService;
    }


    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "根据id查询用户信息")
    @GetMapping(value = "/{id}/info")
    public ResponseEntity<User> queryInfo(@Encrypt @PathVariable Long id) {
        return Optional.ofNullable(userC7nService.queryInfo(id))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(NotFoundException::new);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "获取组织注册信息")
    @GetMapping(value = "/registrant")
    public ResponseEntity<RegistrantInfoDTO> queryInfoSkipLogin(
            @RequestParam(value = "org_code") String orgCode) {
        return Optional.ofNullable(userC7nService.queryRegistrantInfoAndAdmin(orgCode))
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElseThrow(NotFoundException::new);
    }


    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "修改用户信息")
    @PutMapping(value = "/{id}/info")
    public ResponseEntity<User> updateInfo(@Encrypt @PathVariable Long id,
                                           @RequestBody UserDTO user) {
        user.setId(id);
        if (user.getObjectVersionNumber() == null) {
            throw new CommonException("error.user.objectVersionNumber.null");
        }
        User queryInfo = userC7nService.queryInfo(id);
        if (Objects.isNull(queryInfo)) {
            throw new CommonException("error.user.update");
        }
        user.setAdmin(queryInfo.getAdmin());
        //不能修改状态
        user.setEmailCheckFlag(BaseConstants.Flag.YES);
        user.setPhoneCheckFlag(BaseConstants.Flag.YES);
        user.setEnabled(queryInfo.getEnabled());
        user.setLdap(queryInfo.getLdap());
        user.setOrganizationId(queryInfo.getOrganizationId());
        user.setLoginName(queryInfo.getLoginName());
        user.setLocked(queryInfo.getLocked());
        return new ResponseEntity<>(userC7nService.updateInfo(user, true), HttpStatus.OK);
    }

    /**
     * 上传头像到文件服务返回头像url
     */
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "用户头像上传")
    @PostMapping(value = "/{id}/upload_photo")
    public ResponseEntity<String> uploadPhoto(@Encrypt @PathVariable Long id,
                                              @RequestPart MultipartFile file) {
        return new ResponseEntity<>(userC7nService.uploadPhoto(id, file), HttpStatus.OK);
    }

    /**
     * 上传头像，支持裁剪，旋转，并保存
     */
    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "用户头像上传裁剪，旋转并保存")
    @PostMapping(value = "/{id}/save_photo")
    public ResponseEntity<String> savePhoto(@Encrypt @PathVariable Long id,
                                            @RequestPart MultipartFile file,
                                            @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
                                            @RequestParam(required = false) Double rotate,
                                            @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
                                            @RequestParam(required = false, name = "startX") Integer axisX,
                                            @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
                                            @RequestParam(required = false, name = "startY") Integer axisY,
                                            @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
                                            @RequestParam(required = false, name = "endX") Integer width,
                                            @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
                                            @RequestParam(required = false, name = "endY") Integer height) {
        return new ResponseEntity<>(userC7nService.savePhoto(id, file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询用户所在项目列表")
    @GetMapping(value = "/{id}/projects")
    public ResponseEntity<List<ProjectDTO>> queryProjects(@Encrypt @PathVariable Long id,
                                                          @RequestParam(required = false, name = "included_disabled")
                                                                  boolean includedDisabled) {
        return new ResponseEntity<>(userC7nService.queryProjects(id, includedDisabled), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询用户所在项目列表（只包含devops项目和运维项目）")
    @GetMapping(value = "/projects/devops_and_operations")
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> queryProjectsOfDevopsOrOperations(
            PageRequest pageRequest,
            @RequestParam(value = "project_name", required = false) String projectName
    ) {
        return ResponseEntity.ok(userC7nService.queryProjectsOfDevopsOrOperations(projectName, pageRequest));
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation(value = "用户信息校验")
    @PostMapping(value = "/check")
    public ResponseEntity<Void> checkUserInfo(@RequestBody User user) {
        userC7nService.check(user);
        return ResponseEntity.ok().build();
    }


    /**
     * 分页查询所有的admin用户
     *
     * @return 分页的admin用户
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "分页模糊查询管理员用户列表")
    @GetMapping("/admin")
    @CustomPageRequest
    public ResponseEntity<Page<User>> pagingQueryAdminUsers(@ApiIgnore
                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                            @RequestParam(required = false) String loginName,
                                                            @RequestParam(required = false) String realName,
                                                            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(userC7nService.pagingQueryAdminUsers(pageRequest, loginName, realName, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "批量给用户添加管理员身份")
    @PostMapping("/admin")
    public ResponseEntity<Void> addDefaultUsers(@Encrypt @RequestParam("id") Long[] ids) {
        userC7nService.addAdminUsers(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "清除用户的管理员身份")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteDefaultUser(@Encrypt @PathVariable Long id) {
        userC7nService.deleteAdminUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据id批量查询用户信息列表")
    @PostMapping(value = "/ids")
    public ResponseEntity<List<User>> listUsersByIds(@Encrypt @RequestBody Long[] ids,
                                                     @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled) {
        return new ResponseEntity<>(userC7nService.listUsersByIds(ids, onlyEnabled), HttpStatus.OK);
    }


    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据id批量查询带有gitlab用户id的用户信息列表")
    @PostMapping(value = "/list_by_ids")
    public ResponseEntity<List<UserWithGitlabIdVO>> listUsersWithGitlabIdByIds(
            @ApiParam(value = "是否只查询启用的用户", required = false)
            @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled,
            @ApiParam(value = "用户id集合", required = true)
            @Encrypt @RequestBody Set<Long> ids) {
        return new ResponseEntity<>(userC7nService.listUsersByIds(ids, onlyEnabled), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据email批量查询用户信息列表")
    @PostMapping(value = "/emails")
    public ResponseEntity<List<User>> listUsersByEmails(@RequestBody String[] emails) {
        return new ResponseEntity<>(userC7nService.listUsersByEmails(emails), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据loginName批量查询用户信息列表")
    @PostMapping(value = "/login_names")
    public ResponseEntity<List<User>> listUsersByLoginNames(@RequestBody String[] loginNames,
                                                            @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled) {
        return new ResponseEntity<>(userC7nService.listUsersByLoginNames(loginNames, onlyEnabled), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据realName集合批量查询用户信息列表")
    @PostMapping(value = "/real_names")
    public ResponseEntity<List<User>> listUsersByRealNames(@RequestBody Set<String> realNames,
                                                           @RequestParam(value = "only_enabled", defaultValue = "true", required = false) Boolean onlyEnabled) {
        return new ResponseEntity<>(userC7nService.listUsersByRealNames(realNames, onlyEnabled), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("根据id分页获取组织列表和角色")
    @GetMapping("/{id}/organization_roles")
    @CustomPageRequest
    public ResponseEntity<Page<TenantVO>> pagingQueryOrganizationAndRolesById(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @Encrypt @PathVariable(value = "id") Long id,
            @RequestParam(value = "params", required = false) String[] params) {
        return new ResponseEntity<>(userC7nService.pagingQueryOrganizationsWithRoles(pageRequest, id, ParamUtils.arrToStr(params)), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("根据id分页获取项目列表和角色")
    @GetMapping("/{id}/project_roles")
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> pagingQueryProjectAndRolesById(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @Encrypt @PathVariable("id") Long id,
            @RequestParam(value = "params", required = false) String[] params) {
        return new ResponseEntity<>(userC7nService.pagingQueryProjectAndRolesById(pageRequest, id, ParamUtils.arrToStr(params)), HttpStatus.OK);
    }

//    @Permission(permissionWithin = true)
//    @ApiOperation("新建用户，并根据角色code分配角色")
//    @PostMapping("/init_role")
//    public ResponseEntity<UserDTO> createUserAndAssignRoles(@RequestBody CreateUserWithRolesDTO userWithRoles) {
//        return new ResponseEntity<>(userService.createUserAndAssignRoles(userWithRoles), HttpStatus.OK);
//    }


    /**
     * 根据用户邮箱查询对应组织下的密码策略
     *
     * @return 目标组织密码策略
     */
    @Permission(permissionPublic = true)
    @ApiOperation(value = "根据用户邮箱查询对应组织下的密码策略")
    @GetMapping("/password_policies")
    public ResponseEntity<PasswordPolicy> queryByUserEmail(@RequestParam(value = "email", required = false) String email) {
        Long tenantId = userC7nService.queryOrgIdByEmail(email);
        PasswordPolicy params = new PasswordPolicy();
        params.setOrganizationId(tenantId);
        return new ResponseEntity<>(passwordPolicyMapper.selectOne(params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "卡片：新增用户统计")
    @GetMapping("/new")
    public ResponseEntity<Map<String, Object>> queryNewAndAllUsers() {
        return new ResponseEntity<>(userC7nService.queryAllAndNewUsers(), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层分页查询用户列表（包括用户信息以及所分配的全局角色信息）")
    @GetMapping(value = "/search")
    @CustomPageRequest
    public ResponseEntity<Page<User>> pagingQueryUsersWithRolesOnSiteLevel(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                           @RequestParam(required = false) String organizationName,
                                                                           @RequestParam(required = false) String loginName,
                                                                           @RequestParam(required = false) String realName,
                                                                           @RequestParam(required = false) String roleName,
                                                                           @RequestParam(required = false) Boolean enabled,
                                                                           @RequestParam(required = false) Boolean locked,
                                                                           @RequestParam(required = false) String params) {
        return new ResponseEntity<>(userC7nService.pagingQueryUsersWithRolesOnSiteLevel(pageRequest, organizationName, loginName, realName, roleName,
                enabled, locked, params), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层修改用户")
    @PutMapping(value = "/{id}")
    public ResponseEntity<User> updateUser(
            @Encrypt @PathVariable Long id,
            @RequestBody User userDTO) {
        // 不能更新admin字段
        userDTO.setAdmin(null);
        // 不能更新ldap字段
        userDTO.setLdap(null);
        // 不能更新登录名
        userDTO.setLoginName(null);
        // 不能更新所属组织
        userDTO.setOrganizationId(null);
        // 不能修改密码
        userDTO.setPassword(null);
        userDTO.setId(id);
        return new ResponseEntity<>(userService.updateUser(userDTO), HttpStatus.OK);
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "根据用户id查询对应的组织和项目")
    @GetMapping("/{id}/organization_project")
    public ResponseEntity<OrganizationProjectVO> queryOrganizationProjectByUserId(@Encrypt @PathVariable("id") Long id,
                                                                                  @RequestParam(value = "project_name", required = false) String projectName) {
        return ResponseEntity.ok(userC7nService.queryOrganizationProjectByUserId(id, projectName));
    }


//    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
//    @ApiOperation("获取用户下的指定项目(没有项目权限或者项目不存在返回空)")
//    @GetMapping("/{id}/projects/{project_id}")
//    public ResponseEntity<ProjectDTO> queryProjectById(
//            @PathVariable("id") Long id,
//            @PathVariable("project_id") Long projectId) {
//        return ResponseEntity.ok(userC7nService.queryProjectById(id, projectId));
//    }
//


    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("校验用户是否是gitlab项目的所有者")
    @GetMapping("/{id}/projects/{project_id}/check_is_gitlab_owner")
    public ResponseEntity<Boolean> checkIsGitlabProjectOwner(
            @Encrypt @PathVariable("id") Long id,
            @PathVariable("project_id") Long projectId) {
        return ResponseEntity.ok(userC7nService.checkIsGitlabOwner(id, projectId, ResourceLevel.PROJECT.value()));
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("校验用户是否是gitlab组织层owner")
    @GetMapping("/{id}/projects/{project_id}/check_is_gitlab_org_owner")
    public ResponseEntity<Boolean> checkIsGitlabOrgOwner(
            @Encrypt @PathVariable("id") Long id,
            @PathVariable("project_id") Long projectId) {
        return ResponseEntity.ok(userC7nService.checkIsGitlabOwner(id, projectId, ResourceLevel.ORGANIZATION.value()));
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("校验用户是否是gitlab组织层owner或者项目层的owner")
    @GetMapping("/{id}/projects/{project_id}/check_is_org_or_proj_gitlab_owner")
    public ResponseEntity<Boolean> checkIsOrgOrProjectGitlabOwner(
            @Encrypt @PathVariable("id") Long id,
            @PathVariable("project_id") Long projectId) {
        return ResponseEntity.ok(userC7nService.checkIsGitlabOwnerInOrgOrProject(projectId, id));
    }

    @Permission(level = ResourceLevel.SITE, permissionLogin = true)
    @ApiOperation("校验用户是否是项目的所有者")
    @GetMapping("/{id}/projects/{project_id}/check_is_owner")
    public ResponseEntity<Boolean> checkIsProjectOwner(
            @Encrypt @PathVariable("id") Long id,
            @PathVariable("project_id") Long projectId) {
        return ResponseEntity.ok(userC7nService.checkIsProjectOwner(id, projectId));
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台人数统计")
    @GetMapping(value = "/count_by_date")
    public ResponseEntity<UserNumberVO> countByDateInSite(@RequestParam(value = "start_time") Date startTime,
                                                          @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(userC7nService.countByDate(null, startTime, endTime));
    }

    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    @ApiOperation("校验用户是否是Root用户")
    @GetMapping("/{id}/check_is_root")
    public ResponseEntity<Boolean> checkIsRoot(@Encrypt @PathVariable("id") Long id) {
        return ResponseEntity.ok(userC7nService.isRoot(id));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation("查询用户组织列表，根据into字段判断是否能够进入")
    @GetMapping("/{user_id}/organizations")
    public ResponseEntity<List<TenantVO>> listOwnedOrganizationByUserId(@Encrypt @PathVariable("user_id") Long userId) {
        return ResponseEntity.ok(tenantC7nService.listOwnedOrganizationByUserId(userId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "根据用户名查询用户信息")
    @GetMapping
    public ResponseEntity<UserDTO> query(@RequestParam(name = "login_name") String loginName) {
        return Results.success(userC7nService.queryByLoginName(loginName));
    }


    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiOperation(value = "项目层查询所有包含gitlab角色标签的用户")
    @PostMapping(value = "/projects/{project_id}/gitlab_role/users")
    public ResponseEntity<List<UserDTO>> listUsersWithGitlabLabel(
            @PathVariable(name = "project_id") Long projectId,
            @RequestParam(name = "label_name") String labelName,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return new ResponseEntity<>(userC7nService.listUsersWithGitlabLabel(projectId, labelName, roleAssignmentSearchDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiOperation(value = "批量根据项目id查询用户在这个项目下拥有的角色标签, 如果在某个项目下没有角色, 不会包含该项目的纪录")
    @PostMapping(value = "/{user_id}/project_role_labels")
    public ResponseEntity<List<UserProjectLabelVO>> listRoleLabelsForUserInTheProject(
            @Encrypt @PathVariable("user_id") Long userId,
            @Encrypt @RequestBody Set<Long> projectIds) {
        return ResponseEntity.ok(userC7nService.listRoleLabelsForUserInTheProject(userId, projectIds));
    }


    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询平台层市场审核员的用户")
    @GetMapping(value = "/roles/site/market_auditor")
    public ResponseEntity<List<User>> listMarketAuditor() {
        return ResponseEntity.ok(userC7nService.listMarketAuditor());
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询系统的所有root用户 包括admin")
    @GetMapping(value = "/root")
    public ResponseEntity<List<User>> listRoot() {
        return ResponseEntity.ok(userC7nService.listRoot());
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询平台中所有用户的数量")
    @GetMapping(value = "/all_user_count")
    public ResponseEntity<UserCountVO> countAllUsers() {
        return ResponseEntity.ok(userC7nService.countAllUsers());
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询平台中所有用户的id")
    @GetMapping(value = "/all_user_ids")
    public ResponseEntity<Set<Long>> listAllUserIds() {
        return ResponseEntity.ok(userC7nService.listAllUserIds());
    }

    @ApiModelProperty(value = "查询用户是不是平台管理员")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/self/is_site_administrator")
    public ResponseEntity<Boolean> platformAdministrator() {
        return ResponseEntity.ok(userC7nService.platformAdministrator());
    }

    @ApiModelProperty(value = "查询用户是不是平台管理员")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/self/admin/org/list")
    public ResponseEntity<List<Tenant>> adminOrgList() {
        return ResponseEntity.ok(userC7nService.adminOrgList());
    }
}