package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.DashboardLayoutVO;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardLayoutDTO;
import io.choerodon.iam.infra.mapper.DashboardLayoutMapper;
import org.springframework.stereotype.Service;
import io.choerodon.iam.app.service.DashboardLayoutService;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 应用服务默认实现
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@Service
public class DashboardLayoutServiceImpl implements DashboardLayoutService {

    private DashboardLayoutMapper dashboardLayoutMapper;

    public DashboardLayoutServiceImpl(DashboardLayoutMapper dashboardLayoutMapper) {
        this.dashboardLayoutMapper = dashboardLayoutMapper;
    }

    @Override
    public List<DashboardLayoutDTO> batchCreateOrUpdateLayout(Long dashboardId, List<DashboardLayoutDTO> dashboardLayoutS) {
        Long userId = DetailsHelper.getUserDetails().getUserId();
        dashboardLayoutMapper.delete(new DashboardLayoutDTO().setUserId(userId).setDashboardId(dashboardId));
        if (CollectionUtils.isEmpty(dashboardLayoutS)) {
            return new ArrayList<>();
        }
        dashboardLayoutS.forEach(dashboardLayout -> {
            dashboardLayout.setUserId(userId);
            dashboardLayout.setDashboardId(dashboardId);
            dashboardLayoutMapper.insertSelective(dashboardLayout);
        });
        return dashboardLayoutS;
    }

    @Override
    public int deleteDashboardLayout(Long dashboardId) {
        return dashboardLayoutMapper.delete(new DashboardLayoutDTO().setUserId(obtainUserId())
                .setDashboardId(dashboardId));
    }

    @Override
    public List<DashboardLayoutVO> queryLayoutByDashboard(Long dashboardId) {
        return dashboardLayoutMapper.queryLayoutByDashboard(dashboardId, obtainUserId());
    }

    private Long obtainUserId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_USER_GET);
        }
        return customUserDetails.getUserId();
    }
}
