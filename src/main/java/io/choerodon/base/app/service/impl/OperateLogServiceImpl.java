package io.choerodon.base.app.service.impl;

import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.mapper.OperateLogMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
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
    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<OperateLogVO> listNewOperateLog(Long sourceId) {
        if (sourceId == 0L) {
            return modelMapper.map(operateLogMapper.listNewOperateLogSite(),
                    new TypeToken<List<OperateLogVO>>() {
                    }.getType());
        }
        return modelMapper.map(operateLogMapper.listNewOperateLogOrg(sourceId),
                new TypeToken<List<OperateLogVO>>() {
                }.getType());
    }

    @Override
    public List<OperateLogVO> listMoreOperateLog(Long sourceId) {
        if (sourceId == 0L) {
            return modelMapper.map(operateLogMapper.listMoreOperateLogSite(),
                    new TypeToken<List<OperateLogVO>>() {
                    }.getType());
        }
        return modelMapper.map(operateLogMapper.listMoreOperateLogOrg(sourceId),
                new TypeToken<List<OperateLogVO>>() {
                }.getType());
    }
}
