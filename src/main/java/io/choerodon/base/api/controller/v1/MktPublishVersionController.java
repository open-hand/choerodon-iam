package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.MktConfirmVO;
import io.choerodon.base.api.vo.MktVersionUpdateVO;
import io.choerodon.base.app.service.MktPublishApplicationService;
import io.choerodon.base.app.service.MktPublishVersionInfoService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author pengyuhua
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/publish_applications/{publish_app_id}/versions")
public class MktPublishVersionController {

    private MktPublishApplicationService mktPublishApplicationService;
    private MktPublishVersionInfoService mktPublishVersionInfoService;

    public MktPublishVersionController(MktPublishApplicationService mktPublishApplicationService, MktPublishVersionInfoService mktPublishVersionInfoService) {
        this.mktPublishApplicationService = mktPublishApplicationService;
        this.mktPublishVersionInfoService = mktPublishVersionInfoService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "一键提交申请")
    @GetMapping(value = "/{id}/apply")
    public ResponseEntity<Boolean> apply(@PathVariable("project_id") Long projectId,
                                         @PathVariable("publish_app_id") Long publishAppId,
                                         @PathVariable("id") Long id,
                                         @RequestParam(value = "organization_id") Long organizationId) {
        return new ResponseEntity<>(mktPublishApplicationService.oneClickApply(organizationId, publishAppId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "确认信息 - 查询确认信息")
    @GetMapping("/{id}/confirm")
    public ResponseEntity<MktConfirmVO> getBeforeConfirm(@PathVariable("project_id") Long projectId,
                                                         @PathVariable("publish_app_id") Long publishAppId,
                                                         @PathVariable("id") Long id) {
        return new ResponseEntity<>(mktPublishApplicationService.getBeforeConfirm(publishAppId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "确认信息(可发布)")
    @PutMapping("/{id}/confirm")
    public ResponseEntity<MktConfirmVO> confirmAndPublish(@PathVariable("project_id") Long projectId,
                                                          @PathVariable("publish_app_id") Long publishAppId,
                                                          @PathVariable("id") Long id,
                                                          @RequestParam("organization_id") Long organizationId,
                                                          @RequestParam("publish") Boolean publish,
                                                          @RequestBody @Validated MktConfirmVO confirmVO) {
        return new ResponseEntity<>(mktPublishApplicationService.confirmAndPublish(organizationId, publishAppId, id, publish, confirmVO), HttpStatus.OK);
    }


    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "发布市场应用重试发布")
    @PutMapping(value = "/{id}/republish")
    public ResponseEntity<Boolean> republish(@PathVariable("project_id") Long projectId,
                                             @PathVariable("publish_app_id") Long publishAppId,
                                             @PathVariable("id") Long id,
                                             @RequestParam("organization_id") Long organizationId) {
        return new ResponseEntity<>(mktPublishApplicationService.republish(organizationId, publishAppId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "撤销应用版本申请")
    @PutMapping(value = "/{id}/revocation")
    public ResponseEntity<Boolean> revocation(@PathVariable("project_id") Long projectId,
                                              @PathVariable("publish_app_id") Long publishAppId,
                                              @PathVariable("id") Long id) {
        return new ResponseEntity<>(mktPublishApplicationService.revocation(publishAppId, id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "删除应用及指定版本信息")//1.仅可删除未发布/已驳回/已撤销状态下的版本；2.应用下有已发布版本信息，则保留应用信息
    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteByVersion(@PathVariable("project_id") Long projectId,
                                          @PathVariable("publish_app_id") Long publishAppId,
                                          @PathVariable("id") Long id) {
        mktPublishApplicationService.delete(publishAppId, id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "更新已发布应用版本 - 查询信息")
    @GetMapping("/{id}")
    public ResponseEntity<MktVersionUpdateVO> getBeforeUpdate(@PathVariable("project_id") Long projectId,
                                                              @PathVariable("publish_app_id") Long publishAppId,
                                                              @PathVariable("id") Long id) {
        return new ResponseEntity<>(mktPublishVersionInfoService.getBeforeUpdateAndFix(id), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "更新已发布应用版本")
    @PutMapping("/{id}")
    public ResponseEntity<MktVersionUpdateVO> updateAndFix(@PathVariable("project_id") Long projectId,
                                                           @PathVariable("publish_app_id") Long publishAppId,
                                                           @PathVariable("id") Long id,
                                                           @RequestParam("organization_id") Long organizationId,
                                                           @RequestBody @Validated MktVersionUpdateVO updateVO) {
        return new ResponseEntity<>(mktPublishVersionInfoService.updateAndFix(organizationId, publishAppId, id, updateVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "更新已发布应用版本 - 重试")
    @GetMapping("/{id}/refix")
    public ResponseEntity<MktVersionUpdateVO> refix(@PathVariable("project_id") Long projectId,
                                                    @PathVariable("publish_app_id") Long publishAppId,
                                                    @PathVariable("id") Long id) {
        return new ResponseEntity<>(mktPublishVersionInfoService.refix(publishAppId, id), HttpStatus.OK);
    }
}