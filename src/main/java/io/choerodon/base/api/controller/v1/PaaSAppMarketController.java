package io.choerodon.base.api.controller.v1;

import com.github.pagehelper.PageInfo;
import io.choerodon.core.annotation.Permission;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.CustomerApplicationVersionVO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.api.vo.RemoteApplicationVO;
import io.choerodon.base.app.service.PublishApplicationService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.choerodon.core.enums.ResourceType;
import io.choerodon.base.infra.utils.ParamUtils;
import org.springframework.data.web.SortDefault;
import io.choerodon.swagger.annotation.CustomPageRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/8/7
 */
@RestController
@RequestMapping("/v1/paas_app_market")
public class PaaSAppMarketController {
    private PublishApplicationService publishApplicationService;

    public PaaSAppMarketController(PublishApplicationService publishApplicationService) {
        this.publishApplicationService = publishApplicationService;
    }

    @GetMapping
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "分页查询PaaS平台应用市场")
    @CustomPageRequest
    public ResponseEntity<PageInfo<RemoteApplicationVO>> getPaasAppMarkets(@ApiIgnore
                                                                           @SortDefault(value = "id", direction = Sort.Direction.DESC) Pageable Pageable,
                                                                           @RequestParam(required = false) Long categoryId,
                                                                           @RequestParam(required = false) String[] param,
                                                                           @RequestParam(required = false) String orderBy,
                                                                           @RequestParam(defaultValue = "DESC") String order,
                                                                           @RequestParam(defaultValue = "false") Boolean isMyDownload,
                                                                           @RequestParam(required = false) Long organizationId
    ) {

        return new ResponseEntity<>(publishApplicationService.pagingQueryPaasAppMarket(Pageable, ParamUtils.arrToStr(param), categoryId, orderBy, order, isMyDownload, organizationId), HttpStatus.OK);

    }

    @GetMapping("/newVersionNum")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "PaaS下载中心获取新版本/修复版本数量")
    public ResponseEntity<Integer> getNewVersionNum(@RequestParam(required = false) Long organizationId) {
        return new ResponseEntity<>(publishApplicationService.getNewVersionNum(organizationId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "Paas平台应用市场查询应用详情")
    public ResponseEntity<MarketApplicationVO> getAppPublishDetailsById(@PathVariable(name = "id") Long id,
                                                                        @RequestParam(value = "version_id", required = false) Long versionId,
                                                                        @RequestParam(required = false, value = "organization_id") Long organizationId) {

        return new ResponseEntity<>(publishApplicationService.getPaasAppMarketById(id, versionId, organizationId), HttpStatus.OK);

    }

    @GetMapping("/version/{id}")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "Paas平台应用市场查询应用版本详情")
    @CustomPageRequest
    public ResponseEntity<PageInfo<MarketApplicationVersionVO>> getAppPublishVersionDetailsById(@ApiIgnore Pageable Pageable,
                                                                                                @PathVariable(name = "id") Long id,
                                                                                                @RequestParam(defaultValue = "0", value = "organization_id") Long organizationId) {

        return new ResponseEntity<>(publishApplicationService.getAppPublishVersionDetailsById(Pageable, id, organizationId), HttpStatus.OK);

    }

    @GetMapping("/applications/{app_code}")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "Paas平台应用市场查询应用信息，以及应用版本下载列表")
    public ResponseEntity<List<CustomerApplicationVersionVO>> listDownloadInfo(@PathVariable(name = "app_code") String appCode,
                                                                               @RequestParam(value = "version", required = false) String version,
                                                                               @RequestParam(value = "organization_id") Long organizationId) {

        return new ResponseEntity<>(publishApplicationService.listDownloadInfo(appCode, version, organizationId), HttpStatus.OK);

    }


    @GetMapping("/category")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "PaaS查询应用类型列表")
    @CustomPageRequest
    public ResponseEntity<List<AppCategoryDTO>> getSaasAppCategory() {
        return new ResponseEntity<>(publishApplicationService.getAppCategories(), HttpStatus.OK);

    }

    @GetMapping("/{app_id}/versions")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "PaaS查询应用已发布版本列表")
    @CustomPageRequest
    public ResponseEntity<List<MarketApplicationVersionVO>> getAppVersions(@PathVariable(value = "app_id") Long id) {
        return new ResponseEntity<>(publishApplicationService.getAppVersionsById(id), HttpStatus.OK);
    }

    @GetMapping("/token")
    @Permission(type = ResourceType.SITE, permissionLogin = true)
    @ApiOperation(value = "PaaS查询当前token是否可用")
    public ResponseEntity<Boolean> validateRemoteToken() {
        return new ResponseEntity<>(publishApplicationService.validateRemoteToken(), HttpStatus.OK);
    }
}
