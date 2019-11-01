package io.choerodon.base.api.controller.v1;

import io.choerodon.base.infra.dto.*;
import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.LovService;
import io.choerodon.core.enums.ResourceType;

import com.github.pagehelper.PageInfo;
import com.sun.org.apache.regexp.internal.RE;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.*;

@RestController
@RequestMapping(value = "/v1/lov")
public class LovController {
    private LovService lovService;

    public LovController(LovService lovService) {
        this.lovService = lovService;
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过code查询LOV")
    @GetMapping(value = "/code")
    public ResponseEntity<LovDTO> queryByCode(@RequestParam(name = "code") String code) {
        return new ResponseEntity<>(lovService.queryLovByCode(code), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建 Lov")
    @PostMapping
    public ResponseEntity<LovDTO> createLov(@RequestBody @Valid LovDTO lovDTO) {
        return new ResponseEntity<>(lovService.createLov(lovDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过层级查询 api")
    @GetMapping("/api")
    public ResponseEntity<PageInfo<PermissionDTO>> queryApiByLevel(@RequestParam String level,
                                                                   @ApiIgnore @PageableDefault(size = 20) @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(lovService.queryApiByLevel(pageable, level), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "查询 lov 列表")
    @GetMapping("/list")
    public ResponseEntity<PageInfo<LovDTO>> queryLovList(@ApiIgnore @PageableDefault(size = 20) @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                         @RequestParam(required = false) String code,
                                                         @RequestParam(required = false) String description,
                                                         @RequestParam(required = false) String level,
                                                         @RequestParam(required = false) String param) {
        return new ResponseEntity<>(lovService.queryLovList(pageable, code, description, level, param), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改lov")
    @PutMapping("/update/{id}")
    public ResponseEntity<LovDTO> updateLov(@PathVariable Long id, @RequestBody @Valid LovDTO lovDTO) {
        return new ResponseEntity<>(lovService.updateLov(id, lovDTO), HttpStatus.OK);
    }


    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "删除 lov")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity deleteLov(@PathVariable Long id) {
        lovService.deleteLov(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
