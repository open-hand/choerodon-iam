package io.choerodon.iam.infra.feign;

import java.util.Date;
import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.iam.api.vo.BarLabelRotationItemVO;
import io.choerodon.iam.api.vo.devops.UserAttrVO;
import io.choerodon.iam.infra.feign.fallback.DevopsFeignClientFallback;

@FeignClient(value = "devops-service", fallback = DevopsFeignClientFallback.class)
public interface DevopsFeignClient {
    // todo devops
//    /**
//     * 校验email在gitlab中是否已经使用
//     *
//     * @param email 邮箱
//     * @return 校验结果
//     */
//    @GetMapping(value = "/gitlab/email/check")
//    ResponseEntity<Boolean> checkGitlabEmail(@RequestParam(value = "email") String email);
//
//    @PostMapping("/v1/organizations/app_market/page_app_services")
//    ResponseEntity<PageInfo<AppServiceUploadPayload>> pageByAppId(@RequestParam(value = "app_id") Long appId,
//                                                                  @RequestParam("page") int page,
//                                                                  @RequestParam("size") int size);
//
//    /**
//     * 根据应用服务ID查询所对应的应用版本
//     *
//     * @param appServiceId 应用服务Id
//     * @return 应用服务版本信息列表
//     */
//    @GetMapping("/v1/organizations/app_market/list_versions/{app_service_id}")
//    ResponseEntity<List<AppServiceVersionUploadPayload>> listVersionsByAppServiceId(@PathVariable(value = "app_service_id") Long appServiceId);
//
//    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_app_service_ids")
//    ResponseEntity<PageInfo<AppServiceDetailsVO>> batchQueryAppService(
//            @PathVariable(value = "project_id") Long projectId,
//            @RequestParam(value = "ids") Set<Long> ids,
//            @RequestParam(value = "doPage", required = false) Boolean doPage,
//            @RequestParam("page") int page,
//            @RequestParam("size") int size,
//            @RequestParam("sort") List<String> sort,
//            @RequestBody(required = false) String params);
//
//    @PostMapping(value = "/v1/organizations/{organization_id}/app_service/list_app_service_ids")
//    ResponseEntity<PageInfo<AppServiceDetailsVO>> batchQueryAppServiceWithOrg(
//            @PathVariable(value = "organization_id") Long organizationsId,
//            @RequestParam(value = "ids") Set<Long> ids,
//            @RequestParam(value = "doPage", required = false) Boolean doPage,
//            @RequestParam("page") int page,
//            @RequestParam("size") int size,
//            @RequestParam(value = "sort") List<String> sort,
//            @RequestBody(required = false) String params);
//
//    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_by_project_id")
//    ResponseEntity<PageInfo<AppServiceVO>> listAppByProjectId(
//            @PathVariable(value = "project_id") Long projectId,
//            @RequestParam(value = "doPage", required = false) Boolean doPage,
//            @RequestParam("page") int page,
//            @RequestParam("size") int size,
//            @RequestParam("sort") List<String> sort,
//            @RequestBody(required = false) String params);
//
//    @PostMapping(value = "/v1/projects/{project_id}/app_service/page_share_app_service")
//    ResponseEntity<PageInfo<AppServiceRepVO>> pageShareApps(
//            @PathVariable(value = "project_id") Long projectId,
//            @RequestParam(value = "doPage", required = false) Boolean doPage,
//            @RequestParam("page") int page,
//            @RequestParam("size") int size,
//            @RequestParam("sort") List<String> sort,
//            @RequestBody(required = false) String searchParam);
//
//    @PostMapping("v1/organizations/app_market/list_versions")
//    ResponseEntity<List<AppServiceAndVersionDTO>> getSvcVersionByVersionIds(
//            @RequestBody List<AppServiceAndVersionDTO> versionVOList);
//
//    @GetMapping(value = "/v1/projects/{project_id}/app_service/list_service_by_version_ids")
//    ResponseEntity<List<AppServiceVO>> listServiceByVersionIds(
//            @PathVariable(value = "project_id") Long projectId,
//            @RequestParam(value = "version_ids") Set<Long> ids);
//
//    @GetMapping(value = "/v1/projects/{project_id}/app_service_versions/list_by_service_id")
//    ResponseEntity<List<AppServiceVersionVO>> listVersionById(
//            @ApiParam(value = "项目ID", required = true)
//            @PathVariable(value = "project_id") Long projectId,
//            @ApiParam(value = "应用服务id", required = true)
//            @RequestParam(value = "app_service_id", required = true) String id,
//            @ApiParam(value = "查询参数", required = false)
//            @RequestParam(value = "params", required = false) String params);
//
//    @GetMapping(value = "/v1/projects/{project_id}/app_service/list_by_project_id")
//    ResponseEntity<Map<Long, Integer>> countAppServerByProjectId(@ApiParam(value = "项目ID", required = true)
//                                                                 @PathVariable(value = "project_id") Long projectId,
//                                                                 @RequestParam("longList") List<Long> longList);
//
    @GetMapping("/v1/projects/{project_id}/deploy_record/count_by_date")
    ResponseEntity<BarLabelRotationItemVO> countByDate(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam("startTime") Date startTime,
            @RequestParam("endTime") Date endTime);

    @PostMapping("/v1/users/list_by_ids")
    ResponseEntity<List<UserAttrVO>> listByUserIds(
            @ApiParam(value = "用户id", required = true)
            @RequestBody Set<Long> iamUserIds);
}
