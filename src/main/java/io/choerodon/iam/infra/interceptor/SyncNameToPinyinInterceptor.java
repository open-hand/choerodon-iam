package io.choerodon.iam.infra.interceptor;

import static io.choerodon.iam.app.service.impl.C7nUserServiceImpl.PHONE_SUFFIX;

import org.apache.commons.lang3.ObjectUtils;
import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.choerodon.iam.infra.mapper.UserC7nMapper;
import io.choerodon.iam.infra.utils.StringUtil;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/2/4
 * @Modified By:
 */
@Component
public class SyncNameToPinyinInterceptor implements HandlerInterceptor<User> {
    @Autowired
    private UserC7nMapper userC7nMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void interceptor(User user) {
        if (user.getId() != null && !StringUtils.isEmpty(user.getRealName())) {
            userC7nMapper.updatePinyinById(user.getId(), StringUtil.toPinyin(user.getRealName()));
            userC7nMapper.updatePinyinHeadCharById(user.getId(), StringUtil.getPinYinHeadChar(user.getRealName()));
        }
        // 停用用户手机号加上特殊字符
        if (user.getEnabled() != null && !user.getEnabled() && ObjectUtils.isNotEmpty(user.getPhone())) {
            if (!user.getPhone().contains(PHONE_SUFFIX)) {
                user.setPhone(user.getPhone() + PHONE_SUFFIX);
                userRepository.updateOptional(user, User.FIELD_PHONE);
            }
        }
    }
}
