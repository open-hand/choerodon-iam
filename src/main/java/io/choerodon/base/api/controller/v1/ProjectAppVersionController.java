package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.ApplicationSvcVersionRefService;
import io.choerodon.base.app.service.ApplicationVersionService;
import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/applications/{application_id}/versions")
public class ProjectAppVersionController extends BaseController {

    private ApplicationVersionService applicationVersionService;
    private ApplicationSvcVersionRefService applicationSvcVersionRefService;

    public ProjectAppVersionController(ApplicationVersionService applicationVersionService, ApplicationSvcVersionRefService applicationSvcVersionRefService) {
        this.applicationVersionService = applicationVersionService;
        this.applicationSvcVersionRefService = applicationSvcVersionRefService;
    }

    @GetMapping("/{version_id}")
    @ApiOperation(value = "根据版本id查询版本信息，以及版本下的服务列表，服务版本列表信息")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    public ResponseEntity<ApplicationVersionVO> getAppVersionWithServicesAndServiceVersions(@PathVariable("project_id") Long projectId,
                                                                                            @PathVariable("application_id") Long applicationId,
                                                                                            @PathVariable("version_id") Long versionId
    ) {
        return new ResponseEntity<>(applicationVersionService.getAppVersionWithServicesAndServiceVersions(projectId, versionId), HttpStatus.OK);
    }
    @GetMapping("/info/{version_id}")
    @ApiOperation(value = "根据版本id查询版本基本信息")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    public ResponseEntity<AppVersionInfoVO> getAppVersionInfo(@PathVariable("project_id") Long projectId,
                                                              @PathVariable("application_id") Long applicationId,
                                                              @PathVariable("version_id") Long versionId
    ) {
        return new ResponseEntity<>(applicationVersionService.getAppVersionInfo(projectId, versionId), HttpStatus.OK);
    }

    @GetMapping("/brief_info")
    @ApiOperation(value = "查询应用下版本（简要信息）")
    @Permission(type = ResourceType.PROJECT)
    public ResponseEntity<List<ApplicationVersionWithStatusVO>> getBriefInfo(@PathVariable("project_id") Long projectId,
                                                                             @PathVariable("application_id") Long applicationId) {
        return new ResponseEntity<>(applicationVersionService.getBriefInfo(applicationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "创建应用版本")
    @PostMapping
    public ResponseEntity<ApplicationVersionDTO> createAppVersion(@PathVariable("project_id") Long projectId,
                                                                  @PathVariable("application_id") Long applicationId,
                                                                  @RequestBody @Valid ApplicationVersionVO applicationVersionVO) {
        return new ResponseEntity<>(applicationVersionService.createAppVersion(applicationId, applicationVersionVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询所有应用版本")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<ApplicationVersionVO>> pagingByOptions(@PathVariable("project_id") Long projectId,
                                                                          @PathVariable("application_id") Long applicationId,
                                                                          @ApiIgnore
                                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                          @RequestParam(required = false) String version,
                                                                          @RequestParam(required = false) String description,
                                                                          @RequestParam(required = false) String status,
                                                                          @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(applicationVersionService.pagingByOptions(Pageable, applicationId, version, description, status, params), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据主键更新应用版本")
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationVersionDTO> updateAppVersion(@PathVariable("project_id") Long projectId,
                                                                  @PathVariable("id") Long id,
                                                                  @PathVariable("application_id") Long applicationId,
                                                                  @RequestBody ApplicationVersionVO applicationVersionVO) {
        applicationVersionVO.setId(id);
        applicationVersionVO.setApplicationId(applicationId);
        return new ResponseEntity<>(applicationVersionService.update(applicationVersionVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "version唯一性校验,已存在返回false")
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkAppVersionName(@PathVariable("project_id") Long projectId,
                                                       @PathVariable("application_id") Long applicationId,
                                                       @RequestParam("version") String version) {
        return new ResponseEntity<>(applicationVersionService.checkName(applicationId, version), HttpStatus.OK);
    }

    @GetMapping("/{version_id}/svc_versions")
    @ApiOperation(value = "查询应用版本的服务版本信息")
    @Permission(type = ResourceType.PROJECT)
    public ResponseEntity<List<AppServiceDetailsVO>> getSvcVersions(@PathVariable("project_id") Long projectId,
                                                                    @PathVariable("application_id") Long applicationId,
                                                                    @PathVariable("version_id") Long versionId) {
        return new ResponseEntity<>(applicationSvcVersionRefService.getSvcVersions(versionId), HttpStatus.OK);
    }

    @GetMapping("/{version_id}/svc_versions/page")
    @ApiOperation(value = "分页查询应用版本的服务版本信息")
    @Permission(type = ResourceType.PROJECT)
    @CustomPageRequest
    public ResponseEntity<PageInfo<AppServiceDetailsVO>> pagingAppSvcAndVersions(@PathVariable("project_id") Long projectId,
                                                                                 @PathVariable("application_id") Long applicationId,
                                                                                 @ApiIgnore
                                                                                 @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                 @PathVariable("version_id") Long versionId) {
        return new ResponseEntity<>(applicationSvcVersionRefService.pagingAppSvcAndVersions(versionId, Pageable), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据主键删除应用版本")
    @DeleteMapping("/{id}")
    public ResponseEntity deleteAppVersion(@PathVariable("project_id") Long projectId,
                                           @PathVariable("application_id") Long applicationId,
                                           @PathVariable("id") Long id) {
        applicationVersionService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据应用版本id,服务id,查询服务下所有版本（不包含已发布版本，支持模糊搜索）")
    @GetMapping("/{version_id}/services/{service_id}/with_all_version")
    public ResponseEntity<List<AppServiceVersionVO>> getAppSvcWithAllVersion(@PathVariable("project_id") Long projectId,
                                                                             @PathVariable("application_id") Long applicationId,
                                                                             @PathVariable("version_id") Long versionId,
                                                                             @PathVariable("service_id") String serviceId,
                                                                             @RequestParam(required = false) String version) {
        return new ResponseEntity<>(applicationVersionService.getAppSvcWithAllVersion(projectId, versionId, serviceId, version), HttpStatus.OK);
    }
}
