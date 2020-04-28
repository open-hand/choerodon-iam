package io.choerodon.iam.infra.asserts;

import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.exception.ext.AlreadyExistedException;

/**
 * 用户断言帮助类
 *
 * @author superlee
 * @since 2019-05-10
 */
@Component
public class UserAssertHelper extends AssertHelper {

    private UserMapper userMapper;

    public UserAssertHelper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User userNotExisted(Long id) {
        return userNotExisted(id, "error.user.not.exist");
    }

    public User userNotExisted(WhichColumn whichColumn, String value) {
        switch (whichColumn) {
            case LOGIN_NAME:
                return loginNameNotExisted(value, "error.user.loginName.not.existed");
            case EMAIL:
                return emailNotExisted(value, "error.user.email.not.existed");
            default:
                throw new CommonException("error.illegal.whichColumn", whichColumn.value);
        }
    }

    private User emailNotExisted(String email, String message) {
        User dto = new User();
        dto.setEmail(email);
        User result = userMapper.selectOne(dto);
        if (result == null) {
            throw new CommonException(message, email);
        }
        return result;
    }

    private User loginNameNotExisted(String loginName, String message) {
        User dto = new User();
        dto.setLoginName(loginName);
        User result = userMapper.selectOne(dto);
        if (result == null) {
            throw new CommonException(message, loginName);
        }
        return result;
    }

    public User userNotExisted(Long id, String message) {
        // todo 改为从redis查询
        User dto = userMapper.selectByPrimaryKey(id);
        if (dto == null) {
            throw new CommonException(message, id);
        }
        return dto;
    }

    public void emailExisted(String email, String message) {
        User dto = new User();
        dto.setEmail(email);
        if (userMapper.selectOne(dto) != null) {
            throw new AlreadyExistedException(message);
        }
    }

    public enum WhichColumn {

        /**
         * 登录名字段
         */
        LOGIN_NAME("login_name"),

        /**
         * 邮箱字段
         */
        EMAIL("email");

        private String value;

        WhichColumn(String value) {
            this.value = value;
        }

        public static boolean contains(String value) {
            for (WhichColumn whichColumn : WhichColumn.values()) {
                if (whichColumn.value.equals(value)) {
                    return true;
                }
            }
            return false;
        }
    }
}
