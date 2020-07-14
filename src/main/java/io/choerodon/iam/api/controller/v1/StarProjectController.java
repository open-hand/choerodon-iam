package io.choerodon.iam.api.controller.v1;

import com.netflix.discovery.EurekaNamespace;
import io.choerodon.iam.app.service.StarProjectService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.StarProjectUserRelDTO;
import io.choerodon.swagger.annotation.Permission;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 10:20
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_STAR_PROJECT)
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/star_projects")
public class StarProjectController {

    @Autowired
    private StarProjectService starProjectService;

    @ApiOperation("新增星标项目")
    @Permission(permissionLogin = true)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody StarProjectUserRelDTO starProjectUserRelDTO) {
        starProjectService.create(starProjectUserRelDTO);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("删除星标项目")
    @Permission(permissionLogin = true)
    @DeleteMapping
    public ResponseEntity<Void> deleteByProjectId(
            @EurekaNamespace @RequestParam(value = "project_id") Long projectId) {
        starProjectService.delete(projectId);
        return ResponseEntity.noContent().build();
    }

    @ApiOperation("查询组织下 用户有权限的星标项目")
    @Permission(permissionLogin = true)
    @GetMapping
    public ResponseEntity<List<ProjectDTO>> query(@Encrypt @PathVariable(value = "organization_id") Long organizationId,
                                                  @RequestParam(value = "size", required = false) Integer size) {
        return ResponseEntity.ok(starProjectService.query(organizationId, size));
    }

}
