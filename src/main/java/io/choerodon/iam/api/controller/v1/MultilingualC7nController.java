package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.swagger.annotation.Permission;

@Api(tags = C7nSwaggerApiConfig.CHOERODON_DATA_FIX)
@RestController
@RequestMapping(value = "/choerodon/v1/multi_language")
public class MultilingualC7nController {
    private ProjectC7nService projectService;

    public MultilingualC7nController(ProjectC7nService projectService) {
        this.projectService = projectService;
    }

    /**
     * 不要删除
     * @return
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询组织多语言")
    @GetMapping("/tenant_tl")
    public ResponseEntity<List<ProjectDTO>> queryTenantNameTl() {
        return ResponseEntity.ok(projectService.listAllProjects());
    }
}