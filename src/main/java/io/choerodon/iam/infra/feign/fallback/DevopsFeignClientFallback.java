package io.choerodon.iam.infra.feign.fallback;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.infra.feign.DevopsFeignClient;

/**
 * @author Eugen
 */
@Component
public class DevopsFeignClientFallback implements DevopsFeignClient {
    @Override
    public ResponseEntity<String> checkGitlabEmail(String email) {
        throw new CommonException("error.feign.devops.check.gitlab.email");
    }
    @Override
    public ResponseEntity<String> countAppServerByProjectId(Long aLong, List<Long> longs) {
        throw new CommonException("error.feign.devops.query.app.server");
    }

    @Override
    public ResponseEntity<String> countByDate(Long projectId, String startTime, String endTime) {
        throw new CommonException("error.feign.devops.query.deploy.records");
    }

    @Override
    public ResponseEntity<String> listByUserIds(Set<Long> iamUserIds) {
        throw new CommonException("error.feign.devops.query.gitlab.user.id");
    }

    @Override
    public ResponseEntity<String> queryByUserId(Long projectId, Long userId) {
        throw new CommonException("error.feign.devops.query.gitlab.user.id");
    }
}
