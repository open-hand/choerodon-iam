package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
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

import io.choerodon.base.app.service.*;
import io.choerodon.base.infra.dto.*;
import io.choerodon.core.annotation.*;
import io.choerodon.core.base.*;
import io.choerodon.core.enums.*;
import io.choerodon.core.exception.*;
import io.choerodon.swagger.annotation.*;


/**
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/lookups")
public class LookupController extends BaseController {

    private LookupService lookupService;

    public LookupController(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * 创建lookupCode
     *
     * @param lookupDTO 需要创建的lookupDTO对象
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "创建快码")
    @PostMapping
    public ResponseEntity<LookupDTO> create(@RequestBody @Validated LookupDTO lookupDTO, BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        return new ResponseEntity<>(lookupService.create(lookupDTO), HttpStatus.OK);
    }

    /**
     * 删除lookupType
     *
     * @param id lookup id
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "删除快码")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        lookupService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "修改快码")
    @PutMapping(value = "/{id}")
    public ResponseEntity<LookupDTO> update(@PathVariable Long id, @RequestBody @Validated LookupDTO lookupDTO,
                                            BindingResult result) {
        if (result.hasErrors()) {
            throw new CommonException(result.getAllErrors().get(0).getDefaultMessage());
        }
        lookupDTO.setId(id);
        return new ResponseEntity<>(lookupService.update(lookupDTO), HttpStatus.OK);
    }

    /**
     * 分页查询lookupType 数据
     *
     * @param Pageable
     * @param code
     * @param description
     * @param params
     * @return
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询快码")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<LookupDTO>> list(
            @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
            @RequestParam(required = false) String code, @RequestParam(required = false) String description,
            @RequestParam(required = false) String params) {
        return new ResponseEntity<>(lookupService.pagingQuery(Pageable, code, description, params), HttpStatus.OK);
    }

    /**
     * 查看lookupCode
     *
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过 id 查询快码")
    @GetMapping(value = "/{id}")
    public ResponseEntity<LookupDTO> queryById(@PathVariable Long id) {
        return new ResponseEntity<>(lookupService.queryById(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过code查询快码")
    @GetMapping(value = "/code")
    public ResponseEntity<LookupDTO> queryByCode(@RequestParam String code) {
        return new ResponseEntity<>(lookupService.queryByCode(code), HttpStatus.OK);
    }

    /**
     * 校验 code || code + lookupId 是否已存在
     *
     * @param lookupId 传入 lookupId 则校验 lookupValue 表，即 code + lookupId
     * @param code 默认校验 lookup 表
     * @return
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "校验 code 是否存在")
    @GetMapping(value = "/check")
    public ResponseEntity check(@RequestParam(required = false) Long lookupId, @RequestParam String code) {
        lookupService.check(lookupId, code);
        return new ResponseEntity(HttpStatus.OK);
    }
}
