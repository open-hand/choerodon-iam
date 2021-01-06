package io.choerodon.iam.api.controller.v1;

import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.choerodon.iam.api.vo.EnterpriseInfoVO;
import io.choerodon.iam.app.service.EnterpriseInfoService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:09
 */
@RestController
@RequestMapping("/choerodon/v1/enterprises")
public class EnterpriseInfoController {

    @Autowired
    private EnterpriseInfoService enterpriseInfoService;

    @Permission(permissionLogin = true)
    @ApiOperation(value = "校验企业信息是否完善")
    @GetMapping("/default")
    public ResponseEntity<Boolean> checkEnterpriseInfoComplete() {
        return ResponseEntity.ok(enterpriseInfoService.checkEnterpriseInfoComplete());
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "完善企业信息")
    @PutMapping
    public ResponseEntity<Void> updateEnterpriseInfo(@RequestBody @Validated EnterpriseInfoVO enterpriseInfoVO) {
        enterpriseInfoService.saveEnterpriseInfo(enterpriseInfoVO);
        return ResponseEntity.noContent().build();
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "校验是否可以修改组织编码（组织下创建了项目则不能修改）")
    @GetMapping("/default/check_enable_update_tenant_num")
    public ResponseEntity<Boolean> checkEnableUpdateTenantNum() {
        return ResponseEntity.ok(enterpriseInfoService.checkEnableUpdateTenantNum());
    }

}
