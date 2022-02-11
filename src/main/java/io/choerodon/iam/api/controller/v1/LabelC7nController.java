package io.choerodon.iam.api.controller.v1;

import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hzero.core.base.BaseController;
import org.hzero.iam.app.service.LabelService;
import org.hzero.iam.domain.entity.Label;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.app.service.LabelC7nService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * @author superlee
 */

@Api(tags = C7nSwaggerApiConfig.CHOERODON_LABEL)
@RestController
@RequestMapping(value = "/choerodon/v1/labels")
public class LabelC7nController extends BaseController {

    private LabelService labelService;
    private LabelC7nService labelC7nService;

    public LabelC7nController(LabelService labelService, LabelC7nService labelC7nService) {
        this.labelService = labelService;
        this.labelC7nService = labelC7nService;
    }


    @Permission(level = ResourceLevel.SITE)
    @ApiOperation(value = "通过类型查询label")
    @GetMapping
    public ResponseEntity<List<Label>> listByType(Label label) {
        return new ResponseEntity<>(labelService.getLabelListByType(label.getType()), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "通过类型查询组织层label")
    @GetMapping(value = "/org/{organization_id}")
    public ResponseEntity<List<Label>> listByTypeAtOrg(
            @PathVariable(name = "organization_id") Long organizationId,
                                                       Label label) {
        label.setFdLevel(ResourceLevel.ORGANIZATION.value());
        return new ResponseEntity<>(labelC7nService.listByOption(label), HttpStatus.OK);
    }

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询项目层角色gitlab标签")
    @GetMapping("/project_gitlab_labels")
    public ResponseEntity<List<Label>> listProjectGitlabLabels() {
        return ResponseEntity.ok(labelC7nService.listProjectGitlabLabels());
    }

}
