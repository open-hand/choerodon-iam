package io.choerodon.iam.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.PlatformFeignClient;

/**
 * @author scp
 * @since 2022/4/29
 */
@Component
public class PlatformFeignClientFallback implements PlatformFeignClient {

    @Override
    public ResponseEntity<Void> updateDefaultLanguage(String language) {
        throw new CommonException("error.update.default.language");
    }
}
