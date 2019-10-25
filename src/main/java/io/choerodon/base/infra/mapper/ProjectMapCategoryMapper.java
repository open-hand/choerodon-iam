package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.dto.ProjectMapCategorySimpleDTO;
import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.base.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @author Eugen
 */
public interface ProjectMapCategoryMapper extends Mapper<ProjectMapCategoryDTO> {

    List<String> selectProjectCategories(@Param("projectId") Long projectId);

    List<ProjectCategoryDTO> selectProjectCategoryNames(@Param("projectId") Long projectId);

    /**
     * 批量插入
     *
     * @param records
     * @return
     */
    int batchInsert(@Param("records") List<ProjectMapCategoryDTO> records);

    List<ProjectMapCategorySimpleDTO> selectAllProjectMapCategories();

}
