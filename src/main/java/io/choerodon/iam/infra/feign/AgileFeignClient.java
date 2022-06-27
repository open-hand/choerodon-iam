package io.choerodon.iam.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.choerodon.iam.infra.feign.fallback.AgileFeignClientFallback;

/**
 * @author jiameng.cao
 * @since 2019/7/30
 */
@FeignClient(value = "agile-service", fallback = AgileFeignClientFallback.class)
public interface AgileFeignClient {

    @GetMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<String> queryProjectInfoByProjectId(@PathVariable(name = "project_id") Long projectId);
}
