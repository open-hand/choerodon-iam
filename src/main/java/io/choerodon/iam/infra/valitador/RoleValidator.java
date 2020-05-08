package io.choerodon.iam.infra.valitador;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;
import org.hzero.iam.domain.entity.Role;


public class RoleValidator {
    /**
     * 校验用户角色.
     * 1. 角色需要与当前层级匹配
     * 2. 角色为启用状态
     * 3. 如果是组织层创建的角色必须为本组织角色
     *
     * @param sourceType     资源层级
     * @param sourceId       资源Id
     * @param role           角色DTO
     * @param allowRoleEmpty 是否允许用户角色为禁用
     */
    public static void validateRole(String sourceType, Long sourceId, Role role, boolean allowRoleEmpty) {
        String roleCode = role.getCode();
        if (!sourceType.equals(role.getLevel())) {
            throw new CommonException("error.user.role.level.illegal", roleCode);
        }
        if (!allowRoleEmpty && role.getEnabled() != null && !role.getEnabled()) {
            throw new CommonException("error.user.role.disabled", roleCode);
        }
        boolean isNotOrgRole = ResourceLevel.ORGANIZATION.value().equals(sourceType)
                && role.getTenantId() != null && !sourceId.equals(role.getTenantId());
        if (isNotOrgRole) {
            throw new CommonException("error.user.role.org.illegal", roleCode);
        }
    }


}
