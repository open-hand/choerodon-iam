package io.choerodon.iam.app.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.iam.infra.constant.DashboardConstants;
import io.choerodon.iam.infra.dto.DashboardCardDTO;
import io.choerodon.iam.infra.mapper.DashboardCardMapper;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.springframework.stereotype.Service;
import io.choerodon.iam.app.service.DashboardCardService;

import java.util.Objects;

/**
 * 应用服务默认实现
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@Service
public class DashboardCardServiceImpl implements DashboardCardService {

    private DashboardCardMapper dashboardCardMapper;

    public DashboardCardServiceImpl(DashboardCardMapper dashboardCardMapper){
        this.dashboardCardMapper = dashboardCardMapper;
    }

    @Override
    public Page<DashboardCardDTO> pageDashboardCard(String groupId, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> dashboardCardMapper.queryDashboardCard(obtainUserId(),
                groupId));
    }

    private Long obtainUserId() {
        CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
        if (Objects.isNull(customUserDetails) || Objects.isNull(customUserDetails.getUserId())) {
            throw new CommonException(DashboardConstants.ErrorCode.ERROR_USER_GET);
        }
        return customUserDetails.getUserId();
    }
}
