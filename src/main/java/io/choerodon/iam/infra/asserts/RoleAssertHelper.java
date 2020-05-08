package io.choerodon.iam.infra.asserts;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.core.exception.ext.NotExistedException;
import org.hzero.iam.domain.entity.Role;
import org.hzero.iam.infra.mapper.RoleMapper;
import org.springframework.stereotype.Component;

/**
 * 角色断言帮助类
 *
 * @author superlee
 * @since 2019-04-15
 */
@Component
public class RoleAssertHelper extends AssertHelper {

    private static final String ERROR_ROLE_NOT_EXIST = "error.role.not.exist";

    private RoleMapper roleMapper;

    public RoleAssertHelper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
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
}
