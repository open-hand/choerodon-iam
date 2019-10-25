package io.choerodon.base.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.infra.dto.MenuDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author wuguokai
 * @author superlee
 */
public interface MenuMapper extends Mapper<MenuDTO> {

    /**
     * 根据层级查菜单附带权限，不包含top菜单
     *
     * @param level 层级
     * @return 对应层级的菜单列表
     */
    List<MenuDTO> selectMenusWithPermission(String level);


    /**
     * 根据权限和项目组织类型查询菜单，admin被认为拥有所有权限
     *
     * @param userId     用户ID
     * @param sourceId   来源ID
     * @param sourceType 来源类型 组织或者项目
     * @param categories 组织项目类型，取交集菜单
     * @return 可以显示的 menu, menu_item, tab
     */
    List<MenuDTO> selectMenusByPermissionAndCategory(@Param("admin") Boolean admin,
                                                     @Param("userId") Long userId,
                                                     @Param("sourceId") Long sourceId,
                                                     @Param("sourceType") String sourceType,
                                                     @Param("categories") List<String> categories,
                                                     @Param("parentCategory") String parentCategory);

}
