package io.choerodon.iam.api.controller.v1;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.ResourceLimitVO;
import io.choerodon.iam.app.service.OrganizationResourceLimitService;
import io.choerodon.iam.infra.config.C7nSwaggerApiConfig;
import io.choerodon.swagger.annotation.Permission;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/4/20
 * @Modified By:
 */
@Api(tags = C7nSwaggerApiConfig.CHOERODON_RESOURCE_LIMIT)
@RestController
@RequestMapping("/choerodon/v1/organizations/{organization_id}/resource_limit")
public class ResourceLimitController {

    @Autowired
    private OrganizationResourceLimitService resourceLimitService;

    @Permission(level = ResourceLevel.ORGANIZATION)
    @ApiOperation(value = "查询组织资源")
    @GetMapping
    public ResponseEntity<ResourceLimitVO> queryReportList(@PathVariable(value = "organization_id") Long organizationId) {
        resourceLimitService.queryResourceLimit(organizationId);
        return new ResponseEntity<>(resourceLimitService.queryResourceLimit(organizationId), HttpStatus.OK);
    }

}
