package io.choerodon.iam.infra.feign;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.iam.infra.feign.fallback.AgileFeignClientFallback;

/**
 * @author jiameng.cao
 * @since 2019/7/30
 */
@FeignClient(value = "choerodon-admin", fallback = AgileFeignClientFallback.class)
public interface AdminFeignClient {

    @GetMapping("/choerodon/v1/services/model")
    ResponseEntity<String> listModels();
}
