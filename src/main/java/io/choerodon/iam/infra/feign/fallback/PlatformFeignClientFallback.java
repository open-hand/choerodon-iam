package io.choerodon.iam.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.PlatformFeignClient;

/**
 * @author scp
 * @since 2022/4/29
 */
@Component
public class PlatformFeignClientFallback implements PlatformFeignClient {

    @Override
    public ResponseEntity<Void> updateConfig(@RequestParam String code, @RequestParam String value) {
        throw new CommonException("error.update.config");
    }
}
