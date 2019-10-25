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
import io.choerodon.base.api.vo.ProjectCategoryEDTO;
import io.choerodon.base.app.service.ProjectCategoryService;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.base.infra.enums.CategoryResourceLevel;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author jiameng.cao
 * @date 2019/6/4
 */
@RestController
@RequestMapping(value = "/v1/categories/pro")
public class ProjectCategoryController {
    private ProjectCategoryService projectCategoryService;

    public ProjectCategoryController(ProjectCategoryService projectCategoryService) {
        this.projectCategoryService = projectCategoryService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "分页查询平台下的项目类型")
    public ResponseEntity<PageInfo<ProjectCategoryDTO>> getProjectCategories(@ApiIgnore
                                                                             @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                             @RequestParam(required = false) String name,
                                                                             @RequestParam(required = false) String description,
                                                                             @RequestParam(required = false) String code,
                                                                             @RequestParam(required = false) final String param) {
        ProjectCategoryDTO projectCategoryDTO = new ProjectCategoryDTO();
        if (!StringUtils.isEmpty(name)) {
            projectCategoryDTO.setName(name);
        }
        if (!StringUtils.isEmpty(code)) {
            projectCategoryDTO.setCode(code);
        }
        if (!StringUtils.isEmpty(description)) {
            projectCategoryDTO.setDescription(description);
        }
        return new ResponseEntity<>(projectCategoryService.getCategories(Pageable, param, projectCategoryDTO), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "查询平台下具体的项目类型")
    public ResponseEntity<ProjectCategoryEDTO> getProjectCategoriesById(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(projectCategoryService.getProCategoriesById(id), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下的项目类型创建")
    public ResponseEntity<ProjectCategoryEDTO> createProjectCategory(@RequestBody @Validated({Insert.class}) ProjectCategoryEDTO createDTO) {
        if (createDTO.getId() != null || createDTO.getObjectVersionNumber() != null) {
            throw new CommonException("error.projectCategory.create.id.and.objectVersionNumber.must.be.null");
        }
        projectCategoryService.checkProCategory(createDTO);
        return new ResponseEntity<>(projectCategoryService.createProjectCategory(createDTO, CategoryResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @PostMapping(value = "/check")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下的项目类型创建校验")
    public ResponseEntity checkProjectCategory(@RequestBody @Validated({Check.class}) ProjectCategoryEDTO createDTO) {
        projectCategoryService.checkProCategory(createDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下项目类型删除")
    public ResponseEntity<Boolean> deleteProjectCategory(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(projectCategoryService.deleteProjectCategory(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "平台下项目类型更新")
    public ResponseEntity<ProjectCategoryEDTO> updateProjectCategory(@PathVariable(name = "id") Long id,
                                                                     @RequestBody @NotNull @Valid ProjectCategoryEDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            throw new CommonException("error.update.primary.key.not.equal.to.the.path.id");
        }
        return new ResponseEntity<>(projectCategoryService.updateProjectCategory(updateDTO), HttpStatus.OK);
    }

}
