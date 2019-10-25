package io.choerodon.base.api.validator;

import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.iam.ResourceLevel;


public class RoleValidator {
    /**
     * 校验用户角色.
     * 1. 角色需要与当前层级匹配
     * 2. 角色为启用状态
     * 3. 如果是组织层创建的角色必须为本组织角色
     *
     * @param sourceType     资源层级
     * @param sourceId       资源Id
     * @param roleDTO        角色DTO
     * @param allowRoleEmpty 是否允许用户角色为禁用
     */
    public static void validateRole(String sourceType, Long sourceId, RoleDTO roleDTO, boolean allowRoleEmpty) {
        String roleCode = roleDTO.getCode();
        if (!sourceType.equals(roleDTO.getResourceLevel())) {
            throw new CommonException("error.user.role.level.illegal", roleCode);
        }
        if (!allowRoleEmpty && roleDTO.getEnabled() != null && !roleDTO.getEnabled()) {
            throw new CommonException("error.user.role.disabled", roleCode);
        }
        boolean isNotOrgRole = ResourceLevel.ORGANIZATION.value().equals(sourceType)
                && roleDTO.getOrganizationId() != null && !sourceId.equals(roleDTO.getOrganizationId());
        if (isNotOrgRole) {
            throw new CommonException("error.user.role.org.illegal", roleCode);
        }

    }


}
