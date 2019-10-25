package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.vo.ApplicationRespVO;
import io.choerodon.base.app.service.ApplicationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.core.base.BaseController;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/applications")
public class OrganizationApplicationController extends BaseController {

    private ApplicationService applicationService;

    public OrganizationApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @Permission(type = ResourceType.ORGANIZATION, permissionLogin = true)
    @ApiOperation(value = "分页查询组织下的所有应用信息")
    @CustomPageRequest
    @GetMapping("/pagingByOptions")
    public ResponseEntity<PageInfo<ApplicationRespVO>> pagingAppByOptions(@PathVariable("organization_id") Long organizationId,
                                                                          @ApiIgnore
                                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                          @RequestParam(required = false) String type,
                                                                          @RequestParam(required = false) String name,
                                                                          @RequestParam(required = false) String description,
                                                                          @RequestParam(required = false, value = "project_name") String projectName,
                                                                          @RequestParam(required = false, value = "creator_real_name") String creatorRealName,
                                                                          @RequestParam(required = false, value = "created_by") Long createBy,
                                                                          @RequestParam(required = false) Long participant,
                                                                          @RequestParam(required = false) Boolean all,
                                                                          @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(applicationService.pagingOrgAppByOptions(organizationId, type, name, description, projectName, creatorRealName, createBy, participant, all, params, Pageable), HttpStatus.OK);
    }

}
