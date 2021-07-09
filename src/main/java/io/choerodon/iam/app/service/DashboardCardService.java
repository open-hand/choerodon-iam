package io.choerodon.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.iam.infra.dto.DashboardCardDTO;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import java.util.List;

/**
 * 应用服务
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardCardService {
    /**
     * 分页查询卡片
     *
     * @param groupId
     * @param pageRequest
     * @return
     */
    Page<DashboardCardDTO> pageDashboardCard(String groupId, PageRequest pageRequest);
}
