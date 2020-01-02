package io.choerodon.base.infra.feign.fallback;

import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.core.exception.CommonException;
import org.springframework.http.ResponseEntity;

/**
 * @author Eugen
 */
public class DevopsFeignClientFallback implements DevopsFeignClient {
    @Override
    public ResponseEntity<Boolean> checkGitlabEmail(String email) {
        throw new CommonException("error.feign.devops.check.gitlab.email");
    }
}
