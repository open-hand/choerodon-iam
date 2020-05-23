package io.choerodon.iam.api.controller.v1;


import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.LdapC7nService;
import io.choerodon.iam.infra.dto.LdapAutoDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.hzero.core.base.BaseController;
import org.hzero.iam.config.SwaggerApiConfig;
import org.hzero.iam.domain.entity.Ldap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author wuguokai
 */
@Api(tags = SwaggerApiConfig.LDAP)
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}")
public class LdapC7nController extends BaseController {

    @Autowired
    private LdapC7nService ldapC7nService;

    /**
     * 用于创建ldap自动同步
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建ldap自动同步")
    @PostMapping("/ldaps/auto")
    public ResponseEntity<LdapAutoDTO> createLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
        return new ResponseEntity<>(ldapC7nService.createLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
    }

    /**
     * 用于更新ldap自动同步
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "更新编辑ldap自动同步")
    @PutMapping("/ldaps/auto")
    public ResponseEntity<LdapAutoDTO> updateLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid LdapAutoDTO ldapAutoDTO) {
        return new ResponseEntity<>(ldapC7nService.updateLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "ldap自动同步详情查询")
    @GetMapping("/ldaps/auto/detail")
    public ResponseEntity<LdapAutoDTO> updateLdapAutoActive(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapC7nService.queryLdapAutoDTO(organizationId), HttpStatus.OK);
    }
}
