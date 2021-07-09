package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.DashboardVO;
import io.choerodon.iam.infra.dto.DashboardDTO;

import java.util.List;

/**
 * 应用服务
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardService {
    /**
     * 创建视图
     *
     * @param dashboard
     * @return
     */
    DashboardDTO createDashboard(DashboardDTO dashboard);

    /**
     * 更新视图
     *
     * @param dashboard
     * @return
     */
    DashboardDTO updateDashboard(DashboardVO dashboard);

    /**
     * 删除视图
     *
     * @param dashboardId
     * @return
     */
    DashboardDTO deleteDashboard(Long dashboardId);

    /**
     * 查询当前用户视图
     *
     * @return
     */
    List<DashboardDTO> queryDashboard();
}
