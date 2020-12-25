package io.choerodon.iam.infra.retrofit;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(RetrofitHandler.class);

    private RetrofitHandler() {
    }

    /**
     * Retrofit 设置
     *
     * @return retrofit
     */
    public static Retrofit initRetrofit(String baseUrl) {

        // 跳过tls校验
        OkHttpClient okHttpClient = buildOkHttpClient();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    /**
     * basic的token来创建client
     *
     * @param token basic认证的token
     * @return client
     */
    public static OkHttpClient buildOkHttpClient() {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.followRedirects(true);
        return okHttpClientBuilder.build();
    }

}
