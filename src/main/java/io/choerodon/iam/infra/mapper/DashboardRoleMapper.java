package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.infra.dto.DashboardRoleDTO;
import io.choerodon.mybatis.common.BaseMapper;


/**
 * @author dongfan117@gmail.com
 */
public interface DashboardRoleMapper extends BaseMapper<DashboardRoleDTO> {

    List<String> selectRoleCodes(@Param("dashboardCode") String dashboardCode);

    void deleteByDashboardCode(@Param("dashboardCode") String dashboardCode);

    List<Long> selectDashboardByUserId(@Param("userId") Long userId,
                                       @Param("sourceId") Long sourceId,
                                       @Param("level") String level);
}
