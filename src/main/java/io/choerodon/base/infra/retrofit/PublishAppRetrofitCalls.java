package io.choerodon.base.infra.retrofit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.api.dto.AppCategoryDTO;
import io.choerodon.base.api.vo.MarketApplicationVO;
import io.choerodon.base.api.vo.MarketApplicationVersionVO;
import io.choerodon.base.api.vo.RemoteTokenAuthorizationVO;
import io.choerodon.base.infra.dto.mkt.ApproveStatusVO;
import io.choerodon.base.infra.dto.mkt.HarborUserAndUrlVO;
import io.choerodon.base.infra.enums.RemoteTokenStatus;
import io.choerodon.base.infra.factory.RetrofitClientFactory;
import io.choerodon.base.infra.mapper.RemoteTokenAuthorizationMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ExceptionResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

/**
 * @author Eugen
 * @date 2019/8/22
 */
@Component
public class PublishAppRetrofitCalls {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublishAppRetrofitCalls.class);

    private static final String RETROFIT_RESPONSE_PARSE_EXCEPTION = "error.retrofit.response.parse";
    private RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper;
    private RetrofitClientFactory retrofitClientFactory;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PublishAppRetrofitCalls(RemoteTokenAuthorizationMapper remoteTokenAuthorizationMapper, RetrofitClientFactory retrofitClientFactory) {
        this.remoteTokenAuthorizationMapper = remoteTokenAuthorizationMapper;
        this.retrofitClientFactory = retrofitClientFactory;
    }

    /*
        发布/重新发布
         */
    public Boolean apply(Boolean isReapply, MarketApplicationVO sendVO) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call;

        if (isReapply) {
            //重新申请
            call = client.reapply(latestEnabledToken.getRemoteToken(), sendVO);
        } else {
            //申请
            call = client.apply(latestEnabledToken.getRemoteToken(), sendVO);
        }
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }


    /*
    撤销
     */
    public Boolean revocation(String version, String code) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.revocation(latestEnabledToken.getRemoteToken(), version, code);// 执行request
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }

    /*
    已发布应用保存应用及版本更新信息
     */
    public Boolean updateMktPublishVersionInfo(String code, String version, MarketApplicationVersionVO sendVO) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.updateMktPublishVersionInfo(latestEnabledToken.getRemoteToken(), code, version, sendVO);// 执行request
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }


    /*
    提交确认信息并获得发布参数
     */
    public HarborUserAndUrlVO confirm(String code, String version, MarketApplicationVO sendVO) {

        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.confirm(latestEnabledToken.getRemoteToken(), code, version, sendVO);// 执行request
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<HarborUserAndUrlVO>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }

    /*
    修复版本时请求发布参数
     */
    public HarborUserAndUrlVO fixConfirm(String code) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.fixConfirm(latestEnabledToken.getRemoteToken(), code);// 执行request

        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<HarborUserAndUrlVO>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }


    /*
    请求更新状态
     */
    public List<ApproveStatusVO> getStatus(List<ApproveStatusVO> sendVOs) {

        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.getStatus(latestEnabledToken.getRemoteToken(), sendVOs);// 执行request
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<List<ApproveStatusVO>>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }

    /*
    校验发布权限
     */
    public Boolean checkPublishPermissions(String remoteToken) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.checkPublishPermissions(remoteToken);// 执行request

        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }

    }


    /*
    校验客户可用情况
     */
    public Boolean checkCustomerAvailable(String remoteToken) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.checkCustomerAvailable(remoteToken);// 执行request

        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }

    }


    /*
      校验市场应用名称是否可用，可用返回true
     */
    public Boolean checkName(String name, String code) {
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        Call<ResponseBody> call = client.checkName(latestEnabledToken.getRemoteToken(), name, code);// 执行request

        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }

    /*
       可用应用类型获取
     */
    public PageInfo<AppCategoryDTO> getEnableCategoryList(int page, int size) {
        Response<PageInfo<AppCategoryDTO>> repos = null;
        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        PublishAppRetrofitClient client = (PublishAppRetrofitClient) retrofitClientFactory
                .getRetrofitBean(latestEnabledToken.getAuthorizationUrl(), PublishAppRetrofitClient.class);// 获取Client

        try {
            repos = client.getEnableCategoryList(page, size, latestEnabledToken.getRemoteToken()).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (repos != null) {
            return repos.body();
        } else {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION);
        }
    }

    /**
     * 通知market已发布应用已经修改
     */
    public Boolean updateMarketPublishAppInfo(String appCode, MarketApplicationVO sendVO) {

        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                latestEnabledToken.getAuthorizationUrl(), AppMarketRetrofitClient.class);
        Call<ResponseBody> call = client.updateAppPublishInfoDetails(latestEnabledToken.getRemoteToken(), appCode, sendVO);
        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        try {
            return objectMapper.readValue(responseStr, new TypeReference<Boolean>() {
            });//返回正确结果
        } catch (IOException e) {
            throw new CommonException(RETROFIT_RESPONSE_PARSE_EXCEPTION, e);
        }
    }


    /**
     * 通知market已发布应用已经修改
     */
    public String cutImage(MultipartFile file, Double rotate, Integer axisX, Integer axisY, Integer width, Integer height) {

        RemoteTokenAuthorizationVO latestEnabledToken = getLatestEnabledToken();// 获取token

        AppMarketRetrofitClient client = (AppMarketRetrofitClient) retrofitClientFactory.getRetrofitBean(
                latestEnabledToken.getAuthorizationUrl(), AppMarketRetrofitClient.class);

        MultipartBody.Part multipartBodyPart = null;
        try {
            multipartBodyPart = fileToMultipartBodyPart(file);
        } catch (IOException e) {
            throw new CommonException("error.retrofit.parse.multipart.body.part", e);
        }
        Call<ResponseBody> call = client.cutImage(latestEnabledToken.getRemoteToken(), multipartBodyPart, rotate, axisX, axisY, width, height);

        String responseStr = executeCall(call);

        parseCommonException(responseStr);//分析是否是自定义异常

        return responseStr;
    }

    /*————————————————————————————————————————————————————————————————————————————————*/

    /**
     * {@link MultipartFile} 转化为 {@link MultipartBody.Part}
     *
     * @param file {@link MultipartFile}
     * @return {@link MultipartBody.Part}
     * @throws IOException
     */
    public static MultipartBody.Part fileToMultipartBodyPart(MultipartFile file) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse(file.getContentType()), file.getBytes());
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getOriginalFilename(), requestBody);
        return part;
    }


    /**
     * 执行请求并返回Response
     */
    private String executeCall(Call<ResponseBody> call) {
        Response<ResponseBody> execute = null;
        try {
            execute = call.execute();
            if (execute == null) {
                LOGGER.info("::Retrofit::response is null");
                throw new CommonException("error.retrofit.execute.response.is.empty");
            }
            if (!execute.isSuccessful()) {
                LOGGER.info("::Retrofit::unsuccessful:{}", execute.errorBody().string());
                throw new CommonException("error.retrofit.execute.is.unsuccessful");
            }
            return execute.body().string();// response关闭
        } catch (IOException e) {
            LOGGER.info("::Retrofit::An exception occurred during execution:{}", e);
            throw new CommonException("error.retrofit.execute", e);
        }
    }

    /**
     * 获取最新的有效的远程连接令牌
     */
    private RemoteTokenAuthorizationVO getLatestEnabledToken() {
        RemoteTokenAuthorizationVO remoteTokenAuthorizationVO = remoteTokenAuthorizationMapper.selectLatestToken();
        if (remoteTokenAuthorizationVO == null || !RemoteTokenStatus.SUCCESS.value().equals(remoteTokenAuthorizationVO.getStatus())) {
            throw new CommonException("error.remote.token.authorization.not.exist");
        }
        return remoteTokenAuthorizationVO;
    }

    /**
     * 解析响应是否是CommonException
     */
    private void parseCommonException(String responseStr) {
        try {
            ExceptionResponse e = objectMapper.readValue(responseStr, new TypeReference<ExceptionResponse>() {
            });
            LOGGER.info("::Retrofit::The response is CommonException，code:{},message:{}", e.getCode(), e.getMessage());
            throw new CommonException("error.retrofit.response.is.common.exception");
        } catch (IOException e) {
            LOGGER.info("Retrofit：response is normal，body : {}", responseStr);
        }
    }

}
