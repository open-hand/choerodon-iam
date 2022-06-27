package io.choerodon.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.hzero.iam.domain.entity.Tenant;
import org.hzero.iam.domain.entity.User;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ExternalTenantVO;
import io.choerodon.iam.api.vo.ProjectOverViewVO;
import io.choerodon.iam.api.vo.TenantVO;
import io.choerodon.iam.app.service.DemoRegisterService;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.app.service.TenantC7nService;
import io.choerodon.iam.app.service.UploadHistoryService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @Author: scp
 * @Description: 组织查询相关接口
 * 重要接口已经迁移至business OrganizationProController
 * @Date: Created in 2021/11/9
 * @Modified By:
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_TENANT)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations")
public class TenantC7nController {

    private TenantC7nService tenantC7nService;
    private OrganizationResourceLimitService organizationResourceLimitService;
    private DemoRegisterService demoRegisterService;

    public TenantC7nController(TenantC7nService tenantC7nService,
                               DemoRegisterService demoRegisterService,
                               OrganizationResourceLimitService organizationResourceLimitService) {
        this.tenantC7nService = tenantC7nService;
        this.demoRegisterService = demoRegisterService;
        this.organizationResourceLimitService = organizationResourceLimitService;
    }

    @ApiOperation(value = "校验用户邮箱是否在iam/gitlab已存在")
    @GetMapping(value = "/check/email")
    @Permission(permissionPublic = true)
    public ResponseEntity<Void> checkEmailIsExist(
            @RequestParam(value = "email") String email) {
        demoRegisterService.checkEmail(email);
        return ResponseEntity.ok().build();
    }

    /**
     * 根据组织id查询组织
     *
     * @param id 所要查询的组织id号
     * @return 组织信息
     */
    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层根据组织id查询组织")
    @GetMapping(value = "/{tenant_id}")
    public ResponseEntity<TenantVO> query(@PathVariable(name = "tenant_id") Long id,
                                          @RequestParam(value = "with_more_info", required = false, defaultValue = "true") Boolean withMoreInfo) {
        return new ResponseEntity<>(tenantC7nService.queryTenantById(id, withMoreInfo), HttpStatus.OK);
    }

    /**
     * 查询默认运营组织信息
     *
     * @return 组织信息
     */
    @Permission(permissionPublic = true)
    @ApiOperation(value = "查询默认运营组织信息")
    @GetMapping(value = "/default")
    public ResponseEntity<Tenant> queryDefault() {
        return ResponseEntity.ok(tenantC7nService.queryDefault());
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "全局层根据组织名称查询组织")
    @GetMapping(value = "/listByName")
    public ResponseEntity<List<TenantVO>> queryOrganizationsByName(@RequestParam(name = "organization_name") String organizationName) {
        return new ResponseEntity<>(tenantC7nService.queryTenantByName(organizationName), HttpStatus.OK);
    }

