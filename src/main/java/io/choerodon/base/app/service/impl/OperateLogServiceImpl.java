package io.choerodon.base.app.service.impl;

import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.dto.OperateLogDTO;
import io.choerodon.base.infra.mapper.OperateLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
@Service
public class OperateLogServiceImpl implements OperateLogService {
    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public List<OperateLogDTO> listNewOperateLod(Long organizationId) {
        List<OperateLogDTO> listNewOperateLod = operateLogMapper.listNewOperateLod(organizationId);
        return listNewOperateLod;
    }
}
