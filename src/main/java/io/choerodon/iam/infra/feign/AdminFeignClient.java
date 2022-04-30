package io.choerodon.iam.infra.feign;

import java.util.List;

import org.hzero.common.HZeroService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import io.choerodon.iam.infra.feign.fallback.AdminFeignClientFallback;

/**
 * <p>
 * 用户缓存信息修改
 * </p>
 *
 * @author qingsheng.chen 2018/8/31 星期五 17:09
 */
@FeignClient(value = HZeroService.Admin.NAME, fallback = AdminFeignClientFallback.class, path = "/hadm")
public interface AdminFeignClient {


    /**
     * 查询服务code列表
     */
    @GetMapping("/choerodon/v1/services/service_codes")
    ResponseEntity<List<String>> listServiceCodes();
}
