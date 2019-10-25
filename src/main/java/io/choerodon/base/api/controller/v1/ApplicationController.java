package io.choerodon.base.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.AppDownloadDevopsReqVO;
import io.choerodon.base.api.vo.ProjectAndAppVO;
import io.choerodon.base.app.service.AppDownloadService;
import io.choerodon.base.app.service.ApplicationService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.core.base.BaseController;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@RestController
@RequestMapping(value = "/v1/applications")
public class ApplicationController extends BaseController {

    private ApplicationService applicationService;
    private AppDownloadService appDownloadService;

    public ApplicationController(ApplicationService applicationService, AppDownloadService appDownloadService) {
        this.applicationService = applicationService;
        this.appDownloadService = appDownloadService;
    }

    @Permission(type = ResourceType.SITE, permissionWithin = true)
    @ApiOperation(value = "根据Id查询应用")
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationDTO> getAppById(@PathVariable(value = "id") Long id) {
        return new ResponseEntity<>(applicationService.getAppById(id), HttpStatus.OK);
    }

    @ApiOperation(value = "通过应用token查询项目")
    @Permission(type = ResourceType.SITE, permissionWithin = true)
    @GetMapping("/by_token")
    public ResponseEntity<ProjectAndAppVO> getProjectAndAppByToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(applicationService.getProjectAndAppByToken(token), HttpStatus.OK);
    }

    @ApiOperation(value = "下载SaaS应用")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @PostMapping("/{publish_app_version_id}/download")
    public ResponseEntity<ApplicationDTO> downloadApplication(@PathVariable("publish_app_version_id") Long publishAppVersionId,
                                                              @RequestParam(required = false, name = "organization_id") Long organizationId) {
        return new ResponseEntity<>(appDownloadService.downloadApplication(publishAppVersionId, organizationId), HttpStatus.OK);
    }

    @ApiOperation(value = "完成SaaS应用下载")
    @Permission(type = ResourceType.SITE, permissionWithin = true)
    @PostMapping("/{app_download_record_id}/complete_downloading")
    public ResponseEntity completeDownloadApplication(@PathVariable("app_download_record_id") Long appDownloadRecordId,
                                                      @RequestParam("app_version_id") Long appVersionId,
                                                      @RequestParam("organization_id") Long organizationId,
                                                      @RequestBody List<AppDownloadDevopsReqVO> appDownloadDevopsReqVOS) {
        appDownloadService.completeDownloadApplication(appDownloadRecordId, appVersionId, organizationId, appDownloadDevopsReqVOS);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "SaaS应用下载失败")
    @Permission(type = ResourceType.SITE, permissionWithin = true)
    @PutMapping("/{app_download_record_id}/fail_downloading")
    public ResponseEntity failToDownloadApplication(@PathVariable("app_download_record_id") Long appDownloadRecordId,
                                                    @RequestParam("app_version_id") Long appVersionId,
                                                    @RequestParam("organization_id") Long organizationId) {
        appDownloadService.failToDownloadApplication(appDownloadRecordId, appVersionId, organizationId);
        return new ResponseEntity(HttpStatus.OK);
    }

}
