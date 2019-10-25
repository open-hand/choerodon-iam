package io.choerodon.base.app.service.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.pagehelper.PageInfo;
import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.app.service.AppVerDownloadRecordService;
import io.choerodon.base.infra.dto.AppVerDownloadRecordDTO;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.AppVerDownloadRecordMapper;
import io.choerodon.base.infra.mapper.RemoteTokenAuthorizationMapper;
import io.choerodon.base.infra.retrofit.AppMarketRetrofitClient;
import io.choerodon.base.infra.utils.PageUtils;
import io.choerodon.base.infra.utils.RetrofitCallExceptionParse;
import io.choerodon.core.exception.CommonException;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/8/27
 */
@Service
public class AppVerDownloadRecordServiceImpl implements AppVerDownloadRecordService {

    private static final String GET_APP_PUSHLISH_EXCEPTION = "error.published.app.query";

    @Value("${choerodon.market.saas.platform:false}")
    private boolean marketSaaSPlatform;

    private AppVerDownloadRecordMapper appVerDownloadRecordMapper;
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private RetrofitClientFactory retrofitClientFactory;
    private MarketFeignClient marketFeignClient;

    public AppVerDownloadRecordServiceImpl(AppVerDownloadRecordMapper appVerDownloadRecordMapper, RetrofitClientFactory retrofitClientFactory,
                                           RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, MarketFeignClient marketFeignClient) {
        this.appVerDownloadRecordMapper = appVerDownloadRecordMapper;
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.retrofitClientFactory = retrofitClientFactory;
        this.marketFeignClient = marketFeignClient;
    }

    @Override
    public PageInfo<AppVerDownloadRecordDTO> pagingAppDownloadRecord(Pageable pageable, String appName, String categoryName, Long organizationId,
                                                                     String downloader, String versionName, String status, String[] params) {
        // 如果是Pass平台则不需要组织间隔离
        if (!marketSaaSPlatform) {
            organizationId = null;
        }
        List<AppVerDownloadRecordDTO> appVerDownloadRecordList = appVerDownloadRecordMapper.fulltextSearch(appName, categoryName, organizationId, downloader, versionName, status, params);
        if (!appVerDownloadRecordList.isEmpty()) {
            Map<String, MktPublishApplicationDTO> appMap = getAppMap(appVerDownloadRecordList);
            // 过滤掉 已经在market上已经不存在的应用 -- 历史下载表的脏数据（可能是由于手动删除market应用导致）
            List<AppVerDownloadRecordDTO> resDownloadRecords = appVerDownloadRecordList
                    .stream()
                    .filter(v -> appMap.get(v.getMktAppCode()) != null)
                    .sorted(Comparator.comparing(AppVerDownloadRecordDTO::getId).reversed())
                    .collect(Collectors.toList());
            resDownloadRecords.forEach(v -> v.setMktAppImageUrl(appMap.get(v.getMktAppCode()).getImageUrl()));
            return PageUtils.createPageFromList(resDownloadRecords, pageable);
        }
        return new PageInfo<>();
    }

    private Map<String, MktPublishApplicationDTO> getAppMap(List<AppVerDownloadRecordDTO> appVerDownloadRecordList) {
        Set<String> appCodes = appVerDownloadRecordList.stream().map(AppVerDownloadRecordDTO::getMktAppCode).collect(Collectors.toSet());
        if (marketSaaSPlatform) {
            return marketFeignClient.getAppPublishMapByCode(appCodes).getBody();
        }
        RemoteTokenAuthorizationVO authorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
        if (ObjectUtils.isEmpty(authorizationVO) || !RemoteTokenStatus.SUCCESS.value().equals(authorizationVO.getStatus())) {
            throw new CommonException("error.latest.remote.token");
        }
        AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                authorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
        Call<ResponseBody> call = client.getAppPublishMapByCode(authorizationVO.getRemoteToken(), appCodes);
        return RetrofitCallExceptionParse.executeCallForMap(call, GET_APP_PUSHLISH_EXCEPTION, String.class, MktPublishApplicationDTO.class);
    }

}
