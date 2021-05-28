package io.choerodon.iam.api.controller.v1;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.FixService;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/choerodon/v1/fix")
public class FixController {
    @Autowired
    private FixService fixService;

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建ldap自动同步")
    @GetMapping("/project-category")
    public ResponseEntity<Void> fixProjectCateGory() {
        fixService.fixProjectCateGory();
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建ldap自动同步")
    @GetMapping("/pinyin")
    public ResponseEntity<Void> fixRealNameToPinyin() {
        fixService.fixRealNameToPinyin();
        return Results.success();
    }

    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "创建ldap自动同步")
    @GetMapping("/pinyin-header-char")
    public ResponseEntity<Void> fixRealNameToPinyinHeaderChar() {
        fixService.fixRealNameToPinyinHeaderChar();
        return Results.success();
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation("修复菜单层级,增量修复")
    @PutMapping("/menu_level_path")
    public ResponseEntity<Void> fixMenuLevelPath() {
        fixService.fixMenuLevelPath(false);
        return ResponseEntity.noContent().build();
    }

}
