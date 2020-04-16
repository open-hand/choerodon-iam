package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * @author scp
 * @date 2020/3/30
 * @description
 */
public interface DashboardC7nService {
    DashboardDTO update(Long dashboardId, DashboardDTO dashboardDTO, Boolean updateRole);

    DashboardDTO query(Long dashboardId);

    Page<DashboardDTO> list(DashboardDTO dashboardDTO, PageRequest pageRequest, String param);

    void reset(Long dashboardId);
}
