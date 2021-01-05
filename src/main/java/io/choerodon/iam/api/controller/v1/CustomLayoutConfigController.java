package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.iam.ResourceLevel;
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


    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "保存个人工作台")
    @PostMapping(value = "/workbench_configs")
    public ResponseEntity<CustomLayoutConfigDTO> saveOrUpdateCustomWorkBeachConfig(
            @RequestBody CustomLayoutConfigDTO customLayoutConfigDTO) {
        return ResponseEntity.ok(customLayoutConfigService.saveOrUpdateCustomWorkBeachConfig(customLayoutConfigDTO));
    }
    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询个人工作台")
    @GetMapping(value = "/workbench_configs/self")
    public ResponseEntity<CustomLayoutConfigDTO> queryCustomWorkBeachConfig() {
        return ResponseEntity.ok(customLayoutConfigService.queryCustomWorkBeachConfig());
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "保存项目概览")
    @PostMapping(value = "/projects/{project_id}/project_overview_config")
    public ResponseEntity<CustomLayoutConfigDTO> saveOrUpdateCustomProjectOverview(
            @PathVariable(value = "project_id") Long projectId,
            @RequestBody CustomLayoutConfigDTO customLayoutConfigDTO) {
        return ResponseEntity.ok(customLayoutConfigService.saveOrUpdateCustomProjectOverview(projectId, customLayoutConfigDTO));
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目概览")
    @GetMapping(value = "/projects/{project_id}/project_overview_config")
    public ResponseEntity<CustomLayoutConfigDTO> queryCustomProjectOverview(
            @PathVariable(value = "project_id") Long projectId) {
        return ResponseEntity.ok(customLayoutConfigService.queryCustomProjectOverview(projectId));
    }
}
