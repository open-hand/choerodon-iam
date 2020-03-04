package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.annotation.OperateLog;
import io.choerodon.base.infra.feign.AsgardFeignClient;
import io.choerodon.base.infra.mapper.OperateLogMapper;
import io.choerodon.core.enums.ResourceType;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    private OperateLogMapper operateLogMapper;
    private AsgardFeignClient asgardFeignClient;

    public OperateLogServiceImpl(OperateLogMapper operateLogMapper) {
        this.operateLogMapper = operateLogMapper;
    }


    @Override
    public PageInfo<OperateLogVO> listOperateLog(Pageable pageable, Long sourceId) {
        if (sourceId == 0L) {
            return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                    .doSelectPageInfo(() -> operateLogMapper.listOperateLogSite());

        }
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() ->
                operateLogMapper.listOperateLogOrg(sourceId));
    }

    @Override
    @OperateLog(type = "siteRetry", content = "%s重试了事务实例“%s”", level = {ResourceType.SITE})
    public void siteRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }

    @Override
    @OperateLog(type = "orgRetry", content = "%s重试了事务实例“%s”", level = {ResourceType.ORGANIZATION})
    public void orgRetry(Long sourceId, long id) {
        asgardFeignClient.retry(id);
    }
}
