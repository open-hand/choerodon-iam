package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.WorkGroupVO;
import io.choerodon.iam.infra.dto.WorkGroupProjectRelDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface WorkGroupProjectRelMapper extends BaseMapper<WorkGroupProjectRelDTO> {
    List<WorkGroupVO> selectWorkGroupByProjectIds(@Param("organizationId") Long organizationId, @Param("projectIds") Set<Long> projectIds);

    List<Long> listProjectIdsByWorkGroupId(@Param("workGroupIds") List<Long> workGroupIds);

    void insertSyncData(@Param("insertList") List<WorkGroupProjectRelDTO> insertList);
}
