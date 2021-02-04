package io.choerodon.iam.infra.interceptor;

import org.hzero.core.interceptor.HandlerInterceptor;
import org.hzero.iam.domain.entity.User;
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

    @Override
    public void interceptor(User user) {
        if (user.getId() != null && !StringUtils.isEmpty(user.getRealName())) {
            userC7nMapper.updatePinyinById(user.getId(), StringUtil.toPinyin(user.getRealName()));
        }
    }
}
