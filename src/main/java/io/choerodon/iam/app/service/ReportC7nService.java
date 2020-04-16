package io.choerodon.iam.app.service;

import java.util.List;

import io.choerodon.iam.infra.dto.ReportDTO;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
public interface ReportC7nService {
    List<ReportDTO> queryReportList(Long projectId);
}
