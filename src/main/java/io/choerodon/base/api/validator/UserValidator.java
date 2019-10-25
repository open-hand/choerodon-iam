package io.choerodon.base.api.validator;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.StringUtils;

import io.choerodon.base.infra.dto.RoleDTO;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.core.exception.CommonException;

public class UserValidator {

    public interface UserGroup {
        //email
        //phone
        //realName
    }


    public interface UserInfoGroup {
        //email
        //phone
    }

    /**
     * 创建用户检验.
     *
     * @param userDTO    用户DTO
     * @param checkRoles 是否校验角色必输
     */
    public static void validateCreateUserWithRoles(UserDTO userDTO, boolean checkRoles) {
        validateRealName(userDTO.getRealName());
        validateEmail(userDTO.getEmail());
        validatePassword(userDTO.getPassword());
        if (checkRoles) {
            validateUseRoles(userDTO.getRoles(), false);
        }
    }

    /**
     * 校验用户名.
     * 不能为空
     *
     * @param realName 用户名
     */
    private static void validateRealName(String realName) {
        if (StringUtils.isEmpty(realName)) {
            throw new CommonException("error.user.realName.empty");
        }

    }

    /**
     * 校验用户邮箱.
     * 不能为空且合法
     *
     * @param email 用户邮箱
     */
    private static void validateEmail(String email) {
        if (StringUtils.isEmpty(email)) {
            throw new CommonException("error.user.email.empty");
        }
        if (!Pattern.matches(UserDTO.EMAIL_REG, email)) {
            throw new CommonException("error.user.email.illegal");
        }
    }

    /**
     * 校验用户密码.
     * 不能为空
     *
     * @param password 用户密码
     */
    private static void validatePassword(String password) {
        if (StringUtils.isEmpty(password)) {
            throw new CommonException("error.user.password.empty");
        }
    }

    /**
     * 校验用户角色.
     * 角色id不能为空
     *
     * @param roles          用户角色列表
     * @param allowRoleEmpty 是否允许用户角色为空
     */
    public static void validateUseRoles(List<RoleDTO> roles, boolean allowRoleEmpty) {
        if (CollectionUtils.isEmpty(roles)) {
            if (!allowRoleEmpty) {
                throw new CommonException("error.user.roles.empty");
            }
        } else {
            roles.forEach(role -> {
                if (role.getId() == null) {
                    throw new CommonException("error.roleId.null");
                }
            });
        }
    }
}
