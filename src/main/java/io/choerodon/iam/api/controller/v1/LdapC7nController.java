package io.choerodon.iam.api.controller.v1;


import org.hzero.core.base.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}")
public class LdapC7nController extends BaseController {
    // todo 等待hzero自动同步功能
//    /**
//     * 用于创建ldap自动同步
//     *
//     * @param organizationId 组织id
//     * @return
//     */
//    @Permission(level = ResourceLevel.ORGANIZATION)
//    @ApiOperation(value = "创建ldap自动同步")
//    @PostMapping("/ldaps/auto")
//    public ResponseEntity<Ldap> createLdapAuto(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId,
//            @ApiParam(value = "LdapAutoDTO", required = true)
//            @RequestBody @Valid Ldap ldapAutoDTO) {
//        return new ResponseEntity<>(ldapC7nService.createLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
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
//    public ResponseEntity<Ldap> updateLdapAuto(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId,
//            @ApiParam(value = "LdapAutoDTO", required = true)
//            @RequestBody @Valid Ldap ldapAutoDTO) {
//        return new ResponseEntity<>(ldapC7nService.updateLdapAuto(organizationId, ldapAutoDTO), HttpStatus.OK);
//    }
//
//    @Permission(level = ResourceLevel.ORGANIZATION)
//    @ApiOperation(value = "ldap自动同步详情查询")
//    @GetMapping("/ldaps/auto/detail")
//    public ResponseEntity<Ldap> updateLdapAutoActive(
//            @ApiParam(value = "组织Id", required = true)
//            @PathVariable("organization_id") Long organizationId) {
//        return new ResponseEntity<>(ldapC7nService.queryLdapAutoDTO(organizationId), HttpStatus.OK);
//    }
}
