package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.infra.dto.ProjectTypeDTO;
import io.choerodon.mybatis.common.BaseMapper;

public interface ProjectTypeMapper extends BaseMapper<ProjectTypeDTO> {
    /**
     * 模糊查询projectType
     *
     * @param name
     * @param code
     * @param param
     * @return
     */
    List<ProjectTypeDTO> fuzzyQuery(@Param("name") String name,
                                    @Param("code") String code,
                                    @Param("param") String param);
}
