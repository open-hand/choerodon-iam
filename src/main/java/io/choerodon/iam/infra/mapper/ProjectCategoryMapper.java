package io.choerodon.iam.infra.mapper;


import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author jiameng.cao
 * @since 2019/6/4
 */
public interface ProjectCategoryMapper extends BaseMapper<ProjectCategoryDTO> {

    /**
     * 模糊查询projectType
     *
     * @param name
     * @param code
     * @param param
     * @return ProjectCategoryDTO列表
     */
    List<ProjectCategoryDTO> fuzzyQuery(@Param("name") String name,
                                        @Param("code") String code,
                                        @Param("param") String param);

    List<Long> ListIdByCodes(@Param("codes") Set<String> codes);

    /**
     * 查询项目拥有的项目类型
     * @param projectId 项目id
     * @return
     */
    List<ProjectCategoryDTO> listByProjectId(@Param("projectId") Long projectId);
}
