package io.choerodon.base.app.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.base.app.service.DemoRegisterService;
import io.choerodon.base.infra.dto.UserDTO;
import io.choerodon.base.infra.feign.DevopsFeignClient;
import io.choerodon.base.infra.mapper.UserMapper;
import io.choerodon.core.exception.CommonException;

/**
 * @author superlee, Eugen
 */
@Component
public class DemoRegisterServiceImpl implements DemoRegisterService {
    @Value("${choerodon.devops.message:false}")
    private boolean devopsMessage;

    public void setDevopsMessage(boolean devopsMessage) {
        this.devopsMessage = devopsMessage;
    }

    private UserMapper userMapper;

    private DevopsFeignClient devopsFeignClient;

    public DemoRegisterServiceImpl(UserMapper userMapper,
                                   DevopsFeignClient devopsFeignClient) {
        this.userMapper = userMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public void checkEmail(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        List<UserDTO> select = userMapper.select(userDTO);
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
