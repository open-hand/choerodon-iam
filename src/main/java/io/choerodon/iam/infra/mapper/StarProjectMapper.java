package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.infra.dto.ProjectDTO;
import io.choerodon.iam.infra.dto.StarProjectUserRelDTO;
import io.choerodon.mybatis.common.BaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 10:25
 */
public interface StarProjectMapper extends BaseMapper<StarProjectUserRelDTO> {

    List<ProjectDTO> query(@Param("pids") Set<Long> pids, @Param("userId") Long userId);

    /**
     * @param organizationId
     * @param userId
     * @param isAdmin
     * @param size           为空则不限制条数
     * @return ProjectDTO列表
     */
    List<ProjectDTO> queryWithLimit(@Param("organizationId") Long organizationId,
                                    @Param("userId") Long userId,
                                    @Param("isAdmin") boolean isAdmin,
                                    @Param("size") Integer size);

    Long getDbMaxSeq(@Param("organizationId") Long organizationId, @Param("userId") Long userId);
}
