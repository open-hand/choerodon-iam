package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.vo.OrganizationCategoryEDTO;
import io.choerodon.base.app.service.OrganizationCategoryService;
import io.choerodon.base.infra.dto.OrganizationCategoryDTO;
import io.choerodon.base.infra.enums.CategoryResourceLevel;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


/**
 * @author jiameng.cao
 * @date 2019/6/5
 */
@RestController
@RequestMapping(value = "/v1/categories/org")
public class OrganizationCategoryController {

    private OrganizationCategoryService organizationCategoryService;

    public OrganizationCategoryController(OrganizationCategoryService organizationCategoryService) {
        this.organizationCategoryService = organizationCategoryService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "分页查询平台下的组织类型")
    public ResponseEntity<PageInfo<OrganizationCategoryDTO>> getOrganizationCategories(@ApiIgnore
                                                                                       @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                       @RequestParam(required = false) String name,
                                                                                       @RequestParam(required = false) String description,
                                                                                       @RequestParam(required = false) String code,
                                                                                       @RequestParam(required = false) final String param) {
        OrganizationCategoryDTO organizationCategoryDTO = new OrganizationCategoryDTO();
        if (!StringUtils.isEmpty(name)) {
            organizationCategoryDTO.setName(name);
        }
        if (!StringUtils.isEmpty(code)) {
            organizationCategoryDTO.setCode(code);
        }
        if (!StringUtils.isEmpty(description)) {
            organizationCategoryDTO.setDescription(description);
        }
        return new ResponseEntity<>(organizationCategoryService.getOrgCategories(Pageable, param, organizationCategoryDTO), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "查询平台下具体的组织类型")
    public ResponseEntity<OrganizationCategoryEDTO> getOrganizationCategoriesById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(organizationCategoryService.getOrgCategoriesById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下的组织类型创建")
    public ResponseEntity<OrganizationCategoryEDTO> createOrgCategory(@RequestBody @Validated({Insert.class}) OrganizationCategoryEDTO createDTO) {
        if (createDTO.getId() != null || createDTO.getObjectVersionNumber() != null) {
            throw new CommonException("error.orgCategory.create.id.and.objectVersionNumber.must.be.null");
        }
        organizationCategoryService.checkOrgCategory(createDTO);
        return new ResponseEntity<>(organizationCategoryService.createOrgCategory(createDTO, CategoryResourceLevel.ORGANIZATION_PROJECT.value(), CategoryResourceLevel.ORGANIZATION.value()), HttpStatus.OK);
    }

    @PostMapping(value = "/check")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下的组织类型创建校验")
    public ResponseEntity checkOrgCategory(@RequestBody @Validated({Check.class}) OrganizationCategoryEDTO createDTO) {
        organizationCategoryService.checkOrgCategory(createDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下组织类型删除")
    public ResponseEntity<Boolean> deleteOrgCategory(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(organizationCategoryService.deleteOrgCategory(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下组织类型更新")
    public ResponseEntity<OrganizationCategoryEDTO> updateOrgCategory(@PathVariable(name = "id") Long id,
                                                                      @RequestBody @NotNull @Valid OrganizationCategoryEDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            throw new CommonException("error.update.primary.key.not.equal.to.the.path.id");
        }
        return new ResponseEntity<>(organizationCategoryService.updateOrgCategory(updateDTO), HttpStatus.OK);
    }
}
