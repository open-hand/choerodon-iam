package io.choerodon.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.domain.vo.RoleVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * @author scp
 * @date 2020/4/21
 * @description
 */
public interface RoleC7nMapper {

    List<Role> queryRolesInfoByUser(@Param("sourceType") String sourceType,
                                    @Param("sourceId") Long sourceId,
                                    @Param("userId") Long userId);

    /**
     * 根据标签查询组织下角色
     * @param tenantId 组织id
     * @param labelName 标签名
     * @return
     */
    Role getByTenantIdAndLabel(@Param("tenantId") Long tenantId,
                               @Param("labelName")String labelName);

    List<Role> selectRolesByLabelNameAndType(@Param("name") String name, @Param("type") String type,
                                             @Param("organizationId") Long organizationId);

    /**
     * 查询组织下角色(目前只模糊匹配role.name,调用时有需要自己添加)
     * @param tenantId 组织id
     * @param labelName 角色标签name
     * @param param
     * @return
     */
    List<Role> listRolesByTenantIdAndLableWithOptions(@Param("tenantId") Long tenantId,
                                                      @Param("labelName") String labelName,
                                                      @Param("param") RoleVO param);
}
