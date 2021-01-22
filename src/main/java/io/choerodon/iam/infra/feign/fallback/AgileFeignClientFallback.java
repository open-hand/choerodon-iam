package io.choerodon.iam.infra.feign.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;
import io.choerodon.iam.infra.feign.AgileFeignClient;

/**
 * @author jiameng.cao
 * @since 2019/7/30
 */
@Component
public class AgileFeignClientFallback implements AgileFeignClient {

    @Override
    public ResponseEntity<String> queryProjectInfoByProjectId(Long projectId) {
        throw new CommonException("error.agile.queryProjectInfoByProjectId");
    }
}
