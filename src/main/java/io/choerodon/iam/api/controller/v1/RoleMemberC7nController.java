package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ClientRoleQueryVO;
import io.choerodon.iam.api.vo.SimplifiedUserVO;
import io.choerodon.iam.api.vo.agile.RoleVO;
import io.choerodon.iam.app.service.*;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.RoleAssignmentSearchDTO;
import io.choerodon.iam.infra.dto.RoleC7nDTO;
import io.choerodon.iam.infra.dto.UploadHistoryDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.enums.ExcelSuffix;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Client;
import org.hzero.iam.domain.entity.MemberRole;
import org.hzero.iam.domain.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;


/**
 * @author superlee
 * @author wuguokai
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_ROLE_MEMBER)
@RestController
@RequestMapping(value = "/choerodon/v1")
public class RoleMemberC7nController extends BaseController {

    public static final String MEMBER_ROLE = "member-role";

    private RoleC7nService roleC7nService;
    private ProjectUserService projectUserService;
    private UserC7nService userC7nService;
    private RoleMemberService roleMemberService;
    private ClientC7nService clientC7nService;
    private UploadHistoryService uploadHistoryService;


    public RoleMemberC7nController(RoleC7nService roleC7nService,
                                   UserC7nService userC7nService,
                                   ClientC7nService clientC7nService,
                                   UploadHistoryService uploadHistoryService,
                                   ProjectUserService projectUserService,
                                   RoleMemberService roleMemberService) {
        this.roleC7nService = roleC7nService;
        this.projectUserService = projectUserService;
        this.uploadHistoryService = uploadHistoryService;
        this.userC7nService = userC7nService;
        this.clientC7nService = clientC7nService;
        this.roleMemberService = roleMemberService;
    }

    /**
     * 查询project层角色,附带该角色下分配的用户数
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询角色列表以及该角色下的用户数量")
    @PostMapping(value = "/projects/{project_id}/role_members/users/count")
    public ResponseEntity<List<RoleVO>> listRolesWithUserCountOnProjectLevel(
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return ResponseEntity.ok(roleC7nService.listRolesWithUserCountOnProjectLevel(projectId, roleAssignmentSearchDTO));
    }

    /**
     * 项目层分页查询角色下的用户
     *
     * @param roleId
     * @param projectId
     * @param roleAssignmentSearchDTO
     * @param doPage                  是否分页，如果为false，则不分页
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
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
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询用户在项目下拥有的角色")
    @GetMapping(value = "/projects/{project_id}/role_members/users/{user_id}")
    public ResponseEntity<List<RoleDTO>> getUserRolesByUserIdAndProjectId(@PathVariable(name = "project_id") Long projectId,
                                                                          @PathVariable(name = "user_id") Long userId) {
        return ResponseEntity.ok(projectUserService.listRolesByProjectIdAndUserId(projectId, userId));
    }

    /**
     * 在项目层查询用户，用户包含拥有的project层的角色
     *
     * @param projectId               项目id
     * @param roleAssignmentSearchDTO 查询请求体，无查询条件需要传{}
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询用户列表以及该用户拥有的角色")
    @PostMapping(value = "/projects/{project_id}/role_members/users/roles")
    public ResponseEntity<Page<UserDTO>> pagingQueryUsersWithProjectLevelRoles(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @PathVariable(name = "project_id") Long projectId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return ResponseEntity.ok(projectUserService.pagingQueryUsersWithRoles(
                pageRequest, roleAssignmentSearchDTO, projectId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询角色列表")
    @GetMapping(value = "/projects/{project_id}/roles")
    public ResponseEntity<List<RoleDTO>> listRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                 @RequestParam(name = "role_name") String roleName,
                                                                 @RequestParam(name = "only_select_enable", required = false, defaultValue = "true")
                                                                         Boolean onlySelectEnable) {
        return new ResponseEntity<>(projectUserService.listRolesByName(projectId, roleName, onlySelectEnable), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询启用状态的用户列表")
    @GetMapping(value = "/organizations/{organization_id}/enableUsers")
    public ResponseEntity<List<User>> listUsersOnOrganizationLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                   @RequestParam(name = "user_name") String userName) {
        return new ResponseEntity<>(userC7nService.listEnableUsersByName
                (ResourceLevel.ORGANIZATION.value(), organizationId, userName), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询启用状态的用户列表")
    @GetMapping(value = "/projects/{project_id}/enableUsers")
    public ResponseEntity<List<User>> listUsersOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                              @RequestParam(name = "user_name") String userName) {
        return new ResponseEntity<>(userC7nService.listEnableUsersByName
                (ResourceLevel.PROJECT.value(), projectId, userName), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层查询启用状态的用户列表")
    @GetMapping(value = "/site/enableUsers")
    public ResponseEntity<List<User>> listUsersOnSiteLevel(@RequestParam(name = "user_name") String userName) {
        return new ResponseEntity<>(userC7nService.listEnableUsersByName
                (ResourceLevel.SITE.value(), 0L, userName), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询角色列表")
    @GetMapping(value = "/organizations/{organization_id}/roles")
    public ResponseEntity<List<RoleDTO>> listRolesOnOrganizationLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                      @RequestParam(name = "role_name") String roleName,
                                                                      @RequestParam(name = "only_select_enable", required = false, defaultValue = "true")
                                                                              Boolean onlySelectEnable) {
        return new ResponseEntity<>(roleC7nService.listRolesByName(organizationId, roleName, onlySelectEnable), HttpStatus.OK);
    }

    @Permission(permissionPublic = true)
    @ApiOperation(value = "分页查询全平台层用户（未禁用）")
    @GetMapping(value = "/all/users")
    @CustomPageRequest
    public ResponseEntity<Page<SimplifiedUserVO>> queryAllUsers(@ApiIgnore
                                                                @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                @RequestParam(value = "organization_id") Long organizationId,
                                                                @RequestParam(value = "param", required = false) String param) {
        return new ResponseEntity<>(userC7nService.pagingQueryAllUser(pageRequest, param, organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层批量分配用户角色")
    @PostMapping(value = "/organizations/{organization_id}/users/assign_roles")
    public ResponseEntity<Void> assignUsersRolesOnOrganizationLevel(@PathVariable(name = "organization_id") Long organizationId,
                                                                    @RequestBody List<MemberRole> memberRoleDTOS) {
        userC7nService.assignUsersRolesOnOrganizationLevel(organizationId, memberRoleDTOS);
        return ResponseEntity.noContent().build();
    }

    /**
     * 组织层下载模板
     *
     * @param organizationId
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层下载excel导入模板")
    @GetMapping(value = "/organizations/{organization_id}/role_members/download_templates")
    public ResponseEntity<Resource> downloadTemplatesOnOrganization(@PathVariable(name = "organization_id") Long organizationId) {
        return roleMemberService.downloadTemplatesByResourceLevel(ExcelSuffix.XLSX.value(), ResourceLevel.ORGANIZATION.value());
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("组织层从excel里面批量导入用户角色关系")
    @PostMapping("/organizations/{organization_id}/role_members/batch_import")
    public ResponseEntity import2MemberRoleOnOrganization(@PathVariable(name = "organization_id") Long organizationId,
                                                          @RequestPart MultipartFile file) {
        roleMemberService.import2MemberRole(organizationId, ResourceLevel.ORGANIZATION.value(), file);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层分页查询角色下的客户端")
    @PostMapping(value = "/organizations/{organization_id}/role_members/clients")
    public ResponseEntity<Page<Client>> pagingQueryClientsByRoleIdOnOrganizationLevel(
            @ApiIgnore
            @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
            @RequestParam(name = "role_id") Long roleId,
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestBody(required = false) @Valid ClientRoleQueryVO clientRoleQueryVO) {
        return new ResponseEntity<>(clientC7nService.pagingQueryUsersByRoleId(pageRequest, ResourceLevel.ORGANIZATION, sourceId, clientRoleQueryVO, roleId), HttpStatus.OK);
    }

    /**
     * 查询organization层角色,附带该角色下分配的客户端数
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询角色列表以及该角色下的客户端数量")
    @PostMapping(value = "/organizations/{organization_id}/role_members/clients/count")
    public ResponseEntity<List<RoleC7nDTO>> listRolesWithClientCountOnOrganizationLevel(
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestBody(required = false) @Valid ClientRoleQueryVO clientRoleQueryVO) {
        return new ResponseEntity<>(roleC7nService.listRolesWithClientCountOnOrganizationLevel(
                clientRoleQueryVO, sourceId), HttpStatus.OK);
    }

    /**
     * 查询organization层角色,附带该角色下分配的用户数
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织层查询角色列表以及该角色下的用户数量")
    @PostMapping(value = "/organizations/{organization_id}/role_members/users/count")
    public ResponseEntity<List<RoleC7nDTO>> listRolesWithUserCountOnOrganizationLevel(
            @PathVariable(name = "organization_id") Long sourceId,
            @RequestBody(required = false) @Valid RoleAssignmentSearchDTO roleAssignmentSearchDTO) {
        return new ResponseEntity<>(roleC7nService.listRolesWithUserCountOnOrganizationLevel(
                roleAssignmentSearchDTO, sourceId), HttpStatus.OK);
    }

    /**
     * 查询project层角色,附带该角色下分配的客户端数
     *
     * @return 查询结果
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目层查询角色列表以及该角色下的客户端数量")
    @PostMapping(value = "/projects/{project_id}/role_members/clients/count")
    public ResponseEntity<List<RoleC7nDTO>> listRolesWithClientCountOnProjectLevel(
            @PathVariable(name = "project_id") Long sourceId,
            @RequestBody(required = false) @Valid ClientRoleQueryVO clientRoleQueryVO) {
        return new ResponseEntity<>(roleC7nService.listRolesWithClientCountOnProjectLevel(
                clientRoleQueryVO, sourceId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation("查项目层的历史")
    @GetMapping("/projects/{project_id}/member_role/users/{user_id}/upload/history")
    public ResponseEntity<UploadHistoryDTO> latestHistoryOnProject(@PathVariable(name = "project_id") Long projectId,
                                                                   @PathVariable(name = "user_id") Long userId) {
        return new ResponseEntity<>(uploadHistoryService. latestHistory(userId, MEMBER_ROLE, projectId, ResourceLevel.PROJECT.value()), HttpStatus.OK);
    }


    /**
     * 项目层下载模板
     *
     * @param projectId
     * @return
     */
    @Permission(level = ResourceLevel.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "项目层下载excel导入模板")
    @GetMapping(value = "/projects/{project_id}/role_members/download_templates")
    public ResponseEntity<Resource> downloadTemplatesOnProject(@PathVariable(name = "project_id") Long projectId) {
        return roleMemberService.downloadTemplatesByResourceLevel(ExcelSuffix.XLSX.value(), ResourceLevel.PROJECT.value());
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation("查组织层的历史")
    @GetMapping("/organizations/{organization_id}/member_role/users/{user_id}/upload/history")
    public ResponseEntity<UploadHistoryDTO> latestHistoryOnOrganization(@PathVariable(name = "organization_id") Long organizationId,
                                                                        @PathVariable(name = "user_id") Long userId) {
        return new ResponseEntity<>(uploadHistoryService.latestHistory(userId, MEMBER_ROLE, organizationId, ResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }
}
