package io.choerodon.base.infra.mapper;

import io.choerodon.base.infra.dto.OrganizationCategoryDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/6/5
 */
public interface OrganizationCategoryMapper extends Mapper<OrganizationCategoryDTO> {
    /**
     * 查询（根据参数模糊搜索name）
     *
     * @param param 参数
     * @return list
     */
    List<OrganizationCategoryDTO> selectByParam(@Param("param") String param, @Param("organizationCategoryDTO") OrganizationCategoryDTO organizationCategoryDTO);
}
