package io.choerodon.iam.api.controller.v1;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.ProjectC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author flyleft
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/projects")
public class ProjectC7nController extends BaseController {

    @Value("${choerodon.category.enabled:false}")
    private boolean enableCategory;

    private ProjectC7nService projectService;

    public ProjectC7nController(ProjectC7nService projectService) {
        this.projectService = projectService;
    }

    /**
     * 按照项目Id查询项目与应用
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping(value = "/{project_id}")
    @ApiOperation(value = "按照项目Id查询项目")
    public ResponseEntity<ProjectDTO> query(@PathVariable(name = "project_id") Long id,
                                            @RequestParam(value = "with_category_info", required = false, defaultValue = "true") Boolean withCategoryInfo,
                                            @RequestParam(value = "with_user_info", required = false, defaultValue = "true") Boolean withUserInfo,
                                            @RequestParam(value = "with_agile_info", required = false, defaultValue = "true") Boolean withAgileInfo) {
        return new ResponseEntity<>(projectService.queryProjectById(id, withCategoryInfo, withUserInfo, withAgileInfo), HttpStatus.OK);
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
    public ResponseEntity<List<ProjectDTO>> queryByIds(@Encrypt @RequestBody Set<Long> ids) {
        return new ResponseEntity<>(projectService.queryByIds(ids), HttpStatus.OK);
    }


    /**
     * 项目层更新项目，code和organizationId都不可更改
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "修改项目")
    @PutMapping(value = "/{project_id}")
    public ResponseEntity<ProjectDTO> update(
            @PathVariable(name = "project_id") Long id,
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

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用项目")
    @PutMapping(value = "/{project_id}/disable")
    public ResponseEntity<ProjectDTO> disableProject(
            @PathVariable(name = "project_id") Long id) {
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

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目所属组织下所有可用项目（不包含本项目，限制50个)")
    @GetMapping("/{project_id}/except_self/with_limit")
    public ResponseEntity<List<ProjectDTO>> listOrgProjectsWithLimitExceptSelf(@PathVariable(name = "project_id") Long projectId,
                                                                               @RequestParam(required = false) String name) {
        return ResponseEntity.ok(projectService.listOrgProjectsWithLimitExceptSelf(projectId, name));
    }

    /**
     * 根据projectId和param模糊查询loginName和realName两列
     */
    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页模糊查询项目下的用户")
    @GetMapping(value = "/{project_id}/users")
    @CustomPageRequest
    public ResponseEntity<Page<UserDTO>> list(@PathVariable(name = "project_id") Long projectId,
                                              @ApiIgnore
                                              @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                              @Encrypt @RequestParam(required = false, name = "id") Long userId,
                                              @RequestParam(required = false) String email,
                                              @RequestParam(required = false) String param) {
        return ResponseEntity.ok(projectService.pagingQueryTheUsersOfProject(projectId, userId, email, pageRequest, param));
    }

    /**
     * 查询项目基本信息
     *
     * @param id 要查询的项目ID
     * @return 查询到的项目
     */
    @Permission(permissionLogin = true)
    @GetMapping(value = "/{project_id}/basic_info")
    @ApiOperation(value = "按照项目Id查询项目")
    public ResponseEntity<ProjectDTO> queryBasicInfo(@PathVariable(name = "project_id") Long id) {
        return ResponseEntity.ok(projectService.queryBasicInfo(id));
    }
}
