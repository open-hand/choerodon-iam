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
@RequestMapping(value = "/choerodon/v1/fix")
public class DataFixC7nController {
    private ProjectC7nService projectService;

    public DataFixC7nController(ProjectC7nService projectService) {
        this.projectService = projectService;
    }

    /**
     * 不要删除
     * @return
     */
    @Permission(level = ResourceLevel.SITE, permissionWithin = true)
    @ApiOperation(value = "查询所有的项目")
    @GetMapping("/projects/all")
    public ResponseEntity<List<ProjectDTO>> listAllProjects() {
        return ResponseEntity.ok(projectService.listAllProjects());
    }
}