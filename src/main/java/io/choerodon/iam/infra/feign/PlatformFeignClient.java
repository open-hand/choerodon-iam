package io.choerodon.iam.infra.feign;

import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.choerodon.iam.infra.feign.fallback.AdminFeignClientFallback;
import io.choerodon.iam.infra.feign.fallback.PlatformFeignClientFallback;

/**
 * <p>
 * 用户缓存信息修改
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:09
 */
@FeignClient(value = HZeroService.Platform.NAME, fallback = PlatformFeignClientFallback.class)
public interface PlatformFeignClient {

    /**
     * 查询服务code列表
     */
    @PutMapping("choerodon/v1/config")
    ResponseEntity<Void> updateConfig(@RequestParam String code, @RequestParam String value);
}
