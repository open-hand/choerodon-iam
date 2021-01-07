package io.choerodon.iam.app.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.DemoRegisterService;
import io.choerodon.iam.infra.dto.UserDTO;
import io.choerodon.iam.infra.feign.operator.DevopsFeignClientOperator;
import io.choerodon.iam.infra.mapper.UserC7nMapper;

/**
 * @author superlee, Eugen
 */
@Component
public class DemoRegisterServiceImpl implements DemoRegisterService {
    private UserMapper userMapper;

    private DevopsFeignClientOperator devopsFeignClientOperator;

    public DemoRegisterServiceImpl(UserMapper userMapper,
                                   UserC7nMapper userC7nMapper,
                                   DevopsFeignClientOperator devopsFeignClientOperator) {
        this.userMapper = userMapper;
        this.devopsFeignClientOperator = devopsFeignClientOperator;
    }

    @Override
    public void checkEmail(String email) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(email);
        List<User> select = userMapper.select(userDTO);
        if (CollectionUtils.isNotEmpty(select)) {
            throw new CommonException("error.email.already.exists.in.iam");
        }
        Boolean checkGitlabEmail = devopsFeignClientOperator.checkGitlabEmail(email);
        if (checkGitlabEmail == null) {
            throw new CommonException("error.email.check.gitlab.email.return.empty");
        }
        if (checkGitlabEmail) {
            throw new CommonException("error.email.already.exists.in.gitlab");
        }
    }
}
