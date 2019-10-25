package io.choerodon.base.app.service.impl;

import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import retrofit2.Call;

import io.choerodon.base.api.vo.AppVersionVO;
import io.choerodon.base.api.vo.PublishedApplicationVO;
import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.app.service.MarketCallService;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.RemoteTokenAuthorizationMapper;
import io.choerodon.base.infra.retrofit.AppMarketRetrofitClient;
import io.choerodon.base.infra.utils.RetrofitCallExceptionParse;
import io.choerodon.core.exception.CommonException;

/**
 * @author wanghao
 * @since 2019/9/9
 */
@Service
public class MarketCallServiceImpl implements MarketCallService {

    private static final String ERROR_LATEST_REMOTE_TOKEN = "error.latest.remote.token";
    private static final String ERROR_APPLICATION_NOT_FOUND = "error.application.not.found";
    @Value("${choerodon.market.saas.platform}")
    private boolean isSassPlatform;

    private MarketFeignClient marketFeignClient;
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private RetrofitClientFactory retrofitClientFactory;

    public MarketCallServiceImpl(MarketFeignClient marketFeignClient, RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, RetrofitClientFactory retrofitClientFactory) {
        this.marketFeignClient = marketFeignClient;
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.retrofitClientFactory = retrofitClientFactory;
    }

    @Override
    public PublishedApplicationVO getPublishedApplication(String appCode, Long organizationId) {
        PublishedApplicationVO publishedApplicationVO;
        if (isSassPlatform) {
            Assert.notNull(organizationId, "error.organization.id.null");
            publishedApplicationVO = marketFeignClient.getAppInfoWithVersionsAndPurchaseInfo(appCode, organizationId).getBody();
        } else {
            RemoteTokenAuthorizationVO authorizationVO = checkAndGetLatestToken();
            AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                    authorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
            Call<ResponseBody> call = client.getAppInfoWithVersionsAndPurchaseInfo(appCode, authorizationVO.getRemoteToken());
            publishedApplicationVO = RetrofitCallExceptionParse.executeCall(call, "error.application.not.found", PublishedApplicationVO.class);
        }
        return publishedApplicationVO;
    }

    @Override
    public Set<Long> listServiceVersionsByVersionId(Long versionId) {
        Set<Long> versions;
        if (isSassPlatform) {
            versions = marketFeignClient.listServiceVersionsByVersionId(versionId).getBody();
        } else {
            RemoteTokenAuthorizationVO authorizationVO = checkAndGetLatestToken();
            AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                    authorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
            Call<ResponseBody> call = client.listServiceVersionsByVersionId(versionId, authorizationVO.getRemoteToken());
            versions = RetrofitCallExceptionParse.executeCall(call, ERROR_APPLICATION_NOT_FOUND, Set.class);
        }
        return versions;
    }

    @Override
    public List<AppVersionVO> listServiceVersionsByVersionIds(Set<Long> versionIds) {
        List<AppVersionVO> appVersionVOS;
        if (isSassPlatform) {
            appVersionVOS = marketFeignClient.listServiceVersionsByVersionIds(versionIds).getBody();
        } else {
            RemoteTokenAuthorizationVO authorizationVO = checkAndGetLatestToken();
            AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                    authorizationVO.getAuthorizationUrl(), AppMarketRetrofitClient.class);
            Call<ResponseBody> call = client.listServiceVersionsByVersionIds(versionIds, authorizationVO.getRemoteToken());
            appVersionVOS = RetrofitCallExceptionParse.executeCallForList(call, ERROR_APPLICATION_NOT_FOUND, AppVersionVO.class);
        }
        return appVersionVOS;

    }

    private RemoteTokenAuthorizationVO checkAndGetLatestToken() {
        RemoteTokenAuthorizationVO authorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
        if (ObjectUtils.isEmpty(authorizationVO) || !RemoteTokenStatus.SUCCESS.value().equals(authorizationVO.getStatus())) {
            throw new CommonException(ERROR_LATEST_REMOTE_TOKEN);
        }
        return authorizationVO;
    }
}
