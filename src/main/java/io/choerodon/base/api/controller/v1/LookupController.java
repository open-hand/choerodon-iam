package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.base.app.service.LookupService;
import io.choerodon.base.infra.dto.LookupDTO;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;


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
    public ResponseEntity<LookupDTO> create(@RequestBody @Valid LookupDTO lookupDTO) {
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
    public ResponseEntity<LookupDTO> update(@PathVariable Long id,
                                            @RequestBody @Valid LookupDTO lookupDTO) {
        lookupDTO.setId(id);
        return new ResponseEntity<>(lookupService.update(lookupDTO), HttpStatus.OK);
    }

    /**
     * 分页查询lookupType 数据
     *
     * @param lookupDTO 查询封装对象
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "分页查询快码")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<LookupDTO>> list(@ApiIgnore
                                                    @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                    @RequestBody(required = false) LookupDTO lookupDTO,
                                                    @RequestParam(required = false) String param) {
        return new ResponseEntity<>(lookupService.pagingQuery(Pageable, lookupDTO, param), HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过code查询快码")
    @GetMapping(value = "/code")
    public ResponseEntity<LookupDTO> queryByCode(@RequestParam String code) {
        return new ResponseEntity<>(lookupService.queryByCode(code), HttpStatus.OK);
    }

    /**
     * 根据描述查询 Lookup
     *
     * @return 返回信息
     */
    @Permission(type = ResourceType.SITE)
    @ApiOperation(value = "通过描述查询快码")
    @GetMapping(value = "/description")
    public ResponseEntity<LookupDTO> queryByDes(@RequestParam String des) {
        return new ResponseEntity<>(lookupService.queryByDes(des), HttpStatus.OK);
    }

    /**
     * 校验 code || code + lookupId 是否已存在
     * @param lookupId 传入 lookupId 则校验 lookupValue 表，即 code + lookupId
     * @param code 默认校验 lookup 表
     * @return
     */
    @Permission(type = ResourceType.SITE)
    @ApiModelProperty(value = "校验 code 是否存在")
    @GetMapping(value = "/check")
    public ResponseEntity check(@RequestParam(required = false) Long lookupId, @RequestParam String code) {
        lookupService.check(lookupId, code);
        return new ResponseEntity(HttpStatus.OK);
    }
}