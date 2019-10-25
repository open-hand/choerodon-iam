package io.choerodon.base.infra.retrofit;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.api.vo.RemoteApplicationVO;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Set;

/**
 * @author wuguokai
 * @date 2019/8/13
 */
public interface AppMarketRetrofitClient {

    @POST("/market/v1/public/app_publishes/by_token")
    Call<PageInfo<RemoteApplicationVO>> getSaasAppMarket(@Query("remote_token") String token, @Query("params") String param, @Query("categoryId") Long categoryId, @Query("orderBy") String orderBy, @Query("order") String order, @Query("page") Integer page, @Query("size") Integer size, @Body List<String> appCodeList);

    @GET("/market/v1/public/paas_app/{id}")
    Call<MarketApplicationVO> getAppPublishDetailsById(@Path("id") Long id, @Query("remote_token") String token, @Query("version_id") Long versionId);

    @GET("/market/v1/market_app_categories/list")
    Call<List<AppCategoryDTO>> getCategoryList(@Query("remote_token") String token);

    @GET("/market/v1/public/remote_token")
    Call<Boolean> validateToken(@Query("remote_token") String token);

    @POST("/market/v1/public/download/by_token")
    Call<PageInfo<RemoteApplicationVO>> getMyDownloadApp(@Query("remote_token") String token, @Query("params") String param, @Query("orderBy") String orderBy, @Query("order") String order, @Query("page") Integer page, @Query("size") Integer size, @Body List<String> appCodeList);

    @GET("/market/v1/public/paas_app_version/{id}")
    Call<PageInfo<MarketApplicationVersionVO>> getAppPublishVersionDetailsById(@Path("id") Long id, @Query("remote_token") String token, @Query("page") Integer page, @Query("size") Integer size);

    @GET("/market/v1/public/app_publishes/by_code")
    Call<ResponseBody> getAppPublishMapByCode(@Query("remote_token") String token, @Query("appCodes") Set<String> appCodes);

    @GET("/market/v1/public/{app_id}/versions")
    Call<List<MarketApplicationVersionVO>> getAppPublishVersionsById(@Path("app_id") Long id, @Query("remote_token") String token);

    @GET("/market/v1/public/versions/{version_id}/services")
    Call<ResponseBody> listServiceVersionsByVersionId(@Path("version_id") Long versionId,
                                                      @Query("remote_token") String remoteToken);

    @GET("/market/v1/public/versions/services")
    Call<ResponseBody> listServiceVersionsByVersionIds(@Query("version_ids") Set<Long> versionIds,
                                                       @Query("remote_token") String remoteToken);

    @GET("/market/v1/applications/{app_code}/download_info")
    Call<ResponseBody> getAppInfoWithVersionsAndPurchaseInfo(@Path("app_code") String appCode, @Query("remote_token") String token);

    @PUT("/market/v1/market_applications/info")
    Call<ResponseBody> updateAppPublishInfoDetails(@Query(value = "remote_token") String token, @Query(value = "app_code") String code, @Body MarketApplicationVO body);

    @Multipart
    @POST("/market/v1/market_applications/uploadImage")
    Call<ResponseBody> cutImage(@Query(value = "remote_token") String token,
                                @Part MultipartBody.Part file,
                                @Query(value = "rotate") Double rotate,
                                @Query(value = "startX") Integer axisX,
                                @Query(value = "startY") Integer axisY,
                                @Query(value = "endX") Integer width,
                                @Query(value = "endY") Integer height);
}