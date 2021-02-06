package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.ProjectMapCategorySimpleVO;
import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.iam.infra.dto.ProjectMapCategoryDTO;
import io.choerodon.mybatis.common.BaseMapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;


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
     * @return 插入的数量
     */
    int batchInsert(@Param("records") List<ProjectMapCategoryDTO> records);

    List<ProjectMapCategorySimpleVO> selectAllProjectMapCategories();

    int batchDelete(@Param("projectId") Long projectId, @Param("ids") List<Long> ids);

    List<String> listProjectCategoryById(@Param("projectId") Long projectId);

}
