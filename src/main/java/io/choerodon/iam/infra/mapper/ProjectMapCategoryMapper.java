package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.iam.api.vo.ProjectMapCategorySimpleVO;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.mybatis.common.BaseMapper;


/**
 * @author Eugen
 */
public interface ProjectMapCategoryMapper extends BaseMapper<ProjectMapCategoryDTO> {

    List<String> selectProjectCategories(@Param("projectId") Long projectId);

    List<ProjectCategoryDTO> selectProjectCategoryNames(@Param("projectId") Long projectId);

    /**
     * 批量插入
     *
     * @param records
     * @return
     */
    int batchInsert(@Param("records") List<ProjectMapCategoryDTO> records);

    List<ProjectMapCategorySimpleVO> selectAllProjectMapCategories();

}
