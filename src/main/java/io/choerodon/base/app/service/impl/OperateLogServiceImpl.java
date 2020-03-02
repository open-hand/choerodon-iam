package io.choerodon.base.app.service.impl;

import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import io.choerodon.base.api.vo.OperateLogVO;
import io.choerodon.base.app.service.OperateLogService;
import io.choerodon.base.infra.mapper.OperateLogMapper;
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
    @Autowired
    private OperateLogMapper operateLogMapper;
    private ModelMapper modelMapper = new ModelMapper();


    @Override
    public PageInfo<OperateLogVO> listOperateLog(Pageable pageable, Long sourceId) {
        if (sourceId == 0L) {
            return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize())
                    .doSelectPageInfo(() -> operateLogMapper.listOperateLogSite());

        }
        return PageMethod.startPage(pageable.getPageNumber(), pageable.getPageSize()).doSelectPageInfo(() ->
                operateLogMapper.listOperateLogOrg(sourceId));
    }
}
