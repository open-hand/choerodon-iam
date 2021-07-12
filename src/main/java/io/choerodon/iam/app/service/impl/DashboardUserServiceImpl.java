package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.iam.infra.dto.DashboardUserDTO;
import io.choerodon.iam.infra.enums.DashboardType;
import io.choerodon.iam.infra.mapper.DashboardMapper;
import io.choerodon.iam.infra.mapper.DashboardUserMapper;
import org.apache.commons.lang.StringUtils;
import java.util.Objects;
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

    public DashboardUserServiceImpl(DashboardUserMapper dashboardUserMapper,
                                    DashboardMapper dashboardMapper) {
        this.dashboardUserMapper = dashboardUserMapper;
        this.dashboardMapper = dashboardMapper;
    }

    @Override
    public DashboardUserDTO createDashboardUser(DashboardUserDTO dashboardUser) {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            return null;
        }
        dashboardUser.setUserId(customUserDetails.getUserId());
        check(dashboardUser);
        dashboardUserMapper.insert(dashboardUser);
        return dashboardUser;
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
        if (StringUtils.equals(dashboard.getDashboardType(), DashboardType.CUSTOMIZE.getValue()) ||
                !Objects.equals(dashboard.getCreatedBy(), dashboardUser.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_NOT_ASSIGN_DASHBOARD);
        }
        DashboardUserDTO dashboardUserDTO = dashboardUserMapper.selectOne(dashboardUser);
        if(Objects.nonNull(dashboardUserDTO)){
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_ALREADY_ASSIGN_DASHBOARD);
        }
    }
}
