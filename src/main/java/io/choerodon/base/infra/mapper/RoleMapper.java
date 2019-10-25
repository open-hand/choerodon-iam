package io.choerodon.base.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.mybatis.common.Mapper;

/**
 * @author wuguokai
 */
public interface RoleMapper extends Mapper<RoleDTO> {

    /**
     * 根据memberId查询在当前source下的角色
     *
     * @param memberId
     * @param sourceType
     * @param sourceId
     * @param memberType
     * @return
     */
    List<RoleDTO> queryRoleByMember(@Param("memberId") Long memberId,
                                    @Param("memberType") String memberType,
                                    @Param("sourceType") String sourceType,
                                    @Param("sourceId") Long sourceId);

    List<RoleDTO> queryRolesInfoByUser(@Param("sourceType") String sourceType,
                                       @Param("sourceId") Long sourceId,
                                       @Param("userId") Long userId);

    List<RoleDTO> fulltextSearch(@Param("name") String name,
                                 @Param("code") String code,
                                 @Param("level") String level,
                                 @Param("builtIn") Boolean builtIn,
                                 @Param("enabled") Boolean enabled,
                                 @Param("params") String params);

    List<RoleDTO> pagingQueryOrgRoles(@Param("orgId") Long orgId,
                                      @Param("name") String name,
                                      @Param("code") String code,
                                      @Param("level") String level,
                                      @Param("builtIn") Boolean builtIn,
                                      @Param("enabled") Boolean enabled,
                                      @Param("params") String params);

    RoleDTO selectRoleWithPermissionsAndLabels(Long id);

    int rolesLevelCount(@Param("roleIds") List<Long> roleIds);

    List<RoleDTO> selectRolesByLabelNameAndType(@Param("name") String name, @Param("type") String type,
                                                @Param("organizationId") Long organizationId);

    List<RoleDTO> selectInitRolesByPermissionCode(String permissionCode);

    List<RoleDTO> fuzzySearchRolesByName(@Param("roleName") String roleName,
                                         @Param("sourceType") String sourceType,
                                         @Param("sourceId") Long sourceId,
                                         @Param("onlySelectEnable") Boolean onlySelectEnable);
}
