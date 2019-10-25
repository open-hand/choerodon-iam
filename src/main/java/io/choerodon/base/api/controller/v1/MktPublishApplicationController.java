package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.PublishedAppUpdate;
import io.choerodon.base.api.validator.UnpublishAppUpdate;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.app.service.MktApplyAndPublishService;
import io.choerodon.base.app.service.MktPublishApplicationService;
import io.choerodon.base.app.service.MktPublishVersionInfoService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

/**
 * @author pengyuhua
 * @date 2019/09/12
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/publish_applications")
public class MktPublishApplicationController {

    private MktPublishApplicationService mktPublishApplicationService;
    private MktPublishVersionInfoService mktPublishVersionInfoService;
    private MktApplyAndPublishService mktApplyAndPublishService;

    public MktPublishApplicationController(MktPublishApplicationService mktPublishApplicationService, MktPublishVersionInfoService mktPublishVersionInfoService, MktApplyAndPublishService mktApplyAndPublishService) {
        this.mktPublishApplicationService = mktPublishApplicationService;
        this.mktPublishVersionInfoService = mktPublishVersionInfoService;
        this.mktApplyAndPublishService = mktApplyAndPublishService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "市场发布入口校验（校验权限并更新发布状态）")
    @GetMapping(value = "/verifyPermissions")
    public ResponseEntity<PermissionVerificationResultsVO> verifyPermissions(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(mktApplyAndPublishService.verifyPermissions(projectId), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询市场发布信息")
    @CustomPageRequest
    @GetMapping
    public ResponseEntity<PageInfo<PublishAppPageVO>> list(@ApiIgnore
                                                           @SortDefault(value = "creation_date", direction = Sort.Direction.DESC) Pageable Pageable,
                                                           @PathVariable("project_id") Long projectId,
                                                           @RequestParam(value = "version", required = false) String version,
                                                           @RequestParam(value = "status", required = false) String status,
                                                           @RequestParam(value = "free", required = false) Boolean free,
                                                           @RequestParam(value = "publish_type", required = false) String publishType,
                                                           @RequestParam(value = "name", required = false) String name,
                                                           @RequestParam(value = "source_app_name", required = false) String sourceAppName,
                                                           @RequestParam(value = "description", required = false) String description,
                                                           @RequestParam(value = "params", required = false) String[] params) {
        MarketPublishApplicationVO filterDTO = new MarketPublishApplicationVO()
                .setFree(free)
                .setPublishType(publishType)
                .setName(name)
                .setSourceApplicationName(sourceAppName)
                .setDescription(description);
        return new ResponseEntity<>(mktPublishApplicationService.pageSearchPublishApps(projectId, Pageable, filterDTO, version, status, params), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @GetMapping("/versions")
    @ApiOperation(value = "查询应用版本信息")
    public ResponseEntity<List<MktPublishAppVersionVO>> listMktAppVersions(@PathVariable("project_id") Long projectId,
                                                                           @RequestParam("application_id") Long appId) {
        return new ResponseEntity<>(mktPublishVersionInfoService.listMktAppVersions(appId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询市场发布应用详情")
    @GetMapping("/{application_id}/detail")
    public ResponseEntity<MarketPublishApplicationVO> queryMktPublishAppDetail(@PathVariable("project_id") Long projectId,
                                                                               @PathVariable("application_id") Long appId) {
        return new ResponseEntity<>(mktPublishApplicationService.queryMktPublishAppDetail(projectId, appId), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "修改市场发布未发布应用")
    @PutMapping("/unpublish_apps/{id}")
    public ResponseEntity<MktPublishApplicationDTO> updateMktUnPublishApp(@PathVariable("project_id") Long projectId,
                                                                          @PathVariable("id") Long appId,
                                                                          @RequestBody @Validated(UnpublishAppUpdate.class) MktPublishApplicationDTO updateVO) {
        return new ResponseEntity<>(mktPublishApplicationService.updateMktPublishAppInfo(appId, updateVO, false), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "修改市场发布已发布应用")
    @PutMapping("/published_apps/{id}")
    public ResponseEntity<MktPublishApplicationDTO> updateMktPublishedApp(@PathVariable("project_id") Long projectId,
                                                                          @PathVariable("id") Long appId,
                                                                          @RequestBody @Validated(PublishedAppUpdate.class) MktPublishApplicationDTO updateVO) {
        return new ResponseEntity<>(mktPublishApplicationService.updateMktPublishAppInfo(appId, updateVO, true), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "查询应用版本详情")
    @GetMapping("/versions/{version_id}/detail")
    public ResponseEntity<MktPublishAppVersionVO> queryMktPublishAppVersionDetail(@PathVariable("project_id") Long projectId,
                                                                                  @PathVariable("version_id") Long versionId) {
        return new ResponseEntity<>(mktPublishVersionInfoService.queryMktPublishAppVersionDetail(versionId), HttpStatus.OK);
    }

    @GetMapping("/check_name")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "校验市场应用名称")
    public ResponseEntity<Boolean> checkName(@PathVariable("project_id") Long projectId,
                                             @RequestParam("name") String name,
                                             @RequestParam(value = "id", required = false) Long id) {
        return new ResponseEntity<>(mktPublishApplicationService.checkName(id, name), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据应用ID查询应用信息")
    @GetMapping("/list_by_ids")
    public ResponseEntity<List<MktPublishApplicationDTO>> listApplicationInfoByAppIds(@PathVariable("project_id") Long projectId,
                                                                                      @RequestParam("ids") Set<Long> appIds) {
        return new ResponseEntity<>(mktPublishApplicationService.listApplicationInfoByIds(appIds), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "获取申请发布信息时的初始化信息（贡献者/通知邮箱）")
    @GetMapping("/initial_info")
    public ResponseEntity<MktPublishApplicationDTO> getInitialInfo(@PathVariable("project_id") Long projectId,
                                                                   @RequestParam("organization_id") Long organizationId) {
        return new ResponseEntity<>(mktPublishApplicationService.getInitialInfo(organizationId), HttpStatus.OK);
    }

    @PostMapping
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "保存申请发布信息（可申请）")
    public ResponseEntity<MktPublishApplicationDTO> createAndApply(@PathVariable("project_id") Long projectId,
                                                                   @RequestParam("apply") Boolean apply,
                                                                   @RequestParam(value = "organization_id") Long organizationId,
                                                                   @RequestBody @Validated(Insert.class) MktPublishApplicationVO createVO) {
        return new ResponseEntity<>(mktPublishApplicationService.createAndApply(organizationId, apply, createVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "发布新版本 - 查询默认信息")
    @GetMapping("/{id}/new_version")
    public ResponseEntity<MktNewVersionVO> getBeforeNewVersion(@PathVariable("project_id") Long projectId,
                                                               @PathVariable("id") Long id) {
        return new ResponseEntity<>(mktPublishApplicationService.getBeforeNewVersion(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "发布新版本(可申请)")
    @PostMapping("/{id}/new_version")
    public ResponseEntity<MktNewVersionVO> createNewVersion(@PathVariable("project_id") Long projectId,
                                                            @PathVariable("id") Long id,
                                                            @RequestParam("organization_id") Long organizationId,
                                                            @RequestParam("apply") Boolean apply,
                                                            @RequestBody @Validated MktNewVersionVO newVersionVO) {
        return new ResponseEntity<>(mktPublishApplicationService.createNewVersionAndApply(organizationId, id, apply, newVersionVO), HttpStatus.OK);
    }


    @GetMapping("/app_categories/list/enable")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("分页查询可用应用类型列表")
    @CustomPageRequest
    public ResponseEntity<PageInfo<AppCategoryDTO>> getEnableCategoryList(@PathVariable("project_id") Long projectId,
                                                                          @ApiIgnore @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable) {
        return new ResponseEntity<>(mktPublishApplicationService.getEnableCategoryList(Pageable), HttpStatus.OK);
    }

    @GetMapping("/app_categories/check")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation("应用类型校验")
    public ResponseEntity<Boolean> categoriesCheck(@PathVariable("project_id") Long projectId,
                                                   @RequestParam("category_name") String categoryName) {
        return new ResponseEntity<>(mktPublishApplicationService.categoriesCheck(categoryName), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "上传文件到SaaS平台")
    @PostMapping("/cut_image")
    public ResponseEntity<String> cutImage(@RequestPart(name = "file") MultipartFile file,
                                           @ApiParam(name = "rotate", value = "顺时针旋转的角度", example = "90")
                                           @RequestParam(required = false) Double rotate,
                                           @ApiParam(name = "startX", value = "裁剪的X轴", example = "100")
                                           @RequestParam(required = false, name = "startX") Integer axisX,
                                           @ApiParam(name = "startY", value = "裁剪的Y轴", example = "100")
                                           @RequestParam(required = false, name = "startY") Integer axisY,
                                           @ApiParam(name = "endX", value = "裁剪的宽度", example = "200")
                                           @RequestParam(required = false, name = "endX") Integer width,
                                           @ApiParam(name = "endY", value = "裁剪的高度", example = "200")
                                           @RequestParam(required = false, name = "endY") Integer height) {
        return new ResponseEntity<>(mktPublishApplicationService.cutImage(file, rotate, axisX, axisY, width, height), HttpStatus.OK);
    }
}
