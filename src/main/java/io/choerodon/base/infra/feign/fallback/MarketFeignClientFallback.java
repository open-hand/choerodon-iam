package io.choerodon.base.infra.feign.fallback;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.*;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.base.infra.dto.mkt.HarborUserAndUrlVO;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/5
 */
public class MarketFeignClientFallback implements MarketFeignClient {

    @Override
    public ResponseEntity<DownloadInfoVO> queryDownloadInfo(Long versionId, Long organizationId, Set<Long> serviceVersions) {
        return null;
    }

    @Override
    public ResponseEntity<Set<Long>> listServiceVersionsByVersionId(Long versionId) {
        return null;
    }

    @Override
    public ResponseEntity<PublishedApplicationVO> getAppInfoWithVersionsAndPurchaseInfo(String appCode, Long organizationId) {
        throw new CommonException("error.feign.query.app.info");
    }

    @Override
    public ResponseEntity<PageInfo<RemoteApplicationVO>> getAppPublishesWithin(int page, int size, Long categoryId, String params, String orderBy, String order, List<String> appCodeList, Long organizationId) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<RemoteApplicationVO>> getMyDownloadAppWithin(int page, int size, String params, String orderBy, String order, List<String> appCodeList, Long organizationId) {
        return null;
    }


    @Override
    public ResponseEntity<List<MarketApplicationVersionVO>> getAppPublishVersionsByIdWithin(Long id) {
        return null;
    }


    @Override
    public ResponseEntity<MarketApplicationVO> getAppPublishDetailsByIdWithin(Long versionId, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<MarketApplicationVersionVO>> getAppPublishVersionDetailsByIdWithin(int page, int size, Long id) {
        return null;
    }

    @Override
    public ResponseEntity<List<AppCategoryDTO>> getCategoryListWithin() {
        return null;
    }


    @Override
    public ResponseEntity<List<AppVersionVO>> listServiceVersionsByVersionIds(Set<Long> versionIds) {
        return null;
    }

    @Override
    public ResponseEntity<Map<String, MktPublishApplicationDTO>> getAppPublishMapByCode(Set<String> appCodes) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> apply(MarketApplicationVO body, Long organizationId) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> reapply(MarketApplicationVO body, Long organizationId) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> updatePublishAppVersionInfo(String code, String version, MarketApplicationVersionVO body) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> revocation(String version, String code) {
        return null;
    }

    @Override
    public ResponseEntity<HarborUserAndUrlVO> confirm(String code, String version, MarketApplicationVO body) {
        return null;
    }

    @Override
    public ResponseEntity<HarborUserAndUrlVO> fixConfirm(String code) {
        return null;
    }

    @Override
    public ResponseEntity<List<ApproveStatusVO>> getStatus(List<ApproveStatusVO> approveStatusVOS) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> checkMktAppName(String name, String code) {
        return null;
    }

    @Override
    public ResponseEntity<PageInfo<AppCategoryDTO>> getEnableCategoryList(int page, int size) {
        return null;
    }

    @Override
    public ResponseEntity<Boolean> updateAppPublishInfoDetails(String code, MarketApplicationVO body) {
        return null;
    }

    @Override
    public ResponseEntity<DownloadInfoVO> queryDownloadAppInfo(Long versionId, Long organizationId) {
        return null;
    }
}
