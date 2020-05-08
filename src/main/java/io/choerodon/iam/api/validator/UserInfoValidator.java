package io.choerodon.iam.api.validator;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.api.vo.RegisterInfoVO;
import org.springframework.util.StringUtils;

public class UserInfoValidator {
    /**
     * 用户完善信息校验.
     * 用户唯一token不能为空
     * 用户名不能为空
     * 用户密码不能为空
     *
     * @param registerInfoVO 用户信息
     */
    public static void validate(RegisterInfoVO registerInfoVO) {
        if (StringUtils.isEmpty(registerInfoVO.getUserName())) {
            throw new CommonException("error.user.name.empty");
        }
        if (StringUtils.isEmpty(registerInfoVO.getPassword())) {
            throw new CommonException("error.user.password.empty");
        }
        if (StringUtils.isEmpty(registerInfoVO.getUserToken())) {
            throw new CommonException("error.user.token.empty");
        }
    }

}
