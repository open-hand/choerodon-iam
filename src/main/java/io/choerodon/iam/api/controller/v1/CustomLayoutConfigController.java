package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.iam.app.service.CustomLayoutConfigService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.CustomLayoutConfigDTO;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈自定义工作台、项目概览组件配置控制器〉
 *
 * @author wanghao
 * @since 2021/1/5 17:52
 */
@RestController
@Api(tags = C7nSwaggerApiConfig.CHOERODON_CUSTOM_LAYOUT_CONFIG)
@RequestMapping("/choerodon/v1")
public class CustomLayoutConfigController {

    @Autowired
    private CustomLayoutConfigService customLayoutConfigService;


    @Permission(permissionLogin = true)
    @ApiOperation(value = "保存个人工作台")
    @PostMapping(value = "/workbench_configs")
    public ResponseEntity<CustomLayoutConfigDTO> saveCustomWorkBeachConfig(
            @RequestBody CustomLayoutConfigDTO customLayoutConfigDTO;
    ) {
        return ResponseEntity.ok(customLayoutConfigService.saveCustomWorkBeachConfig(customLayoutConfigDTO));
    }
}
