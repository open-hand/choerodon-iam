package io.choerodon.base.app.service;

import io.choerodon.base.infra.dto.UserDashboardDTO;

import java.util.List;


/**
 * @author dongfan117@gmail.com
 */
public interface UserDashboardService {

    List<UserDashboardDTO> list(String level, Long sourceId);

    List<UserDashboardDTO> update(String level, Long sourceId, List<UserDashboardDTO> dashboardList);

    void reset(String level, Long sourceId);
}