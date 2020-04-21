package io.choerodon.iam.api.controller.v1;


import io.choerodon.swagger.annotation.Permission;
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

import javax.validation.Valid;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}")
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
     * 用于创建ldap自动同步
     *
     * @param organizationId 组织id
     * @return
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "创建ldap自动同步")
    @PostMapping("/ldaps/auto")
    public ResponseEntity<Ldap> createLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid Ldap ldapAutoDTO) {
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
    public ResponseEntity<Ldap> updateLdapAuto(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId,
            @ApiParam(value = "LdapAutoDTO", required = true)
            @RequestBody @Valid Ldap ldapAutoDTO) {
        return new ResponseEntity<>(ldapC7nService.updateLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "ldap自动同步详情查询")
    @GetMapping("/ldaps/auto/detail")
    public ResponseEntity<Ldap> updateLdapAutoActive(
            @ApiParam(value = "组织Id", required = true)
            @PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(ldapC7nService.queryLdapAutoDTO(organizationId), HttpStatus.OK);
    }
}
