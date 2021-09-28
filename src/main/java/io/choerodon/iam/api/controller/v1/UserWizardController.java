package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.UserWizardStatusVO;
import io.choerodon.iam.app.service.UserWizardService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.UserWizardDTO;
import io.choerodon.swagger.annotation.Permission;

/**
 * @Author: scp
 * @Description: 用户向导相关接口
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_USER_WIZARD)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/user_wizard")
public class UserWizardController {
    @Autowired
    private UserWizardService userWizardService;

    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询所有用户向导步骤")
    @GetMapping(value = "/list")
    public ResponseEntity<List<UserWizardDTO>> listUserWizards(@PathVariable("organization_id") Long organizationId) {
        return Results.success(userWizardService.listUserWizards(organizationId));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织下向导完成情况")
    @GetMapping(value = "/list_status")
    public ResponseEntity<List<UserWizardStatusVO>> listUserWizardsStatus(@PathVariable("organization_id") Long organizationId) {
        return Results.success(userWizardService.listUserWizardsStatus(organizationId));
    }

}
