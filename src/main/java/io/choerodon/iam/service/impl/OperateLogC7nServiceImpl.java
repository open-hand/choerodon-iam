package io.choerodon.iam.service.impl;

import org.springframework.stereotype.Service;

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogC7nService;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.base.infra.mapper.OperateLogMapper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Service
public class OperateLogC7nServiceImpl implements OperateLogC7nService {
    private OperateLogMapper operateLogMapper;
    private AsgardFeignClient asgardFeignClient;

    public OperateLogC7nServiceImpl(OperateLogMapper operateLogMapper, AsgardFeignClient asgardFeignClient) {
        this.operateLogMapper = operateLogMapper;
        this.asgardFeignClient = asgardFeignClient;
    }


    @Override
    public Page<OperateLogVO> listOperateLog(PageRequest pageRequest, Long sourceId) {
        if (sourceId == 0L) {
            return PageHelper.doPageAndSort(pageRequest, () -> operateLogMapper.listOperateLogSite());

        }
        return PageHelper.doPageAndSort(pageRequest, () ->
                operateLogMapper.listOperateLogOrg(sourceId));
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
