package io.choerodon.base.api.controller.v1;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.MktPublishAppVersionVO;
import io.choerodon.base.api.validator.UnPublishVersionUpdate;
import io.choerodon.base.api.vo.MktUnPublishVersionInfoVO;
import io.choerodon.base.app.service.MktPublishVersionInfoService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengyuhua
 */
@RestController
@RequestMapping("/v1/projects/{project_id}/publish_version_infos")
public class MktPublishVersionInfoController {

    private MktPublishVersionInfoService mktPublishVersionInfoService;

    public MktPublishVersionInfoController(MktPublishVersionInfoService mktPublishVersionInfoService) {
        this.mktPublishVersionInfoService = mktPublishVersionInfoService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "编辑未发布版本信息(未发布/已撤销/被驳回)")
    @PutMapping
    public ResponseEntity<MktPublishAppVersionVO> updatePublished(@PathVariable("project_id") Long projectId,
                                                                  @RequestParam(value = "organization_id") Long organizationId,
                                                                  @RequestParam("apply") Boolean apply,
                                                                  @RequestBody @Validated(UnPublishVersionUpdate.class) MktUnPublishVersionInfoVO publishAppVO) {
        return new ResponseEntity<>(mktPublishVersionInfoService.updateUnPublished(organizationId, projectId, publishAppVO, apply), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "更新发布失败状态")
    @PutMapping(value = "/{id}/fail")
    public ResponseEntity<Boolean> publishFail(@PathVariable("project_id") Long projectId,
                                               @PathVariable("id") Long id,
                                               @RequestParam("error_code") String errorCode,
                                               @RequestParam("fix_flag") Boolean fixFlag) {
        return new ResponseEntity(mktPublishVersionInfoService.publishFail(id, errorCode, fixFlag, projectId), HttpStatus.OK);
    }
}
