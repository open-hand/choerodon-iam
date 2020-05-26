package io.choerodon.iam.app.service.impl;

import io.choerodon.iam.app.service.OperateLogService;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import org.springframework.stereotype.Service;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    private AsgardFeignClient asgardFeignClient;

    public OperateLogServiceImpl(AsgardFeignClient asgardFeignClient) {
        this.asgardFeignClient = asgardFeignClient;
    }


    @Override
//    @OperateLog(type = "siteRetry", content = "%s重试了事务实例“%s”", level = {ResourceLevel.SITE})
    public void siteRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }

    @Override
//    @OperateLog(type = "orgRetry", content = "%s重试了事务实例“%s”", level = {ResourceLevel.ORGANIZATION})
    public void orgRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }
}
