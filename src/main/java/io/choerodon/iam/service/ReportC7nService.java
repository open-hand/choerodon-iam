package io.choerodon.iam.service;

import java.util.List;

import io.choerodon.base.api.dto.*;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
public interface ReportC7nService {
    List<ReportDTO> queryReportList(Long projectId);
}
