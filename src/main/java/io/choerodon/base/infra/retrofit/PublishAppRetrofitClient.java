package io.choerodon.base.infra.retrofit;

import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * @author wuguokai
 * @date 2019/8/13
 */
public interface PublishAppRetrofitClient {
    // 申请发布应用 / 已撤销重新申请
    @POST("/market/v1/market_applications/create")
    Call<ResponseBody> apply(@Query("remote_token") String token,
                             @Body MarketApplicationVO body);

    // 被驳回重新申请
    @PUT("/market/v1/market_applications/updateApprove")
    Call<ResponseBody> reapply(@Query("remote_token") String token,
                               @Body MarketApplicationVO body);

    // 已发布应用保存应用及版本更新信息
    @PUT("/market/v1/market_applications/published")
    Call<ResponseBody> updateMktPublishVersionInfo(@Query("remote_token") String token,
                                                   @Query("app_code") String code,
                                                   @Query("version") String version,
                                                   @Body MarketApplicationVersionVO body);

    // 撤销
    @DELETE("/market/v1/market_applications/revertApp")
    Call<ResponseBody> revocation(@Query("remote_token") String token,
                                  @Query("version") String version,
                                  @Query("code") String code);

    // 提交确认信息并获得发布参数
    @POST("/market/v1/market_applications/harbor_url")
    Call<ResponseBody> confirm(@Query("remote_token") String token,
                               @Query("app_code") String code,
                               @Query("version") String version,
                               @Body MarketApplicationVO body);

    // 修复版本时请求发布参数
    @GET("/market/v1/market_applications/harbor_url")
    Call<ResponseBody> fixConfirm(@Query("remote_token") String token,
                                  @Query("app_code") String code);


    // 请求更新状态
    @POST("/market/v1/market_applications/approveStatus")
    Call<ResponseBody> getStatus(@Query("remote_token") String token,
                                 @Body List<ApproveStatusVO> approveStatusVOS);

    // 校验发布权限
    @GET("/market/v1/customers/check/publish")
    Call<ResponseBody> checkPublishPermissions(@Query("remote_token") String token);

    // 校验客户可用情况
    @GET("/market/v1/customers/check/available")
    Call<ResponseBody> checkCustomerAvailable(@Query("remote_token") String token);

    @GET("/market/v1/market_applications/check_name")
    Call<ResponseBody> checkName(@Query("remote_token") String token,
                                 @Query("name") String name,
                                 @Query("code") String code);

    @GET("/market/v1/market_app_categories/list/enable")
    Call<PageInfo<AppCategoryDTO>> getEnableCategoryList(@Query("page") Integer page,
                                                         @Query("size") Integer size,
                                                         @Query("remote_token") String token);
}
