package io.choerodon.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.iam.api.vo.OperateLogVO;
import io.choerodon.iam.app.service.OperateLogService;
import io.choerodon.iam.infra.annotation.OperateLog;
import io.choerodon.iam.infra.feign.AsgardFeignClient;
import io.choerodon.iam.infra.mapper.OperateLogMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    private OperateLogMapper operateLogMapper;
    private AsgardFeignClient asgardFeignClient;

    public OperateLogServiceImpl(OperateLogMapper operateLogMapper, AsgardFeignClient asgardFeignClient) {
        this.operateLogMapper = operateLogMapper;
        this.asgardFeignClient = asgardFeignClient;
    }


    @Override
    public Page<OperateLogVO> listOperateLog(PageRequest pageRequest, Long sourceId) {
        if (sourceId == 0L) {
            return PageHelper.doPageAndSort(pageRequest, () -> operateLogMapper.listOperateLogSite());

        }
        return PageHelper.doPageAndSort(pageRequest, () -> operateLogMapper.listOperateLogOrg(sourceId));
    }

    @Override
    @OperateLog(type = "siteRetry", content = "%s重试了事务实例“%s”", level = {ResourceLevel.SITE})
    public void siteRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }

    @Override
    @OperateLog(type = "orgRetry", content = "%s重试了事务实例“%s”", level = {ResourceLevel.ORGANIZATION})
    public void orgRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }
}
