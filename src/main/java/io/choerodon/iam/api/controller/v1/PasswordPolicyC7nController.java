package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.iam.app.service.PasswordPolicyService;
import org.hzero.iam.domain.entity.PasswordPolicy;
import org.hzero.iam.domain.repository.PasswordPolicyRepository;
import org.hzero.mybatis.helper.SecurityTokenHelper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author wuguokai
 */
@RestController
@RequestMapping("/v1/organizations/{organization_id}/password_policies")
public class PasswordPolicyC7nController extends BaseController {

    private PasswordPolicyService passwordPolicyService;
    private PasswordPolicyRepository passwordPolicyRepository;

    public PasswordPolicyC7nController(PasswordPolicyService passwordPolicyService,
                                       PasswordPolicyRepository passwordPolicyRepository) {
        this.passwordPolicyService = passwordPolicyService;
        this.passwordPolicyRepository = passwordPolicyRepository;
    }

    /**
     * 查询目标组织密码策略
     *
     * @return 目标组织密码策略
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织的密码策略")
    @GetMapping
    public ResponseEntity<PasswordPolicy> queryByOrganizationId(@PathVariable("organization_id") Long organizationId) {
        return new ResponseEntity<>(passwordPolicyRepository.selectTenantPasswordPolicy(organizationId), HttpStatus.OK);
    }

    /**
     * 更新当前选择的组织密码策略
     *
     * @param passwordPolicy 要更新的密码策略
     * @return 更新后的密码策略
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改组织的密码策略")
    @PostMapping("/{id}")
    public ResponseEntity<PasswordPolicy> update(@PathVariable("organization_id") Long organizationId,
                                                 @PathVariable("id") Long id,
                                                 @RequestBody @Validated PasswordPolicy passwordPolicy) {
        passwordPolicy.setOrganizationId(organizationId);
        this.validObject(passwordPolicy);
        if (passwordPolicy.getId() == null) {
            return new ResponseEntity<>(passwordPolicyService.createPasswordPolicy(organizationId, passwordPolicy), HttpStatus.OK);
        } else {
            SecurityTokenHelper.validToken(passwordPolicy);
            return new ResponseEntity<>(passwordPolicyService.updatePasswordPolicy(organizationId, passwordPolicy), HttpStatus.OK);
        }
    }

}
