package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.base.api.validator.Check;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.vo.ProjectCategoryEDTO;
import io.choerodon.base.app.service.ProjectCategoryService;
import io.choerodon.base.infra.dto.MenuDTO;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.base.infra.enums.CategoryResourceLevel;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author jiameng.cao
 * @date 2019/6/3
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/categories")
public class ProjectCategoryOrgController extends BaseController {
    private ProjectCategoryService projectCategoryService;

    public ProjectCategoryOrgController(ProjectCategoryService projectCategoryService) {
        this.projectCategoryService = projectCategoryService;
    }

    @GetMapping
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "分页查询平台下组织的项目类型")
    public ResponseEntity<PageInfo<ProjectCategoryDTO>> getProjectCategoriesByOrgId(@PathVariable(name = "organization_id") Long organizationId,
                                                                                    @RequestParam(defaultValue = "1", required = false) final int page,
                                                                                    @RequestParam(defaultValue = "20", required = false) final int size,
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
        return new ResponseEntity<>(projectCategoryService.getCategoriesByOrgId(organizationId, page, size, param, projectCategoryDTO), HttpStatus.OK);
    }

    @PostMapping(value = "/create")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "平台下组织的项目类型创建")
    public ResponseEntity<ProjectCategoryEDTO> createProjectCategory(@PathVariable(name = "organization_id") Long organizationId,
                                                                     @RequestBody @Validated({Insert.class}) ProjectCategoryEDTO createDTO) {
        if (createDTO.getId() != null || createDTO.getObjectVersionNumber() != null) {
            throw new CommonException("error.projectCategory.create.id.and.objectVersionNumber.must.be.null");
        }
        projectCategoryService.checkProCategory(createDTO);
        createDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(projectCategoryService.createProjectCategory(createDTO, CategoryResourceLevel.PROJECT.value()), HttpStatus.OK);
    }

    @PostMapping(value = "/check")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "组织下的项目类型创建校验")
    public ResponseEntity checkProjectCategory(@RequestBody @Validated({Check.class}) ProjectCategoryEDTO createDTO) {
        projectCategoryService.checkProCategory(createDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "平台下组织的项目类型删除")
    public ResponseEntity<Boolean> deleteProjectCategory(@PathVariable(name = "organization_id") Long organizationId,
                                                         @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(projectCategoryService.deleteProjectCategory(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "平台下组织的项目类型更新")
    public ResponseEntity<ProjectCategoryEDTO> updateProjectCategory(@PathVariable(name = "id") Long id,
                                                                     @RequestBody @NotNull @Valid ProjectCategoryEDTO updateDTO) {
        if (!id.equals(updateDTO.getId())) {
            throw new CommonException("error.update.primary.key.not.equal.to.the.path.id");
        }
        return new ResponseEntity<>(projectCategoryService.updateProjectCategory(updateDTO), HttpStatus.OK);
    }

    @GetMapping(value = "/menu")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "获取平台下组织的项目类型菜单")
    public ResponseEntity<MenuDTO> getProjectCategoryMenu(@PathVariable(name = "organization_id") Long organizationId,
                                                          @RequestParam String code) {
        return new ResponseEntity<>(projectCategoryService.getProjectCategoryMenu(organizationId, code), HttpStatus.OK);
    }

    @GetMapping(value = "/list")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "获取平台下组织的项目类型和平台下项目类型")
    public ResponseEntity<PageInfo<ProjectCategoryDTO>> getProjectCategoryList(@PathVariable(name = "organization_id") Long organizationId,
                                                                               @RequestParam(defaultValue = "1", required = false) final int page,
                                                                               @RequestParam(defaultValue = "20", required = false) final int size,
                                                                               @RequestParam(required = false) final String param) {
        return new ResponseEntity<>(projectCategoryService.pagingProjectCategoryList(organizationId, page, size, param), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @CustomPageRequest
    @ApiOperation(value = "查询组织下具体的项目类型")
    public ResponseEntity<ProjectCategoryEDTO> getOrgProjectCategoriesById(@PathVariable(name = "organization_id") Long organizationId,
                                                                           @PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(projectCategoryService.getProCategoriesById(id), HttpStatus.OK);
    }

}
