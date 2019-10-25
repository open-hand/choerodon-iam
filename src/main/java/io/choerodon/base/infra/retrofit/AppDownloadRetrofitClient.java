package io.choerodon.base.infra.retrofit;

import com.github.pagehelper.PageInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Set;

import io.choerodon.base.api.vo.RemoteApplicationVO;

/**
 * @author wanghao
 * @Date 2019/8/23 15:04
 */
public interface AppDownloadRetrofitClient {
    @GET("/market/v1/applications/{app_code}/download_info")
    Call<ResponseBody> listDownloadInfo(@Path("app_code") String appCode, @Query("remote_token") String token);

    @POST("/market/v1/public/download/by_token")
    Call<PageInfo<RemoteApplicationVO>> getMyDownloadApp(@Query("remote_token") String token, @Query("params") String param, @Query("orderBy") String orderBy, @Query("order") String order, @Query("page") Integer page, @Query("size") Integer size, @Body List<String> appCodeList);

    @GET("/market/v1/applications/versions/{version_id}/download_info")
    Call<ResponseBody> queryDownloadInfo(@Path("version_id") Long versionId, @Query("service_versions") Set<Long> serviceVersions, @Query("remote_token") String remoteToken);

    @GET("/market/v1/applications/versions/{version_id}/app_info")
    Call<ResponseBody> queryDownloadAppInfo(@Path("version_id") Long versionId, @Query("remote_token") String remoteToken);
}
