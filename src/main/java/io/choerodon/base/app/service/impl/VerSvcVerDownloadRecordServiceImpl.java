package io.choerodon.base.app.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import io.choerodon.base.app.service.VerSvcVerDownloadRecordService;
import io.choerodon.base.infra.dto.SvcVerDownloadRecordDTO;
import io.choerodon.base.infra.feign.MarketFeignClient;
import io.choerodon.base.infra.mapper.SvcVerDownloadRecordMapper;

/**
 * @author wanghao
 * @since 2019/9/9
 */
@Service
public class VerSvcVerDownloadRecordServiceImpl implements VerSvcVerDownloadRecordService {

    private SvcVerDownloadRecordMapper svcVerDownloadRecordMapper;
    private MarketFeignClient marketFeignClient;

    public VerSvcVerDownloadRecordServiceImpl(SvcVerDownloadRecordMapper svcVerDownloadRecordMapper) {
        this.svcVerDownloadRecordMapper = svcVerDownloadRecordMapper;
    }


    @Override
    public List<SvcVerDownloadRecordDTO> listSvcVerDownloadRecordByVersionIds(Set<Long> versionIds, Long organzationId) {
        return svcVerDownloadRecordMapper.listSvcVerDownloadRecordByVersionIds(versionIds, organzationId);
    }
}
