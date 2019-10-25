package io.choerodon.base.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.MenuService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.MenuDTO;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;

/**
 * @author wuguokai
 * @author superlee
 */
@RestController
@RequestMapping("/v1/menus")
public class MenuController extends BaseController {

    private static final String ORG_TOP_MENU_CODE = "choerodon.code.top.organization";
    private static final String PROJ_TOP_MENU_CODE = "choerodon.code.top.project";

    private MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation("根据id查询目录")
    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> query(@PathVariable("id") Long id) {
        return new ResponseEntity<>(menuService.query(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation("获取可以访问的菜单列表")
    @GetMapping
    public ResponseEntity<MenuDTO> menus(@RequestParam String code,
                                         @RequestParam(name = "source_id") Long sourceId) {
        return new ResponseEntity<>(menuService.menus(code, sourceId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation("菜单配置界面根据层级查菜单")
    @GetMapping("/menu_config")
    public ResponseEntity<MenuDTO> menuConfig(@RequestParam String code) {
        return new ResponseEntity<>(menuService.menuConfig(code), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation("查询组织层菜单")
    @GetMapping("/org/{organization_id}/menu_config")
    public ResponseEntity<MenuDTO> orgMenuConfig(@PathVariable(name = "organization_id") Long organizationId,
                                                 @RequestParam String code) {
        if (!(PROJ_TOP_MENU_CODE.equalsIgnoreCase(code) || ORG_TOP_MENU_CODE.equalsIgnoreCase(code))) {
            throw new CommonException("error.menu.code.cannot.query");
        }
        return new ResponseEntity<>(menuService.menuConfig(code), HttpStatus.OK);
    }


    @Permission(type = ResourceType.SITE)
    @ApiOperation("菜单配置保存")
    @PostMapping("/menu_config")
    public ResponseEntity saveMenuConfig(@RequestParam String code, @RequestBody List<MenuDTO> menus) {
        menuService.saveMenuConfig(code, menus);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "菜单code是否重复")
    @PostMapping(value = "/check")
    public ResponseEntity check(@RequestBody MenuDTO menu) {
        menuService.check(menu);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, permissionWithin = true)
    @ApiOperation(value = "查询所有菜单")
    @GetMapping(value = "/list")
    public ResponseEntity<List<MenuDTO>> list() {
        return new ResponseEntity<>(menuService.list(), HttpStatus.OK);
    }
}
