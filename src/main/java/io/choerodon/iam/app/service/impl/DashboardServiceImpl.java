package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.api.vo.DashboardVO;
import io.choerodon.iam.app.service.DashboardLayoutService;
import io.choerodon.iam.app.service.DashboardUserService;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.iam.infra.dto.DashboardUserDTO;
import io.choerodon.iam.infra.enums.DashboardType;
import io.choerodon.iam.infra.mapper.DashboardMapper;
import io.choerodon.iam.infra.mapper.DashboardUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.springframework.stereotype.Service;
import io.choerodon.iam.app.service.DashboardService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 应用服务默认实现
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    private DashboardMapper dashboardMapper;

    private DashboardUserService dashboardUserService;

    private DashboardUserMapper dashboardUserMapper;

    private DashboardLayoutService dashboardLayoutService;

    public DashboardServiceImpl(DashboardMapper dashboardMapper,
                                DashboardUserService dashboardUserService,
                                DashboardUserMapper dashboardUserMapper,
                                DashboardLayoutService dashboardLayoutService) {
        this.dashboardMapper = dashboardMapper;
        this.dashboardUserService = dashboardUserService;
        this.dashboardUserMapper = dashboardUserMapper;
        this.dashboardLayoutService = dashboardLayoutService;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DashboardDTO createDashboard(DashboardDTO dashboard) {
        dashboard.setDashboardType(DashboardType.CUSTOMIZE.getValue())
                .setDefaultFlag(BaseConstants.Flag.NO);
        dashboardMapper.insert(dashboard);
        dashboardUserService.createDashboardUser(new DashboardUserDTO().setDashboardId(dashboard.getDashboardId()));
        return dashboard;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DashboardDTO updateDashboard(DashboardVO dashboard) {
        DashboardDTO dashboardDTO = dashboardMapper.selectByPrimaryKey(dashboard.getDashboardId());
        check(dashboardDTO);
        if (StringUtils.isNotBlank(dashboard.getDashboardName()) &&
                !StringUtils.equals(dashboard.getDashboardName(), dashboardDTO.getDashboardName())) {
            dashboardDTO.setDashboardName(dashboard.getDashboardName());
            dashboardMapper.updateOptional(dashboardDTO, DashboardDTO.FIELD_DASHBOARD_NAME);
        }
        dashboardLayoutService.batchCreateOrUpdateLayout(dashboard.getDashboardId(), dashboard.getDashboardLayoutS());
        return dashboardDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DashboardDTO deleteDashboard(Long dashboardId) {
        DashboardDTO dashboard = dashboardMapper.selectByPrimaryKey(dashboardId);
        if (Objects.isNull(dashboard)) {
            return null;
        }
        deleteDashboard(dashboard);
        dashboardUserService.deleteDashboardUser(dashboardId);
        if (StringUtils.equals(dashboard.getDashboardType(), DashboardType.INTERNAL.getValue())) {
            return dashboard;
        }
        dashboardLayoutService.deleteDashboardLayout(dashboardId);
        return dashboard;
    }

    @Override
    public List<DashboardDTO> queryDashboard() {
        List<DashboardDTO> dashboards = dashboardMapper.queryDashboard(obtainUserId());
        if (CollectionUtils.isNotEmpty(dashboards)) {
            return dashboards;
        }
        return dashboardMapper.select(new DashboardDTO().setDashboardType(DashboardType.INTERNAL.getValue())
                .setDefaultFlag(BaseConstants.Flag.YES));
    }

    private void deleteDashboard(DashboardDTO dashboard) {
        if (StringUtils.equals(dashboard.getDashboardType(), DashboardType.INTERNAL.getValue())) {
            return;
        }
        dashboardMapper.deleteByPrimaryKey(dashboard);
    }

    private void check(DashboardDTO dashboardDTO) {
        if (Objects.isNull(dashboardDTO)) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_DASHBOARD_NOT_EXIST);
        }
        DashboardUserDTO dashboardUserDTO =
                dashboardUserMapper.selectOne(new DashboardUserDTO().setDashboardId(dashboardDTO.getDashboardId())
                        .setUserId(obtainUserId()));
        if (Objects.isNull(dashboardUserDTO)) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_DASHBOARD_NOT_ASSIGN);
        }
    }

    private Long obtainUserId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_USER_GET);
        }
        return customUserDetails.getUserId();
    }
}
