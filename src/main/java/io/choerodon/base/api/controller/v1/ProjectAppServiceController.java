package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.api.vo.AppServiceVersionVO;
import io.choerodon.base.app.service.AppServiceRefService;
import io.choerodon.base.app.service.ApplicationServiceService;
import io.choerodon.base.infra.dto.devops.AppServiceRepVO;
import io.choerodon.base.infra.dto.devops.AppServiceVO;
import io.choerodon.base.infra.dto.devops.AppServiceVersionUploadPayload;
import io.choerodon.core.annotation.Permission;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/7
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/applications/{application_id}/services")
public class ProjectAppServiceController extends BaseController {

    private ApplicationServiceService applicationServiceService;
    private AppServiceRefService appServiceRefService;

    public ProjectAppServiceController(ApplicationServiceService applicationServiceService, AppServiceRefService appServiceRefService) {
        this.applicationServiceService = applicationServiceService;
        this.appServiceRefService = appServiceRefService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据应用服务ID查询所对应的版本列表")
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<AppServiceVersionUploadPayload>> listVersionsByAppServiceId(@PathVariable("project_id") Long projectId,
                                                                                           @PathVariable("application_id") Long applicationId,
                                                                                           @ApiParam(value = "应用服务id")
                                                                                           @PathVariable("id") Long appServiceId) {
        return new ResponseEntity<>(applicationServiceService.listVersionsByAppServiceId(appServiceId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询应用下的服务以及服务的所有版本")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<AppServiceDetailsVO>> pagingServicesWithVersionsByAppId(@PathVariable("project_id") Long projectId,
                                                                                           @PathVariable("application_id") Long applicationId,
                                                                                           @ApiIgnore
                                                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                           @RequestParam(value = "name", required = false) String name) {
        return new ResponseEntity<>(appServiceRefService.pagingServicesWithVersionsByAppId(projectId, Pageable, applicationId, name), HttpStatus.OK);
    }

    @ApiOperation(value = "根据应用ID查询包含的服务ID列表")
    @Permission(permissionWithin = true)
    @GetMapping("/ids")
    public ResponseEntity<Set<Long>> listAppServicesByAppId(@PathVariable("project_id") Long projectId,
                                                            @PathVariable("application_id") Long applicationId) {
        return new ResponseEntity<>(appServiceRefService.listAppServiceIdsByAppId(applicationId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "添加应用服务")
    @PostMapping
    public ResponseEntity addAppSvcRef(@PathVariable("project_id") Long projectId,
                                       @PathVariable("application_id") Long applicationId,
                                       @RequestParam("service_ids") Set<Long> serviceIds) {
        applicationServiceService.addAppSvcRef(applicationId, serviceIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "删除应用服务")
    @DeleteMapping
    public ResponseEntity deleteAppSvcRef(@PathVariable("project_id") Long projectId,
                                          @PathVariable("application_id") Long applicationId,
                                          @RequestParam("service_ids") Set<Long> serviceIds) {
        applicationServiceService.deleteAppSvcRef(projectId, applicationId, serviceIds);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询应用的服务")
    @CustomPageRequest
    @GetMapping("/paging/app")
    public ResponseEntity<PageInfo<AppServiceDetailsVO>> pagingAppSvcByOptions(@PathVariable("project_id") Long projectId,
                                                                               @PathVariable("application_id") Long applicationId,
                                                                               @ApiIgnore
                                                                               @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                               @RequestParam(required = false) String name,
                                                                               @RequestParam(required = false) String code,
                                                                               @RequestParam(required = false) String type,
                                                                               @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(applicationServiceService.pagingAppSvcByOptions(Pageable, projectId, applicationId, name, code, type, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询本项目下的服务，过滤掉已经添加过的服务")
    @CustomPageRequest
    @GetMapping("/paging/project")
    public ResponseEntity<PageInfo<AppServiceVO>> pagingProServiceByOptions(@PathVariable("project_id") Long projectId,
                                                                            @PathVariable("application_id") Long applicationId,
                                                                            @ApiIgnore
                                                                            @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                            @RequestParam(required = false) String name,
                                                                            @RequestParam(required = false) String code,
                                                                            @RequestParam(required = false) String type,
                                                                            @RequestParam(required = false) String[] params) {
        AppServiceVO appServiceVO = new AppServiceVO();
        appServiceVO.setName(name);
        appServiceVO.setCode(code);
        appServiceVO.setType(type);
        return new ResponseEntity<>(applicationServiceService.pagingProServiceByOptions(Pageable, projectId, applicationId, appServiceVO, params, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询共享服务，过滤掉已经添加过的服务")
    @CustomPageRequest
    @GetMapping("/paging/shared")
    public ResponseEntity<PageInfo<AppServiceRepVO>> pagingSharedServiceByOptions(@PathVariable("project_id") Long projectId,
                                                                                  @PathVariable("application_id") Long applicationId,
                                                                                  @ApiIgnore
                                                                                  @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                                  @RequestParam(required = false) String name,
                                                                                  @RequestParam(required = false) String code,
                                                                                  @RequestParam(required = false) String type,
                                                                                  @RequestParam(required = false) String[] params) {
        AppServiceVO appServiceVO = new AppServiceVO();
        appServiceVO.setName(name);
        appServiceVO.setCode(code);
        appServiceVO.setType(type);
        return new ResponseEntity<>(applicationServiceService.pagingSharedServiceByOptions(Pageable, projectId, applicationId, appServiceVO, params, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据服务id,查询服务下所有版本（支持模糊搜索）")
    @GetMapping("/{service_id}/with_all_version")
    public ResponseEntity<List<AppServiceVersionVO>> getAppSvcWithAllVersion(@PathVariable("project_id") Long projectId,
                                                                             @PathVariable("application_id") Long applicationId,
                                                                             @PathVariable("service_id") String serviceId,
                                                                             @RequestParam(required = false) String version) {
        return new ResponseEntity<>(applicationServiceService.getAppSvcWithAllVersion(projectId, serviceId, version), HttpStatus.OK);
    }
}
