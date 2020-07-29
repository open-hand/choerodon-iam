package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.iam.domain.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * c7n的菜单控制器
 *
 * @author scp
 * @since 2020/5/8
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_MENU)
@RestController
@RequestMapping("/choerodon/v1")
public class MenuC7nController {

    @Autowired
    private MenuC7nService menuC7nService;


    @ApiOperation(value = "权限分配 - 查询组织下可分配的权限集树")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/menu")
    public ResponseEntity<List<Menu>> listNavMenuTree(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Set<String> labels) {
        return ResponseEntity.ok(menuC7nService.listNavMenuTree(labels, projectId));
    }

    @ApiOperation(value = "根据标签查询菜单")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/menus/flat")
    public ResponseEntity<List<Menu>> listMenuByLabel(@RequestParam(required = false) Set<String> labels) {
        return ResponseEntity.ok(menuC7nService.listMenuByLabel(labels));
    }

    @ApiOperation(value = "根据层级查询菜单")
    @Permission(level = ResourceLevel.SITE)
    @GetMapping(value = "/menus/menu_config")
    public ResponseEntity<List<Menu>> listMenuByLevelCode(@RequestParam(value = "code") String code) {
        return ResponseEntity.ok(menuC7nService.listMenuByLevel(code));
    }

    @ApiOperation(value = "判断用户是否能够看到平台层菜单")
    @Permission(permissionLogin = true)
    @GetMapping(value = "/menus/site_menu_flag")
    public ResponseEntity<Boolean> hasSiteMenuPermission() {
        return ResponseEntity.ok(menuC7nService.hasSiteMenuPermission());
    }
}
