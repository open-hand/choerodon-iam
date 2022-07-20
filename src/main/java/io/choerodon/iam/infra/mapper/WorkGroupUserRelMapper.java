package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.WorkGroupVO;
import io.choerodon.iam.infra.dto.WorkGroupUserRelDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author zhaotianxin
 * @date 2021-11-08 16:54
 */
public interface WorkGroupUserRelMapper extends BaseMapper<WorkGroupUserRelDTO> {

    void deleteByWorkGroupIds(@Param("organizationId") Long organizationId, @Param("workGroupIds") List<Long> workGroupIds);

    void batchDelete(@Param("organizationId") Long organizationId, @Param("workGroupIds") List<Long> workGroupIds, @Param("userIds") List<Long> userIds);

    Set<Long> queryByWorkGroupId(@Param("organizationId") Long organizationId, @Param("workGroupId") Long workGroupId);

    Set<Long> listUserIdsByWorkGroupIds(@Param("organizationId") Long organizationId, @Param("workGroupIds") List<Long> workGroupIds);

    List<WorkGroupVO> selectWorkGroupByUserId(@Param("organizationId") Long organizationId, @Param("userIds") List<Long> userIds);

    Set<Long> selectNoGroupUsers(@Param("organizationId") Long organizationId,
                                 @Param("projectIds") List<Long> projectIds,
                                 @Param("startTime") Date startTime,
                                 @Param("endTime") Date endTime);
}