    /**
     * 组织层根据组织id查询组织,附带该用户在该组织分配了那些角色，以及该组织下所有的项目数量
     *
     * @param id 所要查询的组织id号
     * @return 组织信息
     */
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "组织层根据组织id查询组织，并查询当前用户被分配的角色")
    @GetMapping(value = "/{tenant_id}/org_level")
    public ResponseEntity<TenantVO> queryOrgLevel(@PathVariable(name = "tenant_id") Long id) {
        return new ResponseEntity<>(tenantC7nService.queryTenantWithRoleById(id), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "分页查询所有组织基本信息")
    @GetMapping(value = "/all")
    @CustomPageRequest
    public ResponseEntity<Page<TenantVO>> getAllOrgs(@ApiIgnore
                                                     @SortDefault(value = "tenantId", direction = Sort.Direction.DESC) PageRequest pageRequest) {
        return new ResponseEntity<>(tenantC7nService.getAllTenants(pageRequest), HttpStatus.OK);
    }

    /**
     * 根据id集合查询组织
     *
     * @param ids id集合，去重
     * @return 组织集合
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据id集合查询组织")
    @PostMapping("/ids")
    public ResponseEntity<List<Tenant>> queryByIds(@Encrypt @RequestBody Set<Long> ids) {
        return Results.success(tenantC7nService.queryTenantsByIds(ids));
    }

    /**
     * 根据organizationId和param模糊查询loginName和realName两列
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_MEMBER)
    @ApiOperation(value = "分页模糊查询组织下的用户")
    @GetMapping(value = "/{tenant_id}/users")
    @CustomPageRequest
    public ResponseEntity<Page<User>> pagingQueryUsersOnOrganization(@PathVariable(name = "tenant_id") Long id,
                                                                     @ApiIgnore
                                                                     @SortDefault(value = "organizationId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                     @Encrypt @RequestParam(required = false, name = "id") Long userId,
                                                                     @RequestParam(required = false) String email,
                                                                     @RequestParam(required = false) String param) {
        return new ResponseEntity<>(tenantC7nService.pagingQueryUsersInOrganization(id, userId, email, pageRequest, param), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_MEMBER)
    @ApiOperation(value = "分页模糊查询组织下的用户(敏捷需要)")
    @PostMapping(value = "/{tenant_id}/users/agile")
    @CustomPageRequest
    public ResponseEntity<Page<User>> pagingQueryUsersOnOrganizationAgile(@PathVariable(name = "tenant_id") Long id,
                                                                          @ApiIgnore
                                                                          @SortDefault(value = "organizationId", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                          @Encrypt @RequestParam(required = false, name = "id") Long userId,
                                                                          @RequestParam(required = false) String email,
                                                                          @RequestParam(required = false) String param,
                                                                          @RequestBody @Encrypt List<Long> notSelectUserIds) {
        return new ResponseEntity<>(tenantC7nService.pagingQueryUsersOnOrganizationAgile(id, userId, email, pageRequest, param, notSelectUserIds), HttpStatus.OK);
    }

    @CustomPageRequest
    @PostMapping("/specified")
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据组织Id列表分页查询组织简要信息")
    public ResponseEntity<Page<Tenant>> pagingSpecified(@SortDefault(value = "tenantId", direction = Sort.Direction.ASC) PageRequest pageRequest,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String code,
                                                        @RequestParam(required = false) Boolean enabled,
                                                        @RequestParam(required = false) String params,
                                                        @Encrypt @RequestBody Set<Long> orgIds) {
        return new ResponseEntity<>(tenantC7nService.pagingSpecified(orgIds, name, code, enabled, params, pageRequest), HttpStatus.OK);
    }


    @GetMapping("/{tenant_id}/project/overview")
    @Permission(level = ResourceLevel.ORGANIZATION, roles = InitRoleCode.ORGANIZATION_ADMINISTRATOR)
    @ApiOperation(value = "组织概览，返回启用项目数量和停用项目数量")
    public ResponseEntity<ProjectOverViewVO> projectOverview(
            @PathVariable(name = "tenant_id") Long organizationId) {
        return new ResponseEntity<>(tenantC7nService.projectOverview(organizationId), HttpStatus.OK);
    }

    @GetMapping("/{tenant_id}/appserver/overview")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "组织概览，返回应用服务的概览")
    public ResponseEntity<List<ProjectOverViewVO>> appServerOverview(
            @PathVariable(name = "tenant_id") Long organizationId) {
        return new ResponseEntity<>(tenantC7nService.appServerOverview(organizationId), HttpStatus.OK);
    }

    @GetMapping("/{tenant_id}/check_is_register")
    @Permission(permissionWithin = true)
    @ApiOperation(value = "判断组织是否是注册组织")
    public ResponseEntity<Boolean> checkOrganizationIsRegister(@PathVariable(name = "tenant_id") Long organizationId) {
        return ResponseEntity.ok(organizationResourceLimitService.checkOrganizationIsRegister(organizationId));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "全局层根据组织id查询组织")
    @PostMapping(value = "/list")
    public ResponseEntity<List<Tenant>> queryTenants(
            @RequestBody Set<Long> tenantIds) {
        return ResponseEntity.ok(tenantC7nService.queryTenants(tenantIds));
    }


    @Permission(permissionWithin = true)
    @ApiOperation("查询组织包括他的外部信息的开源实现")
    @GetMapping("/external/tenants")
    public ResponseEntity<ExternalTenantVO> queryTenantByIdWithExternalInfo(@RequestParam("organization_id") Long organizationId) {
        return new ResponseEntity<>(tenantC7nService.queryTenantByIdWithExternalInfo(organizationId), HttpStatus.OK);
    }
}
