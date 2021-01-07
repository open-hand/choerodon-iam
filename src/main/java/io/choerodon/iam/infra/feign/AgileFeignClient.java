package io.choerodon.iam.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.choerodon.iam.infra.feign.fallback.AgileFeignClientFallback;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;

/**
 * @author jiameng.cao
 * @since 2019/7/30
 */
@FeignClient(value = "agile-service", fallback = AgileFeignClientFallback.class)
public interface AgileFeignClient {
    @PutMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<String> updateProjectInfo(@PathVariable(name = "project_id") Long projectId, @RequestBody AgileProjectInfoVO agileProjectInfoVO);

    @GetMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<String> queryProjectInfoByProjectId(@PathVariable(name = "project_id") Long projectId);
}
