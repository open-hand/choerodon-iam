package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.app.service.LovService;
import io.choerodon.base.infra.dto.LovDTO;
import io.choerodon.base.infra.dto.PermissionDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

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
    @ApiOperation(value = "通过ID查询LOV")
    @GetMapping(value = "/{id}")
    public ResponseEntity<LovDTO> queryById(@PathVariable Long id) {
        return new ResponseEntity<>(lovService.queryLovById(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建 Lov")
    @PostMapping
    public ResponseEntity<LovDTO> createLov(@RequestBody @Validated LovDTO lovDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(lovService.createLov(lovDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过层级查询 api")
    @GetMapping("/api")
    public ResponseEntity<PageInfo<List<PermissionDTO>>> queryApiByLevel(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                         @RequestParam String level,
                                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(lovService.queryApiByLevel(pageable, level, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "查询 lov 列表")
    @GetMapping("/list")
    public ResponseEntity<PageInfo<LovDTO>> queryLovList(@ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                         @RequestParam(required = false) String code,
                                                         @RequestParam(required = false) String description,
                                                         @RequestParam(required = false) String level,
                                                         @RequestParam(required = false) String params) {
        return new ResponseEntity<>(lovService.queryLovList(pageable, code, description, level, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改lov")
    @PutMapping("/{id}")
    public ResponseEntity<LovDTO> updateLov(@PathVariable Long id, @RequestBody @Validated LovDTO lovDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(lovService.updateLov(id, lovDTO), HttpStatus.OK);
    }


    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "删除 lov")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteLov(@PathVariable Long id) {
        lovService.deleteLov(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
