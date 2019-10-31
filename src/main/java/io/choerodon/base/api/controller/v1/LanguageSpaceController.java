package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.LdapAccountDTO;
import io.choerodon.base.api.dto.LdapConnectionDTO;
import io.choerodon.base.app.service.LanguageSpaceService;
import io.choerodon.base.app.service.LdapService;
import io.choerodon.base.infra.dto.LanguageSpaceDTO;
import io.choerodon.base.infra.dto.LdapDTO;
import io.choerodon.base.infra.dto.LdapErrorUserDTO;
import io.choerodon.base.infra.dto.LdapHistoryDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author wkj
 */
@RestController
@RequestMapping("/v1/language_space")
public class LanguageSpaceController {

    private LanguageSpaceService languageSpaceService;

    public LanguageSpaceController(LanguageSpaceService languageSpaceService) {
        this.languageSpaceService = languageSpaceService;
    }

    /**
     * 添加多语言映射
     *
     * @param languageSpaceDTO
     * @return LanguageSpaceDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "创建多语言映射")
    @PostMapping
    public ResponseEntity<LanguageSpaceDTO> create(@RequestBody @Validated LanguageSpaceDTO languageSpaceDTO) {
        return new ResponseEntity<>(languageSpaceService.create(languageSpaceDTO), HttpStatus.OK);
    }

    /**
     * 更新多语言映射
     *
     * @param id
     * @param languageSpaceDTO
     * @return LanguageSpaceDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "修改多语言映射")
    @PutMapping(value = "/language/{id}")
    public ResponseEntity<LanguageSpaceDTO> update(@PathVariable("id") Long id, @RequestBody LanguageSpaceDTO languageSpaceDTO) {
        return new ResponseEntity<>(languageSpaceService.update(id, languageSpaceDTO), HttpStatus.OK);
    }


    /**
     * 分页查询多语言映射表
     *
     * @param languageSpaceDTO
     * @return LanguageSpaceDTO
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "查询多语言映射")
    @GetMapping(value = "/language")
    @CustomPageRequest
    public ResponseEntity<PageInfo<LanguageSpaceDTO>> pagingQueryByOption(@RequestBody LanguageSpaceDTO languageSpaceDTO,
                                                                   @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.ASC) PageRequest pageRequest,
                                                                   @RequestParam(required = false) String param) {
        return new ResponseEntity<>(languageSpaceService.queryByOptions(languageSpaceDTO, pageRequest, param), HttpStatus.OK);
    }

    /**
     * 根据id删除对应的多语言映射
     *
     * @param id
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除对应的多语言映射关系")
    @DeleteMapping("/language/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        languageSpaceService.delete(id);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 根据id查询多语言映射关系
     *
     * @param id
     */
    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "删除对应的多语言映射关系")
    @GetMapping("/language/{id}")
    public ResponseEntity<LanguageSpaceDTO> queryById(@PathVariable("id") Long id) {
        return new ResponseEntity(languageSpaceService.queryById(id), HttpStatus.OK);
    }

}
