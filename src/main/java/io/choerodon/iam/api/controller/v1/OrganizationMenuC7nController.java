package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.hzero.iam.domain.entity.Menu;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.swagger.annotation.Permission;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 17:33
 */
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/menus")
public class OrganizationMenuC7nController {

    private MenuC7nService menuC7nService;

    public OrganizationMenuC7nController(MenuC7nService menuC7nService) {
        this.menuC7nService = menuC7nService;
    }

    @ApiOperation(value = "权限分配 - 查询组织下可分配的权限集树")
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{menu_level}/permission-set-tree")
    public ResponseEntity<List<Menu>> listPermissionSetTree(@PathVariable(value = "organization_id") Long organizationId,
                                                            @PathVariable(value = "menu_level") String menuLevel) {
        return ResponseEntity.ok(menuC7nService.listPermissionSetTree(organizationId, menuLevel));
    }
}
