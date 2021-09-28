package io.choerodon.iam.infra.feign.operator;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignClientUtils;
import io.choerodon.iam.infra.feign.AdminFeignClient;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
@Component
public class AdminFeignClientOperator {
    private static Boolean haveAgileModel = null;
    @Autowired
    private AdminFeignClient adminFeignClient;

    public List<String> listModels() {
        return FeignClientUtils.doRequest(() -> adminFeignClient.listModels(), new TypeReference<List<String>>() {
        });
    }

    public Boolean haveAgileModel() {
        // todo 删除 scp
        haveAgileModel = true;
        if (haveAgileModel == null) {
            haveAgileModel = listModels().contains("agile");
        }
        return haveAgileModel;
    }
}
