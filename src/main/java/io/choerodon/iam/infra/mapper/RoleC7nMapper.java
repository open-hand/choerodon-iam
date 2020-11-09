package io.choerodon.iam.infra.mapper;

import io.choerodon.iam.api.vo.UserRoleVO;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.RoleDTO;
import org.hzero.iam.domain.entity.Label;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * @author scp
 * @since 2020/4/21
 */
public interface RoleC7nMapper {

    List<UserRoleVO> selectRoles(@Param("userId") Long userId, @Param("name") String name, @Param("level") String level, @Param("params") String params);


    List<Role> queryRolesInfoByUser(@Param("sourceType") String sourceType,
                                    @Param("sourceId") Long sourceId,
                                    @Param("userId") Long userId);

    /**
     * 根据标签查询组织下角色
     *
     * @param tenantId  组织id
     * @param labelName 标签名
     * @return Role列表
     */
    List<Role> getByTenantIdAndLabel(@Param("tenantId") Long tenantId,
                                     @Param("labelName") String labelName);

    List<Role> selectRolesByLabelNameAndType(@Param("name") String name, @Param("type") String type,
                                             @Param("organizationId") Long organizationId);

    /**
     * 查询组织下角色(目前只模糊匹配role.name,调用时有需要自己添加)
     *
     * @param tenantId  组织id
     * @param labelName 角色标签name
     * @param param
     * @return Role列表
     */
    List<Role> listRolesByTenantIdAndLableWithOptions(@Param("tenantId") Long tenantId,
                                                      @Param("labelName") String labelName,
                                                      @Param("param") RoleVO param);

    /**
     * 查询用户是否拥有组织管理员角色（包括租户超级管理员、租户管理员角色即可）
     *
     * @param userId
     * @param tenantId
     * @return Role列表
     */
    List<Role> getOrgAdminByUserIdAndTenantId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);


    List<io.choerodon.iam.api.vo.RoleVO> fuzzySearchRolesByName(@Param("roleName") String roleName,
                                                                @Param("sourceId") Long sourceId,
                                                                @Param("sourceType") String sourceType,
                                                                @Param("labelName") String labelName,
                                                                @Param("onlySelectEnable") Boolean onlySelectEnable);


    List<io.choerodon.iam.api.vo.RoleVO> fulltextSearch(@Param("tenantId") Long tenantId,
                                                        @Param("name") String name,
                                                        @Param("code") String code,
                                                        @Param("level") String level,
                                                        @Param("builtIn") Boolean builtIn,
                                                        @Param("enabled") Boolean enabled,
                                                        @Param("labelName") String labelName,
                                                        @Param("params") String params);

    Role getTenantAdminRole(Long organizationId);

    List<Label> listRoleLabels(@Param("roleId") Long roleId);

    List<RoleDTO> listRolesByName(@Param("tenantId") Long tenantId,
                                  @Param("name") String name,
                                  @Param("code") String code,
                                  @Param("labelName") String labelName,
                                  @Param("enabled") Boolean enabled);

    List<Role> listProjectRoleByProjectIdAndUserId(@Param("projectId") Long projectId,
                                                   @Param("userId") Long userId);

    List<Role> listByLabelNames(@Param("tenantId") Long tenantId,
                                @Param("labelName") String labelName);

    Set<Long> listOrgByUserIdAndTenantIds(@Param("userId") Long userId,
                                          @Param("orgIds") Set<Long> orgIds);

    List<Role> listByTenantId(@Param("tenantId") Long tenantId);

    List<Role> listMemberRolesForProjectClient(@Param("tenantId") Long tenantId,
                                               @Param("clientId") Long clientId,
                                               @Param("projectId") Long projectId,
                                               @Param("roleName") String roleName);

    /**
     * 查询用户组织层角色标签
     *
     * @return String set 集合
     */
    Set<String> listLabelByTenantIdAndUserId(@Param("userId") Long userId, @Param("tenantId") Long tenantId);

    List<Role> listChildRoleByTplRoleId(@Param("roleId") Long roleId);
}
