package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.app.service.ApplicationServiceService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.devops.AppServiceRepVO;
import io.choerodon.base.infra.dto.devops.AppServiceVO;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/7
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/services")
public class ProjectServiceController extends BaseController {

    private ApplicationServiceService applicationServiceService;

    public ProjectServiceController(ApplicationServiceService applicationServiceService) {
        this.applicationServiceService = applicationServiceService;
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询本项目下的所有服务")
    @CustomPageRequest
    @GetMapping("/paging/project")
    public ResponseEntity<PageInfo<AppServiceVO>> pagingProServiceByOptions(@PathVariable("project_id") Long projectId,
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
        return new ResponseEntity<>(applicationServiceService.pagingProServiceByOptions(Pageable, projectId, null, appServiceVO, params, false), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询所有共享服务")
    @CustomPageRequest
    @GetMapping("/paging/shared")
    public ResponseEntity<PageInfo<AppServiceRepVO>> pagingSharedServiceByOptions(@PathVariable("project_id") Long projectId,
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
        return new ResponseEntity<>(applicationServiceService.pagingSharedServiceByOptions(Pageable, projectId, null, appServiceVO, params, false), HttpStatus.OK);
    }
}
