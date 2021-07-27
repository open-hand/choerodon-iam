package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.app.service.DashboardService;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.iam.infra.dto.DashboardUserDTO;
import io.choerodon.iam.infra.enums.DashboardType;
import io.choerodon.iam.infra.mapper.DashboardMapper;
import io.choerodon.iam.infra.mapper.DashboardUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.hzero.core.base.BaseConstants;
import org.springframework.stereotype.Service;
import io.choerodon.iam.app.service.DashboardUserService;

/**
 * 应用服务默认实现
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@Service
public class DashboardUserServiceImpl implements DashboardUserService {

    private DashboardUserMapper dashboardUserMapper;

    private DashboardMapper dashboardMapper;

    private DashboardService dashboardService;

    public DashboardUserServiceImpl(DashboardUserMapper dashboardUserMapper,
                                    DashboardMapper dashboardMapper,
                                    DashboardService dashboardService) {
        this.dashboardUserMapper = dashboardUserMapper;
        this.dashboardMapper = dashboardMapper;
        this.dashboardService = dashboardService;
    }

    @Override
    public DashboardUserDTO createDashboardUser(DashboardUserDTO dashboardUser) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            return null;
        }
        dashboardUser.setUserId(customUserDetails.getUserId());
        check(dashboardUser);
        DashboardDTO dashboardDTO = dashboardMapper.selectByPrimaryKey(dashboardUser.getDashboardId());
        if (StringUtils.equals(dashboardDTO.getDashboardType(), DashboardType.INTERNAL.getValue())) {
            createInternalDashboardUser(dashboardUser);
            return dashboardUser;
        }
        createCustomizeDashboardUser(dashboardUser);
        return dashboardUser;
    }

    private void createCustomizeDashboardUser(DashboardUserDTO dashboardUser) {
        Integer rank = dashboardUserMapper.queryMaxRankByUserId(dashboardUser.getUserId());
        if (Objects.isNull(rank)) {
            dashboardUser.setRank(BaseConstants.Digital.ZERO);
        } else {
            dashboardUser.setRank(++rank);
        }
        dashboardUserMapper.insert(dashboardUser);
    }

    private void createInternalDashboardUser(DashboardUserDTO dashboardUser) {
        List<DashboardUserDTO> customizeDashboardUserDTOS = dashboardUserMapper.queryCustomizeDashboardByUserId(dashboardUser.getUserId());
        if (CollectionUtils.isEmpty(customizeDashboardUserDTOS)) {
            createCustomizeDashboardUser(dashboardUser);
            return;
        }
        Integer rank = customizeDashboardUserDTOS.get(BaseConstants.Digital.ZERO).getRank();
        dashboardUser.setRank(rank);
        for (DashboardUserDTO customizeDashboardUser : customizeDashboardUserDTOS) {
            customizeDashboardUser.setRank(++rank);
        }
        customizeDashboardUserDTOS.add(dashboardUser);
        customizeDashboardUserDTOS.forEach(customizeDashboardUser ->
                dashboardUserMapper.updateOptional(customizeDashboardUser, DashboardUserDTO.FIELD_RANK));
    }

    @Override
    public DashboardUserDTO deleteDashboardUser(Long dashboardId) {
        DashboardUserDTO dashboardUserDTO =
                dashboardUserMapper.selectOne(new DashboardUserDTO().setDashboardId(dashboardId)
                        .setUserId(obtainUserId()));
        if (Objects.isNull(dashboardUserDTO)) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_DASHBOARD_NOT_ASSIGN);
        }
        dashboardUserMapper.deleteByPrimaryKey(dashboardUserDTO);
        return dashboardUserDTO;
    }

    @Override
    public List<DashboardDTO> batchUpdateDashboardUserRank(List<DashboardUserDTO> dashboardUserS) {
        if (CollectionUtils.isEmpty(dashboardUserS)) {
            return Collections.EMPTY_LIST;
        }
        for (int i = 0; i < dashboardUserS.size(); i++) {
            dashboardUserS.get(i).setRank(i);
            dashboardUserMapper.updateOptional(dashboardUserS.get(i), DashboardUserDTO.FIELD_RANK);
        }
        return dashboardService.queryDashboard();
    }

    private Long obtainUserId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_USER_GET);
        }
        return customUserDetails.getUserId();
    }

    private void check(DashboardUserDTO dashboardUser) {
        DashboardDTO dashboard = dashboardMapper.selectByPrimaryKey(dashboardUser.getDashboardId());
        if (Objects.isNull(dashboard)) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_DASHBOARD_NOT_EXIST);
        }
        if (StringUtils.equals(dashboard.getDashboardType(), DashboardType.CUSTOMIZE.getValue()) &&
                !Objects.equals(dashboard.getCreatedBy(), dashboardUser.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_NOT_ASSIGN_DASHBOARD);
        }
        DashboardUserDTO dashboardUserDTO = dashboardUserMapper.selectOne(dashboardUser);
        if (Objects.nonNull(dashboardUserDTO)) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_ALREADY_ASSIGN_DASHBOARD);
        }
    }
}
