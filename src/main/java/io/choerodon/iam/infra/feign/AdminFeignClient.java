package io.choerodon.iam.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.choerodon.iam.infra.feign.fallback.AdminFeignClientFallback;

/**
 * @author jiameng.cao
 * @since 2019/7/30
 */
@FeignClient(value = "choerodon-admin", fallback = AdminFeignClientFallback.class)
public interface AdminFeignClient {

    @GetMapping("/choerodon/v1/services/model")
    ResponseEntity<String> listModels();
}
