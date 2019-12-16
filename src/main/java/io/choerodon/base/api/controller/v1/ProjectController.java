package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.app.service.ProjectService;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

/**
 * @author flyleft
 */
@RestController
@RequestMapping(value = "/v1/projects")
public class ProjectController extends BaseController {

    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 按照项目Id查询项目与应用
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @GetMapping(value = "/{project_id}")
    @ApiOperation(value = "按照项目Id查询项目")
    public ResponseEntity<ProjectDTO> query(@PathVariable(name = "project_id") Long id) {
        return new ResponseEntity<>(projectService.queryProjectById(id), HttpStatus.OK);
    }

    /**
     * 根据id集合查询项目
     *
     * @param ids id集合，去重
     * @return 项目集合
     */
    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据id集合查询项目")
    @PostMapping("/ids")
    public ResponseEntity<List<ProjectDTO>> queryByIds(@RequestBody Set<Long> ids) {
        return new ResponseEntity<>(projectService.queryByIds(ids), HttpStatus.OK);
    }

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     */
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER, InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "分页模糊查询项目下的用户")
    @GetMapping(value = "/{project_id}/users")
    @CustomPageRequest
    public ResponseEntity<PageInfo<UserDTO>> list(@PathVariable(name = "project_id") Long id,
                                                  @ApiIgnore
                                                  @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                  @RequestParam(required = false, name = "id") Long userId,
                                                  @RequestParam(required = false) String email,
                                                  @RequestParam(required = false) String param) {
        return new ResponseEntity<>(projectService.pagingQueryTheUsersOfProject(id, userId, email, Pageable, param), HttpStatus.OK);
    }

    /**
     * 项目层更新项目，code和organizationId都不可更改
     */
    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "修改项目")
    @PutMapping(value = "/{project_id}")
    public ResponseEntity<ProjectDTO> update(@PathVariable(name = "project_id") Long id,
                                             @RequestBody ProjectDTO projectDTO) {
        if (StringUtils.isEmpty(projectDTO.getName())) {
            throw new CommonException("error.project.name.empty");
        }
        if (projectDTO.getName().length() < 1 || projectDTO.getName().length() > 32) {
            throw new CommonException("error.project.name.size");
        }
        if (projectDTO.getObjectVersionNumber() == null) {
            throw new CommonException("error.objectVersionNumber.null");
        }
        projectDTO.setId(id);
        //项目code不可编辑
        projectDTO.setCode(null);
        //项目category不可编辑
        projectDTO.setCategory(null);
        //组织id不可编辑
        projectDTO.setOrganizationId(null);
        return new ResponseEntity<>(projectService.update(projectDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = InitRoleCode.PROJECT_OWNER)
    @ApiOperation(value = "禁用项目")
    @PutMapping(value = "/{project_id}/disable")
    public ResponseEntity<ProjectDTO> disableProject(@PathVariable(name = "project_id") Long id) {
        return new ResponseEntity<>(projectService.disableProject(id), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @GetMapping(value = "/check/{code}")
    public ResponseEntity<Boolean> checkProjCode(@PathVariable(name = "code") String code) {
        return new ResponseEntity<>(projectService.checkProjCode(code), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @GetMapping(value = "/list/by_name")
    public ResponseEntity<List<Long>> getProListByName(@RequestParam(required = false) String name) {
        return new ResponseEntity<>(projectService.getProListByName(name), HttpStatus.OK);
    }
}
