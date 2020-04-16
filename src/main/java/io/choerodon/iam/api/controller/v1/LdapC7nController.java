package io.choerodon.iam.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.core.util.Results;
import org.hzero.iam.api.dto.LdapAccountDTO;
import org.hzero.iam.api.dto.LdapConnectionDTO;
import org.hzero.iam.app.service.LdapService;
import org.hzero.iam.domain.entity.Ldap;
import org.hzero.iam.domain.entity.LdapErrorUser;
import org.hzero.iam.domain.entity.LdapHistory;
import org.hzero.iam.domain.repository.LdapErrorUserRepository;
import org.hzero.iam.domain.repository.LdapHistoryRepository;
import org.hzero.iam.domain.repository.LdapRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}")
public class LdapC7nController extends BaseController {

    private final LdapService ldapService;
    private final LdapC7nService ldapC7nService;
    private final LdapRepository ldapRepository;
    private final LdapHistoryRepository ldapHistoryRepository;
    private final LdapErrorUserRepository ldapErrorUserRepository;

    public LdapC7nController(LdapService ldapService,
                             LdapC7nService ldapC7nService,
                             LdapRepository ldapRepository,
                             LdapHistoryRepository ldapHistoryRepository,
                             LdapErrorUserRepository ldapErrorUserRepository
    ) {
        this.ldapService = ldapService;
        this.ldapC7nService = ldapC7nService;
        this.ldapRepository = ldapRepository;
        this.ldapHistoryRepository = ldapHistoryRepository;
        this.ldapErrorUserRepository = ldapErrorUserRepository;
    }

