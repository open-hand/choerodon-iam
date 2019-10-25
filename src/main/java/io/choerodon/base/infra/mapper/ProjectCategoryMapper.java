package io.choerodon.base.infra.mapper;


import io.choerodon.base.infra.dto.ProjectCategoryDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/6/4
 */
public interface ProjectCategoryMapper extends Mapper<ProjectCategoryDTO> {


    List<ProjectCategoryDTO> selectProjectCategoriesByOrgId(@Param("organizationId") Long organizationId, @Param("param") String param, @Param("projectCategoryDTO") ProjectCategoryDTO projectCategoryDTO);

    List<ProjectCategoryDTO> selectProjectCategoriesListByOrgId(@Param("organizationId") Long organizationId,
                                                                @Param("param") String param);

    List<ProjectCategoryDTO> selectByParam(@Param("param") String param, @Param("projectCategoryDTO") ProjectCategoryDTO projectCategoryDTO);

    Long getIdByCode(@Param("agile") String agile);

}
