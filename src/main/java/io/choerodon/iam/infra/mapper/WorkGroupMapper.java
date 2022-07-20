package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.WorkGroupDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-11-08 15:30
 */
public interface WorkGroupMapper extends BaseMapper<WorkGroupDTO> {

    void deleteByWorkGroupIds(@Param("organizationId") Long organiztionId, @Param("workGroupIds") List<Long> workGroupIds);

    String queryMinRank(@Param("organizationId") Long organizationId, @Param("parentId") Long parentId);

    String queryMaxRank(@Param("organizationId") Long organizationId, @Param("parentId") Long parentId);

    List<WorkGroupDTO> selectByOrganiztionId(@Param("organizationId") Long organizationId);

    String queryRank(@Param("organizationId") Long organizationId, @Param("parentId") Long parentId, @Param("outSetId") Long outSetId);

    String queryLeftRank(@Param("organizationId") Long organizationId, @Param("parentId") Long parentId, @Param("rightRank") String rightRank);

    String queryRightRank(@Param("organizationId") Long organizationId, @Param("parentId") Long parentId, @Param("leftRank") String leftRank);

    List<Long> selectIdsByOrganizationId(@Param("organizationId") Long organizationId, @Param("ignoredWorkGroupIds") List<Long> ignoredWorkGroupIds);

    WorkGroupDTO queryByOpenObjectId(@Param("organizationId") Long organizationId,
                                     @Param("openObjectId") String openObjectId,
                                     @Param("openType") String openType);

    void updateOpenObjectIdById(@Param("id") Long id, @Param("openObjectId") String openObjectId, @Param("openType") String openType);
}
