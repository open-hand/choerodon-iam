package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.DashboardLayoutVO;
import io.choerodon.iam.infra.dto.DashboardLayoutDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardLayoutMapper extends BaseMapper<DashboardLayoutDTO> {
    /**
     * 查询用户视图布局
     *
     * @param dashboardId
     * @param userId
     * @return
     */
    List<DashboardLayoutVO> queryLayoutByDashboard(@Param("dashboardId") Long dashboardId,
                                                   @Param("userId") Long userId);
}
