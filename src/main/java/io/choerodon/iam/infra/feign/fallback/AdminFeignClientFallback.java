package io.choerodon.iam.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.AdminFeignClient;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
@Component
public class AdminFeignClientFallback implements AdminFeignClient {
    @Override
    public ResponseEntity<String> listModels() {
        throw new CommonException("error.admin.list.models");
    }
}
