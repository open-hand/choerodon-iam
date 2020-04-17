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
}
