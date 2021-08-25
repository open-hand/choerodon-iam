package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.api.vo.DashboardVO;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

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

    /**
     * 查询所有官方视图
     *
     * @return
     * @param filterFlag
     * @param pageRequest
     */
    Page<DashboardDTO> queryInternalDashboard(Integer filterFlag, PageRequest pageRequest);

    /**
     * 批量删除官方视图
     * @param dashboardIds
     * @return
     */
    List<DashboardDTO> batchDeleteDashboard(List<Long> dashboardIds);
}
