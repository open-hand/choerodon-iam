package io.choerodon.iam.infra.feign;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.choerodon.iam.infra.feign.fallback.DevopsFeignClientFallback;

@FeignClient(value = "devops-service", fallback = DevopsFeignClientFallback.class)
public interface DevopsFeignClient {

    /**
     * 校验email在gitlab中是否已经使用
     *
     * @param email 邮箱
     * @return 校验结果
     */
    @GetMapping(value = "/gitlab/email/check")
    ResponseEntity<String> checkGitlabEmail(@RequestParam(value = "email") String email);

    @PostMapping(value = "/v1/projects/{project_id}/app_service/list_by_project_ids")
    ResponseEntity<String> countAppServerByProjectId(@ApiParam(value = "项目ID", required = true)
                                                     @PathVariable(value = "project_id") Long projectId,
                                                     @RequestBody List<Long> longList);

    /**
     * 格式 yyyy-MM-dd HH:mm:ss
     *
     * @param projectId 项目id
     * @param startTime 开始时间
     * @param endTime   结束时间
     */
    @GetMapping("/v1/projects/{project_id}/deploy_record/count_by_date")
    ResponseEntity<String> countByDate(
            @PathVariable(value = "project_id") Long projectId,
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime);

    @PostMapping("/v1/users/list_by_ids")
    ResponseEntity<String> listByUserIds(
            @ApiParam(value = "用户id", required = true)
            @RequestBody Set<Long> iamUserIds);

    @GetMapping("/v1/projects/{project_id}/users/{user_id}")
    ResponseEntity<String> queryByUserId(@PathVariable(value = "project_id") Long projectId,
                                             @ApiParam(value = "用户id", required = true)
                                             @PathVariable(value = "user_id") Long userId);


}
