package io.choerodon.iam.infra.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.POST;

import io.choerodon.iam.api.vo.EnterpriseInfoVO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:55
 */
public interface IamClient {

    @POST("/iam/choerodon/v1/enterprises")
    Call<ResponseBody> saveEnterpriseInfo(EnterpriseInfoVO enterpriseInfoVO);
}
