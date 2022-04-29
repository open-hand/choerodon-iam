package io.choerodon.iam.infra.feign.fallback;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.AdminFeignClient;

/**
 * @author scp
 * @since 2022/4/29
 */
@Component
public class AdminFeignClientFallback implements AdminFeignClient {
    @Override
    public ResponseEntity<List<String>> listServiceCodes() {
        throw new CommonException("error.list.service.codes");
    }
}
