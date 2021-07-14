package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.DashboardDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardMapper extends BaseMapper<DashboardDTO> {

    /**
     * 查询用户下所有视图
     *
     * @param userId
     * @return
     */
    List<DashboardDTO> queryDashboard(@Param("userId") Long userId);

    /**
     *
     *
     * @param userId
     * @param filterFlag
     * @return
     */
    List queryInternalDashboard(@Param("userId") Long userId, @Param("filterFlag") Integer filterFlag);
}
