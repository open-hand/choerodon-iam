package io.choerodon.base.infra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "devops-service")
public interface DevopsFeignClient {
    /**
     * 校验email在gitlab中是否已经使用
     *
     * @param email 邮箱
     * @return 校验结果
     */
    @GetMapping(value = "/gitlab/email/check")
    ResponseEntity<Boolean> checkGitlabEmail(@RequestParam(value = "email") String email);

}
