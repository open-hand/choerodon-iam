package io.choerodon.base.app.service;

import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Pageable;
import io.choerodon.base.infra.dto.DashboardDTO;

/**
 * @author dongfan117@gmail.com
 */
public interface DashboardService {

    DashboardDTO update(Long dashboardId, DashboardDTO dashboardDTO, Boolean updateRole);

    DashboardDTO query(Long dashboardId);

    PageInfo<DashboardDTO> list(DashboardDTO dashboardDTO, Pageable Pageable, String param);

    void reset(Long dashboardId);
}