    /**
     * 添加Ldap
     *
     * @param organizationId
     * @param ldap
     * @return ldapDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建Ldap")
    @PostMapping(value = "/ldaps")
    public ResponseEntity<Ldap> create(@PathVariable("organization_id") Long organizationId,
                                       @RequestBody @Validated Ldap ldap) {
        ldap.setOrganizationId(organizationId);
        this.validObject(ldap);
        return new ResponseEntity<>(ldapService.create(ldap), HttpStatus.OK);
    }

    /**
     * 更新Ldap
     *
     * @param organizationId
     * @param ldap
     * @return ldapDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改Ldap")
    @PutMapping(value = "/ldaps")
    public ResponseEntity<Void> update(@PathVariable("organization_id") Long organizationId,
                                       @RequestBody @Validated Ldap ldap) {
        ldap.setOrganizationId(organizationId);
        this.validObject(ldap);
        if (ldap.getId() == null) {
            ldapService.create(ldap);
        } else {
            SecurityTokenHelper.validToken(ldap);
            ldapService.update(ldap);
        }
        return Results.success();
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "启用ldap")
    @PutMapping(value = "/ldaps/enable")
    public ResponseEntity enableLdap(@PathVariable(name = "organization_id") Long organizationId) {
        ldapC7nService.enableLdap(organizationId);
        return Results.success();

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用ldap")
    @PutMapping(value = "/ldaps/disable")
    public ResponseEntity<Ldap> disableLdap(@PathVariable(name = "organization_id") Long organizationId) {
        ldapC7nService.disableLdap(organizationId);
        return Results.success();
    }

    /**
     * 根据组织id查询Ldap
     *
     * @param organizationId
     * @return ldapDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织下的Ldap")
    @GetMapping(value = "/ldaps")
    public ResponseEntity<Ldap> queryByOrgId(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapRepository.selectLdapByTenantId(organizationId), HttpStatus.OK);
    }

    /**
     * 根据组织id删除Ldap
     *
     * @param organizationId
     * @return ldapDTO
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "删除组织下的Ldap")
    @DeleteMapping("/ldaps")
    public ResponseEntity delete(@PathVariable("organization_id") Long organizationId) {
        Ldap ldap = new Ldap();
        ldap.setOrganizationId(organizationId);
        ldapService.delete(ldap);
        return Results.success();
    }

    /**
     * 测试ldap连接
     *
     * @return 是否连接成功
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "测试ldap连接")
    @PostMapping("/ldaps/test_connect")
    public ResponseEntity<LdapConnectionDTO> testConnect(@PathVariable("organization_id") Long organizationId,
                                                         @RequestBody LdapAccountDTO ldapAccount) {
        return new ResponseEntity<>(ldapC7nService.testConnect(organizationId, ldapAccount), HttpStatus.OK);
    }

    /**
     * 同步ldap用户
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "同步ldap用户")
    @PostMapping("/ldaps/sync_users")
    public ResponseEntity syncUsers(@PathVariable("organization_id") Long organizationId) {
        ldapC7nService.syncLdapUser(organizationId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ldap id查询最新一条历史记录")
    @GetMapping("/ldaps/latest_history")
    public ResponseEntity<LdapHistory> latestHistory(@PathVariable("organization_id") Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        return new ResponseEntity<>(ldapHistoryRepository.queryLatestHistory(ldap.getId()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ldap id查询历史记录")
    @GetMapping("/ldaps/history")
    @CustomPageRequest
    public ResponseEntity<Page<LdapHistory>> pagingQueryHistories(@ApiIgnore
                                                                  @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                  @PathVariable("organization_id") Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        return new ResponseEntity<>(ldapHistoryRepository.pageLdapHistories(pageRequest, ldap.getId()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ldap history id查询同步用户错误详情")
    @GetMapping("/ldap_histories/{id}/error_users")
    @CustomPageRequest
    public ResponseEntity<Page<LdapErrorUser>> pagingQueryErrorUsers(@ApiIgnore
                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                                     @PathVariable("organization_id") Long organizationId,
                                                                     @PathVariable Long id,
                                                                     LdapErrorUser ldapErrorUser) {
        return new ResponseEntity<>(ldapErrorUserRepository.pageLdapHistoryErrorUsers(pageRequest, id, ldapErrorUser), HttpStatus.OK);
    }


    /**
     * 用于ldap同步过程中，因为不可控因素（iam服务挂掉）导致endTime为空一直在同步中的问题，该接口只是更新下endTime
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据ldap id更新历史记录的endTime")
    @PutMapping("/ldaps/stop")
    public ResponseEntity<LdapHistory> stop(@PathVariable("organization_id") Long organizationId) {
        Ldap ldap = ldapRepository.selectLdapByTenantId(organizationId);
        ldapService.stop(ldap.getId());
        return Results.success();
    }
//
//    /**
//     * 用于创建ldap自动同步
//     *
//     * @param organizationId 组织id
//     * @return
//     */
//    @Permission(level = ResourceLevel.ORGANIZATION)
//    @ApiOperation(value = "创建ldap自动同步")
//    @PostMapping("/ldaps/auto")
//    public ResponseEntity<LdapAutoDTO> createLdapAuto(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId,
//            @ApiParam(value = "LdapAutoDTO", required = true)
//            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
//        return new ResponseEntity<>(ldapService.createLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
//    }
//
//    /**
//     * 用于更新ldap自动同步
//     *
//     * @param organizationId 组织id
//     * @return
//     */
//    @Permission(level = ResourceLevel.ORGANIZATION)
//    @ApiOperation(value = "更新编辑ldap自动同步")
//    @PutMapping("/ldaps/auto")
//    public ResponseEntity<LdapAutoDTO> updateLdapAuto(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId,
//            @ApiParam(value = "LdapAutoDTO", required = true)
//            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
//        return new ResponseEntity<>(ldapService.updateLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
//    }
//
//    @Permission(level = ResourceLevel.ORGANIZATION)
//    @ApiOperation(value = "ldap自动同步详情查询")
//    @GetMapping("/ldaps/auto/detail")
//    public ResponseEntity<LdapAutoDTO> updateLdapAutoActive(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId) {
//        return new ResponseEntity<>(ldapService.queryLdapAutoDTO(organizationId), HttpStatus.OK);
//    }
}
