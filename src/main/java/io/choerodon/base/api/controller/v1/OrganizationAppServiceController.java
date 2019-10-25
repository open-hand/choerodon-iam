package io.choerodon.base.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.AppServiceDetailsVO;
import io.choerodon.base.app.service.ApplicationServiceService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/7
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/applications/{application_id}/services")
public class OrganizationAppServiceController extends BaseController {

    private ApplicationServiceService applicationServiceService;

    public OrganizationAppServiceController(ApplicationServiceService applicationServiceService) {
        this.applicationServiceService = applicationServiceService;
    }

    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "查询应用的服务")
    @GetMapping("/list")
    public ResponseEntity<List<AppServiceDetailsVO>> listAppSvcByOptions(@PathVariable("organization_id") Long organizationId,
                                                                         @PathVariable("application_id") Long applicationId) {
        return new ResponseEntity<>(applicationServiceService.listAppSvc(organizationId, applicationId), HttpStatus.OK);
    }
}
