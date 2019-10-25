package io.choerodon.base.app.service.impl;

import org.springframework.stereotype.Service;

import java.util.*;

import io.choerodon.base.api.dto.*;
import io.choerodon.base.app.service.*;
import io.choerodon.base.infra.mapper.*;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@Service
public class ReportServiceImpl implements ReportService {

    private ReportMapper reportMapper;

    public ReportServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<ReportDTO> queryReportList() {
        return reportMapper.selectAll();
    }
}
