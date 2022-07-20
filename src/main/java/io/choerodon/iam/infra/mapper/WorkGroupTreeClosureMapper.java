package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.WorkGroupTreeClosureDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-11-10 19:25
 */
public interface WorkGroupTreeClosureMapper extends BaseMapper<WorkGroupTreeClosureDTO> {

    List<Long> queryAncestor(@Param("organizationId") Long organizationId, @Param("workGroupId") Long workGroupId);

    List<Long> queryDescendant(@Param("organizationId") Long organizationId, @Param("workGroupIds") List<Long> workGroupIds);

    void batchInsert(@Param("organizationId") Long organizationId, @Param("userId") Long userId, @Param("workGroupTreeClosures") List<WorkGroupTreeClosureDTO> workGroupTreeClosureDTOS);

    void deleteDescendant(@Param("organizationId") Long organizationId, @Param("childrens") List<Long> childrens);

    void deleteByAncestorsAndDescendants(@Param("organizationId") Long organizationId, @Param("ancestors") List<Long> oldAncestors, @Param("descendants") List<Long> descendants);
}
