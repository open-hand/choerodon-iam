package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.DashboardLayoutVO;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.iam.infra.dto.DashboardLayoutDTO;
import io.choerodon.iam.infra.mapper.DashboardLayoutMapper;
import io.choerodon.iam.infra.mapper.DashboardMapper;
import io.choerodon.iam.infra.mapper.DashboardUserMapper;
import io.choerodon.mybatis.domain.AuditDomain;
import org.springframework.stereotype.Service;
import io.choerodon.iam.app.service.DashboardLayoutService;

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

    private DashboardUserMapper dashboardUserMapper;

    private DashboardLayoutMapper dashboardLayoutMapper;

    private DashboardMapper dashboardMapper;

    public DashboardLayoutServiceImpl(DashboardUserMapper dashboardUserMapper,
                                      DashboardLayoutMapper dashboardLayoutMapper,
                                      DashboardMapper dashboardMapper) {
        this.dashboardUserMapper = dashboardUserMapper;
        this.dashboardLayoutMapper = dashboardLayoutMapper;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public List<DashboardLayoutDTO> batchCreateOrUpdateLayout(Long dashboardId, List<DashboardLayoutDTO> dashboardLayoutS) {
        List<DashboardLayoutDTO> updateDashboardLayoutList = new ArrayList<>();
        List<DashboardLayoutDTO> createDashboardLayoutList = new ArrayList<>();
        List<DashboardLayoutDTO> deleteDashboardLayoutList = new ArrayList<>();
        Long userId = DetailsHelper.getUserDetails().getUserId();
        dashboardLayoutS.forEach(dashboardLayout -> {
            dashboardLayout.setUserId(userId);
            dashboardLayout.setDashboardId(dashboardId);
            if (Objects.isNull(dashboardLayout.getLayoutId())) {
                createDashboardLayoutList.add(dashboardLayout);
                return;
            }
            if (Objects.equals(dashboardLayout.get_status(), AuditDomain.RecordStatus.update)) {
                updateDashboardLayoutList.add(dashboardLayout);
                return;
            }
            if (Objects.equals(dashboardLayout.get_status(), AuditDomain.RecordStatus.delete)) {
                deleteDashboardLayoutList.add(dashboardLayout);
                return;
            }
        });
        createDashboardLayoutList.forEach(createDashboardLayout -> {
            dashboardLayoutMapper.insertSelective(createDashboardLayout);
        });
        updateDashboardLayoutList.forEach(updateDashboardLayout -> {
            dashboardLayoutMapper.updateByPrimaryKeySelective(updateDashboardLayout);
        });
        deleteDashboardLayoutList.forEach(deleteDashboardLayout -> {
            dashboardLayoutMapper.deleteByPrimaryKey(deleteDashboardLayout);
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
