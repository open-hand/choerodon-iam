package io.choerodon.iam.app.service;

import io.choerodon.iam.infra.dto.DashboardUserDTO;

import java.util.List;

/**
 * 应用服务
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardUserService {
    /**
     * 面板分配用户
     *
     * @param dashboardUser
     * @return
     */
    DashboardUserDTO createDashboardUser(DashboardUserDTO dashboardUser);

    /**
     * 删除当前用户关联视图
     *
     * @param dashboardId
     * @return
     */
    DashboardUserDTO deleteDashboardUser(Long dashboardId);

    /**
     * 更新用户视图顺序
     *
     * @param dashboardUserS
     * @return
     */
    List<DashboardUserDTO> batchUpdateDashboardUserRank(List<DashboardUserDTO> dashboardUserS);

    /**
     * 自动同步官网视图给用户
     * @param dashboardCodes 官方视图code
     */
    void syncOfficialDashboard(List<String> dashboardCodes);
}
