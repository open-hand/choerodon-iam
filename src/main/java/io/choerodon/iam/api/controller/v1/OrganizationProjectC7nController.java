package io.choerodon.iam.api.controller.v1;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.base.BaseController;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.BarLabelRotationVO;
import io.choerodon.iam.api.vo.ProjectSearchVO;
import io.choerodon.iam.api.vo.ProjectVisitInfoVO;
import io.choerodon.iam.app.service.OrganizationProjectC7nService;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.utils.ParamUtils;
import io.choerodon.mybatis.pagehelper.annotation.SortDefault;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.pagehelper.domain.Sort;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author flyleft
 * @author superlee
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_ORGANIZATION_PROJECT)
@RestController
@RequestMapping(value = "/choerodon/v1/organizations/{organization_id}/projects")
public class OrganizationProjectC7nController extends BaseController {

    private OrganizationProjectC7nService organizationProjectC7nService;
    private OrganizationResourceLimitService organizationResourceLimitService;

    public OrganizationProjectC7nController(OrganizationProjectC7nService organizationProjectC7nService,
                                            OrganizationResourceLimitService organizationResourceLimitService) {
        this.organizationProjectC7nService = organizationProjectC7nService;
        this.organizationResourceLimitService = organizationResourceLimitService;
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @ApiOperation(value = "创建项目")
    @PostMapping
    public ResponseEntity<ProjectDTO> create(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestBody @Valid ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        return new ResponseEntity<>(organizationProjectC7nService.createProject(organizationId, projectDTO), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @GetMapping("/list")
    @ApiOperation(value = "查询分配开发的项目")
    public ResponseEntity<List<ProjectDTO>> getAgileProjects(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestParam(required = false) String[] param) {
        return new ResponseEntity<>(organizationProjectC7nService.getAgileProjects(organizationId, ParamUtils.arrToStr(param)),
                HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR, InitRoleCode.ORGANIZATION_MEMBER})
    @PutMapping(value = "/{project_id}")
    @ApiOperation(value = "修改项目")
    public ResponseEntity<ProjectDTO> update(@PathVariable(name = "organization_id") Long organizationId,
                                             @PathVariable(name = "project_id") Long projectId,
                                             @Valid @RequestBody ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        projectDTO.setId(projectId);
        return new ResponseEntity<>(organizationProjectC7nService.update(organizationId, projectDTO), HttpStatus.OK);

    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "启用项目")
    @PutMapping(value = "/{project_id}/enable")
    public ResponseEntity<ProjectDTO> enableProject(@PathVariable(name = "organization_id") Long organizationId,
                                                    @PathVariable(name = "project_id") Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationProjectC7nService.enableProject(organizationId, projectId, userId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "禁用项目")
    @PutMapping(value = "/{project_id}/disable")
    public ResponseEntity<ProjectDTO> disableProject(@PathVariable(name = "organization_id") Long organizationId,
                                                     @PathVariable(name = "project_id") Long projectId) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        return new ResponseEntity<>(organizationProjectC7nService.disableProject(
                organizationId, projectId, userId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "项目信息校验")
    @PostMapping(value = "/check")
    public ResponseEntity<Boolean> check(@PathVariable(name = "organization_id") Long organizationId,
                                         @RequestBody ProjectDTO projectDTO) {
        projectDTO.setOrganizationId(organizationId);
        return ResponseEntity.ok(organizationProjectC7nService.check(projectDTO));
    }

    @Permission(level = ResourceLevel.SITE, roles = {InitRoleCode.SITE_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下的项目类型及类下项目数及项目")
    @GetMapping("/under_the_type")
    public ResponseEntity<Map<String, Object>> getProjectsByType(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(organizationProjectC7nService.getProjectsByType(organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, permissionWithin = true)
    @ApiOperation(value = "根据组织Id及项目code查询项目/devops用")
    @GetMapping(value = "/by_code")
    public ResponseEntity<ProjectDTO> getProjectByOrgIdAndCode(@PathVariable(name = "organization_id") Long organizationId,
                                                               @RequestParam(name = "code") String code) {
        return new ResponseEntity<>(organizationProjectC7nService.getProjectByOrgIdAndCode(organizationId, code), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.PROJECT_MEMBER})
    @ApiOperation(value = "查询组织下所有项目")
    @GetMapping(value = "/all")
    public ResponseEntity<List<ProjectDTO>> listProjectsByOrgId(@PathVariable(name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(organizationProjectC7nService.listProjectsByOrgId(organizationId), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "分页查询项目/devops用")
    @GetMapping
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> pagingQuery(@PathVariable(name = "organization_id") Long organizationId,
                                                        @ApiIgnore
                                                        @SortDefault(value = "id", direction = Sort.Direction.DESC) PageRequest pageRequest,
                                                        @RequestParam(required = false) String name,
                                                        @RequestParam(required = false) String code,
                                                        @RequestParam(required = false) Boolean enabled,
                                                        @RequestParam(required = false, defaultValue = "false") Boolean withAdditionInfo,
                                                        @RequestParam(required = false) String params) {
        ProjectDTO project = new ProjectDTO();
        project.setOrganizationId(organizationId);
        project.setName(name);
        project.setCode(code);
        project.setEnabled(enabled);
        return new ResponseEntity<>(organizationProjectC7nService.pagingQuery(organizationId, pageRequest, project, params, withAdditionInfo),
                HttpStatus.OK);
    }

    /**
     * 查询组织下项目部署次数
     * 前端传的时间参数格式应为
     * yyyy-MM-dd HH:mm:ss
     *
     * @param organizationId 组织id
     * @param projectIds     项目id
     * @param startTime      开始时间
     * @param endTime        结束时间
     */
    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下项目部署次数")
    @PostMapping("/deploy_records")
    public ResponseEntity<BarLabelRotationVO> countDeployRecords(@PathVariable(name = "organization_id") Long organizationId,
                                                                 @RequestBody Set<Long> projectIds,
                                                                 @ApiParam(value = "开始时间：结构为yyyy-MM-dd HH:mm:ss", required = true)
                                                                 @RequestParam(value = "start_time") Date startTime,
                                                                 @ApiParam(value = "结束时间：结构为yyyy-MM-dd HH:mm:ss", required = true)
                                                                 @RequestParam(value = "end_time") Date endTime) {
        return ResponseEntity.ok(organizationProjectC7nService.countDeployRecords(projectIds, startTime, endTime));
    }

    @Permission(level = ResourceLevel.ORGANIZATION, roles = {InitRoleCode.ORGANIZATION_ADMINISTRATOR})
    @ApiOperation(value = "查询组织下项目（最多20个）")
    @GetMapping("/with_limit")
    public ResponseEntity<List<ProjectDTO>> listProjectsWithLimit(
            @PathVariable(name = "organization_id") Long organizationId,
            @RequestParam(required = false) String name) {
        return ResponseEntity.ok(organizationProjectC7nService.listProjectsWithLimit(organizationId, name));
    }

    @Permission(permissionLogin = true)
    @ApiOperation(value = "检查是否还能创建项目")
    @GetMapping("/check_enable_create")
    public ResponseEntity<Boolean> checkEnableCreateProject(
            @PathVariable(name = "organization_id") Long organizationId) {
        return ResponseEntity.ok(organizationResourceLimitService.checkEnableCreateProject(organizationId));
    }


    /**
     * 查询当前用户最近访问项目信息
     */
    @Permission(permissionLogin = true)
    @ApiOperation(value = "查询当前用户最近访问信息")
    @GetMapping(value = "/latest_visit")
    public ResponseEntity<List<ProjectVisitInfoVO>> queryLatestVisitProjectInfo(
            @PathVariable(name = "organization_id") Long organizationId) {
        return ResponseEntity.ok(organizationProjectC7nService.queryLatestVisitProjectInfo(organizationId));
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "查询组织下所有项目")
    @PostMapping(value = "/all_with_category")
    @CustomPageRequest
    public ResponseEntity<Page<ProjectDTO>> listProjectsWithCategoryByOrgId(@SortDefault(value = "id", direction = io.choerodon.mybatis.pagehelper.domain.Sort.Direction.DESC)
                                                                                    PageRequest pageRequest,
                                                                            @PathVariable(name = "organization_id") Long organizationId,
                                                                            @RequestBody ProjectSearchVO projectSearchVO) {
        return new ResponseEntity<>(organizationProjectC7nService.listProjectsWithCategoryByOrgId(organizationId, projectSearchVO, pageRequest), HttpStatus.OK);
    }


    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "根据项目类型，查询组织下项目列表")
    @GetMapping(value = "/list_by_category")
    public ResponseEntity<List<ProjectDTO>> listProjectsByCategory(@PathVariable(name = "organization_id") Long organizationId,
                                                                   @RequestParam(name = "category_code") String categoryCode) {
        return ResponseEntity.ok(organizationProjectC7nService.listProjectsByCategoryAndOrgId(organizationId, categoryCode));
    }

}
