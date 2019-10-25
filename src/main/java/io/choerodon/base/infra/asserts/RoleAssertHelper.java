package io.choerodon.base.infra.asserts;

import org.springframework.stereotype.Component;

import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.core.exception.ext.AlreadyExistedException;
import io.choerodon.core.exception.ext.NotExistedException;
import io.choerodon.base.infra.mapper.RoleMapper;
import io.choerodon.core.exception.CommonException;

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
        RoleDTO dto = new RoleDTO();
        dto.setCode(code);
        if (roleMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException("error.role.code.existed");
        }
    }

    public RoleDTO roleNotExisted(Long id) {
        return roleNotExisted(id, ERROR_ROLE_NOT_EXIST);
    }

    public RoleDTO roleNotExisted(Long id, String message) {
        RoleDTO dto = roleMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException(message, id);
        }
        return dto;
    }

    public RoleDTO roleNotExisted(String code) {
        return roleNotExisted(code, ERROR_ROLE_NOT_EXIST);
    }

    public RoleDTO roleNotExisted(String code, String message) {
        RoleDTO dto = new RoleDTO();
        dto.setCode(code);
        RoleDTO result = roleMapper.selectOne(dto);
        if (result == null) {
            throw new NotExistedException(message, code);
        }
        return result;
    }
}
