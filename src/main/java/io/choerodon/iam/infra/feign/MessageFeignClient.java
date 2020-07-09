package io.choerodon.iam.infra.feign;

import io.choerodon.iam.infra.feign.fallback.MessageFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value = "hzero-message", fallback = MessageFeignClientFallback.class)
public interface MessageFeignClient {

    @GetMapping("/choerodon/v1/online/current/ids")
    ResponseEntity<List<Long>> getOnlineUserIds();
}
