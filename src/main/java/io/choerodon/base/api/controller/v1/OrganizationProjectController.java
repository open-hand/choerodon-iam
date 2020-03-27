package io.choerodon.base.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.validator.ProjectValidator;
import io.choerodon.base.api.vo.BarLabelRotationVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.OrganizationProjectService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.ProjectDTO;
import io.choerodon.base.infra.utils.ParamUtils;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author flyleft
 * @author superlee
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/projects")
public class OrganizationProjectController extends BaseController {

    private OrganizationProjectService organizationProjectService;
    private ProjectValidator projectValidator;

    public OrganizationProjectController(OrganizationProjectService organizationProjectService, ProjectValidator projectValidator) {
        this.organizationProjectService = organizationProjectService;
        this.projectValidator = projectValidator;
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "创建项目")
    @PostMapping
    public ResponseEntity<ProjectDTO> create(@PathVariable(name = "organization_id") Long organizationId,
                                             @RequestBody @Valid ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        projectValidator.validateProjectCategoryCode(projectDTO.getCode());
        return new ResponseEntity<>(organizationProjectService.createProject(organizationId, projectDTO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @GetMapping("/list")
    @ApiOperation(value = "查询分配开发的项目")
    public ResponseEntity<List<ProjectDTO>> getAgileProjects(@PathVariable(name = "organization_id") Long organizationId,
                                                             @RequestParam(required = false) String[] param) {
        return new ResponseEntity<>(organizationProjectService.getAgileProjects(organizationId, ParamUtils.arrToStr(param)),
                HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @PutMapping(value = "/{project_id}")
    @ApiOperation(value = "修改项目")
    public ResponseEntity<ProjectDTO> update(@PathVariable(name = "organization_id") Long organizationId,
                                             @PathVariable(name = "project_id") Long projectId,
                                             @RequestBody ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        projectDTO.setId(projectId);
        return new ResponseEntity<>(organizationProjectService.update(organizationId, projectDTO), HttpStatus.OK);

    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "启用项目")
    @PutMapping(value = "/{project_id}/enable")
    public ResponseEntity<ProjectDTO> enableProject(@PathVariable(name = "organization_id") Long organizationId,
                                                    @PathVariable(name = "project_id") Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationProjectService.enableProject(organizationId, projectId, userId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION)
    @ApiOperation(value = "禁用项目")
    @PutMapping(value = "/{project_id}/disable")
    public ResponseEntity<ProjectDTO> disableProject(@PathVariable(name = "organization_id") Long organizationId,
                                                     @PathVariable(name = "project_id") Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationProjectService.disableProject(
                organizationId, projectId, userId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "项目信息校验")
    @PostMapping(value = "/check")
    public ResponseEntity check(@PathVariable(name = "organization_id") Long organizationId,
                                @RequestBody ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        organizationProjectService.check(projectDTO);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下的项目类型及类下项目数及项目")
    @GetMapping("/under_the_type")
    public ResponseEntity<Map<String, Object>> getProjectsByType(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(organizationProjectService.getProjectsByType(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, permissionWithin = true)
    @ApiOperation(value = "根据组织Id及项目code查询项目/devops用")
    @GetMapping(value = "/by_code")
    public ResponseEntity<ProjectDTO> getProjectByOrgIdAndCode(@PathVariable(name = "organization_id") Long organizationId,
                                                               @RequestParam(name = "code") String code) {
        return new ResponseEntity<>(organizationProjectService.getProjectByOrgIdAndCode(organizationId, code), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询组织下所有项目")
    @GetMapping(value = "/all")
    public ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(organizationProjectService.listProjectsByOrgId(organizationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, permissionWithin = true)
    @ApiOperation(value = "分页查询项目/devops用")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<PageInfo<ProjectDTO>> pagingQuery(@PathVariable(name = "organization_id") Long organizationId,
                                                            @ApiIgnore
                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                            @RequestParam(required = false) String name,
                                                            @RequestParam(required = false) String code,
                                                            @RequestParam(required = false) Boolean enabled,
                                                            @RequestParam(required = false) String params) {
        ProjectDTO project = new ProjectDTO();
        project.setOrganizationId(organizationId);
        project.setName(name);
        project.setCode(code);
        project.setEnabled(enabled);
        return new ResponseEntity<>(organizationProjectService.pagingQuery(organizationId, Pageable, project, params),
                HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下项目部署次数")
    @PostMapping("/deploy_records")
    public ResponseEntity<BarLabelRotationVO> countDeployRecords(@PathVariable(name = "organization_id") Long organizationId,
                                                                 @RequestBody Set<Long> projectIds,
                                                                 @RequestParam(value = "start_time") Date startTime,
                                                                 @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(organizationProjectService.countDeployRecords(projectIds, startTime, endTime));
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下项目（最多20个）")
    @GetMapping("/with_limit")
    public ResponseEntity<List<ProjectDTO>> listProjectsWithLimit(@PathVariable(name = "organization_id") Long organizationId,
                                                                  @RequestParam(required = false) String name) {
        return ResponseEntity.ok(organizationProjectService.listProjectsWithLimit(organizationId, name));
    }

    @Permission(type = ResourceType.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "检查是否还能创建项目")
    @GetMapping("/check_enable_create")
    public ResponseEntity<Boolean> checkEnableCreateProject(@PathVariable(name = "organization_id") Long organizationId) {
        return ResponseEntity.ok(organizationProjectService.checkEnableCreateProject(organizationId));
    }
}
