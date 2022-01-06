package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.app.service.UserC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author superlee
 * @since 2020-10-14
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_AGILE_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/projects")
public class AgileOnProjectController {

    private final ProjectC7nService projectC7nService;
    @Autowired
    private UserC7nService userC7nService;

    public AgileOnProjectController(ProjectC7nService projectC7nService) {
        this.projectC7nService = projectC7nService;
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "敏捷分页模糊根据项目id集合查询项目下的用户和分配issue的用户接口")
    @PostMapping(value = "/{project_id}/agile_users_by_projects")
    @CustomPageRequest
    public ResponseEntity<Page<UserDTO>> agileUsersByProjects(@PathVariable(name = "project_id") Long id,
                                                              @ApiIgnore
                                                              @SortDefault(value = "id", direction = Sort.Direction.DESC)
                                                                      PageRequest pageable,
                                                              @RequestBody AgileUserVO agileUserVO) {
        return new ResponseEntity<>(projectC7nService.agileUsersByProjects(pageable, agileUserVO), HttpStatus.OK);
    }


    /**
     * 校验当前用户能否进去项目
     *
     * @param projectId 要查询的项目ID
     */
    @Permission(permissionLogin = true)
    @GetMapping(value = "/check-permission/{project_id}")
    @ApiOperation(value = "校验当前用户能否进去项目")
    public ResponseEntity<Boolean> checkPermissionByProjectId(@PathVariable(name = "project_id") Long projectId) {
        return new ResponseEntity<>(projectC7nService.checkPermissionByProjectId(projectId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询当前组织下用户的项目列表")
    @GetMapping(value = "/users/{user_id}/projects_simple")
    public ResponseEntity<List<ProjectDTO>> listProjectsByUserIdForSimple(@PathVariable(name = "organization_id") Long organizationId,
                                                                          @Encrypt @PathVariable(name = "user_id") Long userId,
                                                                          @RequestParam(required = false) String name,
                                                                          @RequestParam(required = false) String code,
                                                                          @RequestParam(required = false) String category,
                                                                          @RequestParam(required = false) Boolean enabled,
                                                                          @RequestParam(required = false) String params) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setName(name);
        projectDTO.setCode(code);
        projectDTO.setCategory(category);
        projectDTO.setEnabled(enabled);
        return new ResponseEntity<>(userC7nService.listProjectsByUserIdForSimple(organizationId, userId, projectDTO, params), HttpStatus.OK);
    }

}
