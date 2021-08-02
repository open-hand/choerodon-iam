package io.choerodon.iam.app.service;

import io.choerodon.iam.api.vo.DashboardLayoutVO;
import io.choerodon.iam.infra.dto.DashboardLayoutDTO;
import java.util.List;

/**
 * 应用服务
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardLayoutService {

    /**
     * 批量创建、更新视图布局
     *
     * @param dashboardId
     * @param dashboardLayoutS
     * @return
     */
    List<DashboardLayoutDTO> batchCreateOrUpdateLayout(Long dashboardId, List<DashboardLayoutDTO> dashboardLayoutS);

    /**
     * 清除视图下的所有卡片布局
     *
     * @param dashboardId
     * @return
     */
    int deleteDashboardLayout(Long dashboardId);

    /**
     * 查询视图下布局信息
     *
     * @param dashboardId
     * @return
     */
    List<DashboardLayoutVO> queryLayoutByDashboard(Long dashboardId);
}
