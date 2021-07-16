package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.DashboardUserDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
public interface DashboardUserMapper extends BaseMapper<DashboardUserDTO> {

    /**
     * 查询用户下视图最大排序值
     *
     * @param userId
     * @return
     */
    Integer queryMaxRankByUserId(@Param("userId") Long userId);

    /**
     * 查询用户客户化视图
     *
     * @param userId
     * @return
     */
    List<DashboardUserDTO> queryCustomizeDashboardByUserId(@Param("userId") Long userId);
}
