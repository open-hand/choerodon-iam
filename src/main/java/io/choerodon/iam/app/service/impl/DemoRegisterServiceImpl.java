package io.choerodon.iam.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.DemoRegisterService;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.feign.DevopsFeignClient;
import io.choerodon.iam.infra.mapper.UserC7nMapper;
import org.apache.commons.collections.CollectionUtils;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * @author superlee, Eugen
 */
@Component
public class DemoRegisterServiceImpl implements DemoRegisterService {
    private UserMapper userMapper;

    private UserC7nMapper userC7nMapper;

    private DevopsFeignClient devopsFeignClient;

    public DemoRegisterServiceImpl(UserMapper userMapper,
                                   UserC7nMapper userC7nMapper,
                                   DevopsFeignClient devopsFeignClient) {
        this.userMapper = userMapper;
        this.userC7nMapper = userC7nMapper;
        this.devopsFeignClient = devopsFeignClient;
    }

    @Override
    public void checkEmail(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        List<User> select = userMapper.select(userDTO);
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
