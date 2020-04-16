package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.base.app.service.DemoRegisterC7nService;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.core.exception.CommonException;

/**
 * @author superlee, Eugen
 */
@Component
public class DemoRegisterC7nServiceImpl implements DemoRegisterC7nService {
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    public void setDevopsMessage(boolean devopsMessage) {
        this.devopsMessage = devopsMessage;
    }

    private UserMapper userMapper;

    private DevopsFeignClient devopsFeignClient;

    public DemoRegisterC7nServiceImpl(UserMapper userMapper,
                                      DevopsFeignClient devopsFeignClient) {
        this.userMapper = userMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public void checkEmail(String email) {
        User user = new User();
        user.setEmail(email);
        List<User> select = userMapper.select(user);
        if (CollectionUtils.isNotEmpty(select)) {
            throw new CommonException("error.email.already.exists.in.iam");
        }
        ResponseEntity<Boolean> checkGitlabEmail = devopsFeignClient.checkGitlabEmail(email);
        if (ObjectUtils.isEmpty(checkGitlabEmail) || checkGitlabEmail.getBody() == null) {
            throw new CommonException("error.email.check.gitlab.email.return.empty");
        }
        if (checkGitlabEmail.getBody()) {
            throw new CommonException("error.email.already.exists.in.gitlab");
        }
    }
}
