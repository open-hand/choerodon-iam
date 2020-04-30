package io.choerodon.base.api.controller.v1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.base.app.service.ProjectCategoryService;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;

/**
 * @author flyleft
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/project_categories")
public class OrganizationProjectCategoryController extends BaseController {

    private ProjectCategoryService projectCategoryService;

    public OrganizationProjectCategoryController(ProjectCategoryService projectCategoryService) {
        this.projectCategoryService = projectCategoryService;
    }


    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @GetMapping
    public ResponseEntity<List<ProjectCategoryDTO>> list(
            @PathVariable(value = "organization_id") Long organizationId) {

        return ResponseEntity.ok(projectCategoryService.list());
    }

}
