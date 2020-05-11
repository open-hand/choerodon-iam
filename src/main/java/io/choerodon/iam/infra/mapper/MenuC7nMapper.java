package io.choerodon.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Menu;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/23 17:36
 */
public interface MenuC7nMapper {
    /**
     * 根据角色查询菜单列表，用于左侧菜单树展示
     * <p>
     * <lu>
     * <li>平台管理员/租户管理员角色能够查看所有菜单树</li>
     * <li>其他角色只能查看其分配了菜单权限集的菜单树</li> </lu>
     *
     * @param roleIds   角色合并的ID集合
     * @param tenantId  租户ID
     * @param projectId 项目ID
     * @param lang      菜单语言
     * @param labels    标签IDs
     * @return 经过权限检测过后的菜单树
     */
    List<Menu> selectRoleMenus(@Param("roleIds") List<Long> roleIds,
                               @Param("tenantId") Long tenantId,
                               @Param("projectId") Long projectId,
                               @Param("lang") String lang,
                               @Param("labels") Set<String> labels);


    /**
     * 查询个人中心菜单
     */
    List<Menu> selectUserMenus(@Param("lang") String lang,
                               @Param("labels") Set<String> labels);
}
