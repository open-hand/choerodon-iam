package io.choerodon.base.api.controller.v1;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.ApplicationServiceService;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/26
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/services")
public class OrganizationServiceController extends BaseController {

    private ApplicationServiceService applicationServiceService;

    public OrganizationServiceController(ApplicationServiceService applicationServiceService) {
        this.applicationServiceService = applicationServiceService;
    }

    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "根据应用类型查询组织下的所有服务")
    @GetMapping("/{app_type}")
    public ResponseEntity<Set<Long>> listService(@PathVariable("organization_id") Long organizationId,
                                                 @PathVariable("app_type") String appType) {
        return new ResponseEntity<>(applicationServiceService.listService(organizationId, appType), HttpStatus.OK);
    }

    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "根据应用类型查询服务下的所有版本")
    @GetMapping("/{app_type}/versions")
    public ResponseEntity<Set<Long>> listSvcVersion(@PathVariable("organization_id") Long organizationId,
                                                    @PathVariable("app_type") String appType) {
        return new ResponseEntity<>(applicationServiceService.listSvcVersion(organizationId, appType), HttpStatus.OK);
    }
}
