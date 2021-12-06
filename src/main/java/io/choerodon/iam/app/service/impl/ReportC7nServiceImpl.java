package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.helper.LanguageHelper;
import org.hzero.core.util.AssertUtils;
import org.springframework.stereotype.Service;

import io.choerodon.iam.app.service.ReportC7nService;
import io.choerodon.iam.infra.dto.ReportDTO;
import io.choerodon.iam.infra.mapper.ReportMapper;

/**
 * @author scp
 * @since 2020/4/1
 */
@Service
public class ReportC7nServiceImpl implements ReportC7nService {

    protected ReportMapper reportMapper;

    public ReportC7nServiceImpl(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    @Override
    public List<ReportDTO> queryReportList(Long projectId, String module) {
        AssertUtils.isTrue(StringUtils.isNotBlank(module),"error.module.is.null");
        return reportMapper.selectByProjectId(projectId, module, LanguageHelper.language());
    }
}
