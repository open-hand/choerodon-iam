package io.choerodon.base.infra.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/14
 */
public interface RemoteTokenRetrofitClient {
    @GET("/base/v1/public/remote_tokens/check")
    Call<ResponseBody> checkToken(@Query("remote_token") String token,@Query("operation") String operation);
}
