package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.agile.AgileUserVO;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author superlee
 * @since 2020-10-14
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_AGILE_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/projects")
public class AgileOnProjectController {

    private final ProjectC7nService projectC7nService;

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


}
