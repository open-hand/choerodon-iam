package io.choerodon.base.api.controller.v1;

import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.base.infra.dto.LdapAutoDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.LdapAccountDTO;
import io.choerodon.base.api.dto.LdapConnectionDTO;
import io.choerodon.base.app.service.LdapService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.LdapDTO;
import io.choerodon.base.infra.dto.LdapErrorUserDTO;
import io.choerodon.base.infra.dto.LdapHistoryDTO;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}")
public class LdapController {

    private LdapService ldapService;

    public LdapController(LdapService ldapService) {
        this.ldapService = ldapService;
    }

    /**
     * 添加Ldap
     *
     * @param organizationId
     * @param ldapDTO
     * @return ldapDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建Ldap")
    @PostMapping(value = "/ldaps")
    public ResponseEntity<LdapDTO> create(@PathVariable("organization_id") Long organizationId,
                                          @RequestBody @Validated LdapDTO ldapDTO) {
        return new ResponseEntity<>(ldapService.create(organizationId, ldapDTO), HttpStatus.OK);
    }

    /**
     * 更新Ldap
     *
     * @param organizationId
     * @param ldapDTO
     * @return ldapDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改Ldap")
    @PutMapping(value = "/ldaps")
    public ResponseEntity<LdapDTO> update(@PathVariable("organization_id") Long organizationId,
                                          @RequestBody @Validated LdapDTO ldapDTO) {
        return new ResponseEntity<>(ldapService.update(organizationId, ldapDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "启用ldap")
    @PutMapping(value = "/ldaps/enable")
    public ResponseEntity<LdapDTO> enableLdap(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.enableLdap(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "禁用ldap")
    @PutMapping(value = "/ldaps/disable")
    public ResponseEntity<LdapDTO> disableLdap(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.disableLdap(organizationId), HttpStatus.OK);
    }

    /**
     * 根据组织id查询Ldap
     *
     * @param organizationId
     * @return ldapDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询组织下的Ldap")
    @GetMapping(value = "/ldaps")
    public ResponseEntity<LdapDTO> queryByOrgId(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.queryByOrganizationId(organizationId), HttpStatus.OK);
    }

    /**
     * 根据组织id删除Ldap
     *
     * @param organizationId
     * @return ldapDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除组织下的Ldap")
    @DeleteMapping("/ldaps")
    public ResponseEntity delete(@PathVariable("organization_id") Long organizationId) {
        ldapService.delete(organizationId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 测试ldap连接
     *
     * @return 是否连接成功
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "测试ldap连接")
    @PostMapping("/ldaps/test_connect")
    public ResponseEntity<LdapConnectionDTO> testConnect(@PathVariable("organization_id") Long organizationId,
                                                         @RequestBody LdapAccountDTO ldapAccount) {
        return new ResponseEntity<>(ldapService.testConnect(organizationId, ldapAccount), HttpStatus.OK);
    }

    /**
     * 同步ldap用户
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "同步ldap用户")
    @PostMapping("/ldaps/sync_users")
    public ResponseEntity syncUsers(@PathVariable("organization_id") Long organizationId) {
        ldapService.syncLdapUser(organizationId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据ldap id查询最新一条历史记录")
    @GetMapping("/ldaps/latest_history")
    public ResponseEntity<LdapHistoryDTO> latestHistory(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.queryLatestHistory(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据ldap id查询历史记录")
    @GetMapping("/ldaps/history")
    @CustomPageRequest
    public ResponseEntity<PageInfo<LdapHistoryDTO>> pagingQueryHistories(@ApiIgnore
                                                                         @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                         @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.pagingQueryHistories(Pageable, organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据ldap history id查询同步用户错误详情")
    @GetMapping("/ldap_histories/{id}/error_users")
    @CustomPageRequest
    public ResponseEntity<PageInfo<LdapErrorUserDTO>> pagingQueryErrorUsers(@ApiIgnore
                                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                            @PathVariable("organization_id") Long organizationId,
                                                                            @PathVariable Long id,
                                                                            LdapErrorUserDTO ldapErrorUserDTO) {
        return new ResponseEntity<>(ldapService.pagingQueryErrorUsers(Pageable, id, ldapErrorUserDTO), HttpStatus.OK);
    }


    /**
     * 用于ldap同步过程中，因为不可控因素（iam服务挂掉）导致endTime为空一直在同步中的问题，该接口只是更新下endTime
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "根据ldap id更新历史记录的endTime")
    @PutMapping("/ldaps/stop")
    public ResponseEntity<LdapHistoryDTO> stop(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.stop(organizationId), HttpStatus.OK);
    }

    /**
     * 用于创建ldap自动同步
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建ldap自动同步")
    @PostMapping("/ldaps/auto")
    public ResponseEntity<LdapAutoDTO> createLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
        return new ResponseEntity<>(ldapService.createLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
    }

    /**
     * 用于更新ldap自动同步
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "更新编辑ldap自动同步")
    @PutMapping("/ldaps/auto")
    public ResponseEntity<LdapAutoDTO> updateLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
        return new ResponseEntity<>(ldapService.updateLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "ldap自动同步详情查询")
    @GetMapping("/ldaps/auto/detail")
    public ResponseEntity<LdapAutoDTO> updateLdapAutoActive(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapService.queryLdapAutoDTO(organizationId), HttpStatus.OK);
    }
}
