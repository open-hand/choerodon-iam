package io.choerodon.base.infra.feign;


import com.github.pagehelper.PageInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.base.infra.dto.mkt.HarborUserAndUrlVO;


@FeignClient(value = "market-service")
public interface MarketFeignClient {

    @GetMapping("/v1/applications/with_in/versions/{version_id}/download_info")
    ResponseEntity<DownloadInfoVO> queryDownloadInfo(@PathVariable("version_id") Long versionId,
                                                     @RequestParam("organization_id") Long organizationId,
                                                     @RequestParam("service_versions") Set<Long> serviceVersions);

    @GetMapping("/v1/site/versions/{version_id}/services/with_in")
    ResponseEntity<Set<Long>> listServiceVersionsByVersionId(@PathVariable("version_id") Long versionId);

    @GetMapping("/v1/applications/with_in/{app_code}/download_info")
    ResponseEntity<PublishedApplicationVO> getAppInfoWithVersionsAndPurchaseInfo(@PathVariable("app_code") String appCode,
                                                                                 @RequestParam("organization_id") Long organizationId);

    @PostMapping("/v1/public/app_publishes/by_token/within")
    ResponseEntity<PageInfo<RemoteApplicationVO>> getAppPublishesWithin(@RequestParam("page") int page,
                                                                        @RequestParam("size") int size,
                                                                        @RequestParam("categoryId") Long categoryId,
                                                                        @RequestParam("params") String params,
                                                                        @RequestParam("orderBy") String orderBy,
                                                                        @RequestParam(value = "order", defaultValue = "DESC") String order,
                                                                        @RequestBody List<String> appCodeList,
                                                                        @RequestParam(value = "organizationId") Long organizationId);

    @PostMapping(value = "/v1/public/download/by_token/within")
    ResponseEntity<PageInfo<RemoteApplicationVO>> getMyDownloadAppWithin(@RequestParam("page") int page,
                                                                         @RequestParam("size") int size,
                                                                         @RequestParam("params") String params,
                                                                         @RequestParam("orderBy") String orderBy,
                                                                         @RequestParam(value = "order", defaultValue = "DESC") String order,
                                                                         @RequestBody List<String> appCodeList,
                                                                         @RequestParam(value = "organizationId") Long organizationId);

    @GetMapping(value = "/v1/public/{app_id}/versions/within")
    ResponseEntity<List<MarketApplicationVersionVO>> getAppPublishVersionsByIdWithin(@PathVariable(name = "app_id") Long id);


    @GetMapping(value = "/v1/public/paas_app/{id}/within")
    ResponseEntity<MarketApplicationVO> getAppPublishDetailsByIdWithin(@RequestParam(name = "version_id") Long versionId,
                                                                       @PathVariable(name = "id") Long id);

    @GetMapping(value = "/v1/public/paas_app_version/{id}/within")
    ResponseEntity<PageInfo<MarketApplicationVersionVO>> getAppPublishVersionDetailsByIdWithin(@RequestParam("page") int page,
                                                                                               @RequestParam("size") int size,
                                                                                               @PathVariable(name = "id") Long id);

    @GetMapping(value = "/v1/market_app_categories/list/within")
    ResponseEntity<List<AppCategoryDTO>> getCategoryListWithin();

    // 申请发布应用 / 已撤销重新申请
    @PostMapping("/v1/market_applications/createWithin")
    ResponseEntity<Boolean> apply(@RequestBody MarketApplicationVO body,
                                  @RequestParam(value = "organization_id") Long organizationId);

    // 被驳回重新申请
    @PutMapping("/v1/market_applications/updateApproveWithin")
    ResponseEntity<Boolean> reapply(@RequestBody MarketApplicationVO body,
                                    @RequestParam(value = "organization_id") Long organizationId);

    // 已发布应用保存应用及版本更新信息
    @PutMapping("/v1/market_applications/publishedWithin")
    ResponseEntity<Boolean> updatePublishAppVersionInfo(@RequestParam("app_code") String code,
                                                        @RequestParam("version") String version,
                                                        @RequestBody MarketApplicationVersionVO body);

    // 撤销
    @DeleteMapping("/v1/market_applications/revertAppWithin")
    ResponseEntity<Boolean> revocation(@RequestParam("version") String version,
                                       @RequestParam("code") String code);

    // 提交确认信息并获得发布参数
    @PostMapping("/v1/market_applications/harbor_url/within")
    ResponseEntity<HarborUserAndUrlVO> confirm(@RequestParam("app_code") String code,
                                               @RequestParam("version") String version,
                                               @RequestBody MarketApplicationVO body);

    // 修复版本时请求发布参数
    @GetMapping("/v1/market_applications/harbor_url/within")
    ResponseEntity<HarborUserAndUrlVO> fixConfirm(@RequestParam("app_code") String code);

    // 请求更新状态
    @PostMapping("/v1/market_applications/approveStatusWithin")
    ResponseEntity<List<ApproveStatusVO>> getStatus(@RequestBody List<ApproveStatusVO> approveStatusVOS);

    // 修复版本时请求发布参数
    @GetMapping("/v1/market_applications/check_name/within")
    ResponseEntity<Boolean> checkMktAppName(@RequestParam("name") String name,
                                            @RequestParam("code") String code);

    @GetMapping("v1/market_app_categories/list/enable/within")
    public ResponseEntity<PageInfo<AppCategoryDTO>> getEnableCategoryList(@RequestParam("page") int page,
                                                                          @RequestParam("size") int size);


    @GetMapping(value = "/v1/site/versions/services/with_in")
    ResponseEntity<List<AppVersionVO>> listServiceVersionsByVersionIds(@RequestParam("version_ids") Set<Long> versionIds);

    @GetMapping(value = "/v1/market_app_publish/map_by_code")
    ResponseEntity<Map<String, MktPublishApplicationDTO>> getAppPublishMapByCode(@RequestParam("appCodes") Set<String> appCodes);

    // 修改已发布应用发送消息
    @PutMapping("/v1/market_applications/info/within")
    ResponseEntity<Boolean> updateAppPublishInfoDetails(@RequestParam(value = "app_code") String code,
                                                        @RequestBody MarketApplicationVO body);

    @GetMapping("/v1/applications/with_in/versions/{version_id}/app_info")
    ResponseEntity<DownloadInfoVO> queryDownloadAppInfo(@PathVariable("version_id") Long versionId, @RequestParam("organization_id") Long organizationId);
}
