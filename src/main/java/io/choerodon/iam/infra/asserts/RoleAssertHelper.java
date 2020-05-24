package io.choerodon.iam.infra.asserts;

import java.util.List;

import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.iam.infra.mapper.RoleC7nMapper;

/**
 * 角色断言帮助类
 *
 * @author superlee
 * @since 2019-04-15
 */
@Component
public class RoleAssertHelper extends AssertHelper {

    private static final String ERROR_ROLE_NOT_EXIST = "error.role.not.exist";
    private static final String EROOR_ROLE_NOT_EXIST_WITH_LABEL = "error.role.not.exist.with.label";

    private RoleMapper roleMapper;
    private RoleC7nMapper roleC7nMapper;

    public RoleAssertHelper(RoleMapper roleMapper, RoleC7nMapper roleC7nMapper) {
        this.roleMapper = roleMapper;
        this.roleC7nMapper = roleC7nMapper;
    }

    public void codeExisted(String code) {
        Role dto = new Role();
        dto.setCode(code);
        if (roleMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.role.code.existed");
        }
    }

    public Role roleNotExisted(Long id) {
        return roleNotExisted(id, ERROR_ROLE_NOT_EXIST);
    }

    public Role roleNotExisted(Long id, String message) {
        Role dto = roleMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException(message, id);
        }
        return dto;
    }

    public Role roleNotExisted(String code) {
        return roleNotExisted(code, ERROR_ROLE_NOT_EXIST);
    }

    public Role roleNotExisted(String code, String message) {
        Role dto = new Role();
        dto.setCode(code);
        Role result = roleMapper.selectOne(dto);
        if (result == null) {
            throw new NotExistedException(message, code);
        }
        return result;
    }

    /**
     * 在租戶下查詢指定角色标签的角色，并判断🕊非空
     *
     * @param tenantId  租户id
     * @param roleLabel 角色标签
     * @return 非空角色列表
     */
    public List<Role> roleExistedWithLabel(Long tenantId, String roleLabel) {
        List<Role> results = roleC7nMapper.getByTenantIdAndLabel(tenantId, roleLabel);
        if (CollectionUtils.isEmpty(results)) {
            throw new NotExistedException(EROOR_ROLE_NOT_EXIST_WITH_LABEL, roleLabel, tenantId);
        }
        return results;
    }
}
