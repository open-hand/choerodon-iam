package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.base.BaseController;
import io.choerodon.iam.app.service.ProjectCategoryC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author flyleft
 * @author superlee
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_ORGANIZATION_PROJECT_CATEGORY)
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/project_categories")
public class OrganizationProjectCategoryC7nController extends BaseController {

    private ProjectCategoryC7nService projectCategoryC7nService;

    public OrganizationProjectCategoryC7nController(ProjectCategoryC7nService projectCategoryC7nService) {
        this.projectCategoryC7nService = projectCategoryC7nService;
    }

    @Permission(permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<ProjectCategoryDTO>> list(
            @PathVariable(value = "organization_id") Long organizationId) {
        return ResponseEntity.ok(projectCategoryC7nService.list());
    }

}
