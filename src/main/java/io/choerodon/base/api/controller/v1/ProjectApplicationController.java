package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;
import java.util.Set;

import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.Update;
import io.choerodon.base.api.vo.ApplicationReqVO;
import io.choerodon.base.api.vo.ApplicationRespVO;
import io.choerodon.base.api.vo.ApplicationVO;
import io.choerodon.base.app.service.ApplicationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.core.base.BaseController;
import io.choerodon.core.iam.InitRoleCode;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@RestController
@RequestMapping(value = "/v1/projects/{project_id}/applications")
public class ProjectApplicationController extends BaseController {

    private ApplicationService applicationService;

    public ProjectApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @ApiOperation(value = "创建应用")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @PostMapping
    public ResponseEntity<ApplicationDTO> createApplication(@PathVariable("project_id") Long projectId,
                                                            @RequestBody @Validated(Insert.class) ApplicationReqVO applicationReqVO) {
        return new ResponseEntity<>(applicationService.createApplication(projectId, applicationReqVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据主键更新应用")
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationDTO> updateApplication(@PathVariable("project_id") Long projectId,
                                                            @PathVariable("id") Long id,
                                                            @RequestBody @Validated(Update.class) ApplicationReqVO applicationReqVO) {
        applicationReqVO.setId(id);
        return new ResponseEntity<>(applicationService.updateApplication(applicationReqVO), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "根据主键更新应用code")
    @PutMapping("/{id}/refresh/code")
    public ResponseEntity<ApplicationDTO> refreshAppCode(@PathVariable("project_id") Long projectId,
                                                         @PathVariable("id") Long id,
                                                         @RequestParam("object_version_number") Long objectVersionNumber) {
        return new ResponseEntity<>(applicationService.updateApplicationFeedbackToken(id, objectVersionNumber), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "name唯一性校验")
    @GetMapping("/check/{name}")
    public ResponseEntity<Boolean> checkName(@PathVariable("project_id") Long projectId,
                                             @PathVariable("name") String name) {
        return new ResponseEntity<>(applicationService.checkName(projectId, name), HttpStatus.OK);
    }

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "分页查询项目下的所有应用信息，带有是否可修改标识")
    @CustomPageRequest
    @GetMapping("/pagingByOptions")
    public ResponseEntity<PageInfo<ApplicationRespVO>> pagingAppByOptions(@PathVariable("project_id") Long projectId,
                                                                          @ApiIgnore
                                                                          @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                          @RequestParam(required = false) String name,
                                                                          @RequestParam(required = false) String description,
                                                                          @RequestParam(required = false) String projectName,
                                                                          @RequestParam(required = false) String creatorRealName,
                                                                          @RequestParam(required = false) String[] params) {
        return new ResponseEntity<>(applicationService.pagingProjectAppByOptions(projectId, name, description, projectName, creatorRealName, params, Pageable), HttpStatus.OK);
    }

    @GetMapping("/brief_info")
    @ApiOperation(value = "查询项目下未生成发布信息的应用（简要信息列表）")
    @Permission(type = ResourceType.PROJECT)
    public ResponseEntity<List<ApplicationDTO>> getAppBriefInfo(@PathVariable("project_id") Long projectId) {
        return new ResponseEntity<>(applicationService.getAppBriefInfo(projectId, false), HttpStatus.OK);
    }

    @Permission(permissionWithin = true)
    @ApiOperation(value = "根据应用服务ID查询关联应用信息")
    @GetMapping("/query_by_service_ids")
    public ResponseEntity<List<ApplicationVO>> listApplicationInfoByServiceIds(@PathVariable("project_id") Long projectId,
                                                                               @RequestParam("service_ids") Set<Long> serviceIds) {
        return new ResponseEntity<>(applicationService.listApplicationInfoByServiceIds(projectId, serviceIds), HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}")
    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_OWNER})
    @ApiOperation(value = "删除应用")
    public ResponseEntity deleteOrgCategory(@PathVariable(name = "id") Long id) {
        applicationService.deleteApplication(id);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
