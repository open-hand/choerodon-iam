package io.choerodon.base.infra.mapper;

import io.choerodon.base.api.vo.MenuCodeDTO;
import io.choerodon.base.infra.dto.CategoryMenuDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/6/5
 */
public interface CategoryMenuMapper extends Mapper<CategoryMenuDTO> {

    List<CategoryMenuDTO> selectByCode(@Param("code") String code);

    List<String> getMenuCodesByOrgId(@Param("organizationId") Long organizationId, @Param("resourceLevel") String resourceLevel);

    List<MenuCodeDTO> selectPermissionCodeIdsByCode(@Param("code") String code, @Param("resourceLevel") String resourceLevel);

}
