package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.DashboardCardDTO;
import io.choerodon.mybatis.common.BaseMapper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardCardMapper extends BaseMapper<DashboardCardDTO> {
    List<DashboardCardDTO> queryDashboardCard(@Param("groupId") String groupId);
}
