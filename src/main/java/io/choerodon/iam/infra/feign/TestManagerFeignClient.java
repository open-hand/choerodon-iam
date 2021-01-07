package io.choerodon.iam.infra.feign;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.choerodon.iam.infra.feign.fallback.TestManagerFeignClientFallback;
import io.choerodon.iam.api.vo.AgileProjectInfoVO;

/**
 * @author zmf
 * @since 12/13/19
 */
@FeignClient(value = "test-manager-service", fallback = TestManagerFeignClientFallback.class)
public interface TestManagerFeignClient {
    @ApiOperation("更新projectInfo")
    @PutMapping("/v1/projects/{project_id}/project_info")
    ResponseEntity<String> updateProjectInfo(@ApiParam(value = "项目id", required = true)
                                                         @PathVariable(name = "project_id") Long projectId,
                                                         @ApiParam(value = "projectInfo对象", required = true)
                                                         @RequestBody AgileProjectInfoVO projectInfoVO);
}
