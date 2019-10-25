package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.base.BaseController;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;


@RestController
@RequestMapping(value = "/v1/projects")
public class ProjectUserController extends BaseController {

    private UserService userService;

    public ProjectUserController(UserService userService) {
        this.userService = userService;
    }


    @Permission(type = ResourceType.PROJECT)
    @ApiOperation(value = "项目层分页查询用户列表（包括用户信息以及所分配的项目角色信息）")
    @GetMapping(value = "/{project_id}/users/search")
    @CustomPageRequest
    public ResponseEntity<PageInfo<UserDTO>> pagingQueryUsersWithRolesOnProjectLevel(@PathVariable(name = "project_id") Long projectId,
                                                                                     @ApiIgnore
                                                                                     @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                     @RequestParam(required = false) String loginName,
                                                                                     @RequestParam(required = false) String realName,
                                                                                     @RequestParam(required = false) String roleName,
                                                                                     @RequestParam(required = false) Boolean enabled,
                                                                                     @RequestParam(required = false) String params) {
        return new ResponseEntity<>(userService.pagingQueryUsersWithRolesOnProjectLevel(projectId, Pageable, loginName, realName, roleName,
                enabled, params), HttpStatus.OK);
    }

}
