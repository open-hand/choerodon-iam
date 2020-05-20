package io.choerodon.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.iam.domain.entity.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.iam.app.service.MenuC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author scp
 * @date 2020/5/8
 * @description
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
    public ResponseEntity<List<Menu>> listNavMenuTree(@RequestParam(required = false) Long projectId,
                                                      @RequestParam(required = false) Set<String> labels) {
        return ResponseEntity.ok(menuC7nService.listNavMenuTree(labels, projectId));
    }
}
