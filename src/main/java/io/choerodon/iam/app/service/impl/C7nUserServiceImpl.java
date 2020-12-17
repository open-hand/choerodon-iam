package io.choerodon.iam.app.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.iam.app.service.impl.UserServiceImpl;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.hzero.iam.infra.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * @author scp
 * @date 2020/12/17
 * @description 通过bean注入的方式覆盖hzero起停用方法
 */
@Component
public class C7nUserServiceImpl extends UserServiceImpl {

    private static final String PHONE_SUFFIX = "*#*";

    @Autowired
    @Lazy
    private OauthAdminFeignClient oauthAdminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;


    /**
     * 起停用用户复写hzero方法
     * 电话上加特殊字符
     *
     * @param userId
     * @param organizationId
     */
    @Override
    public void frozenUser(Long userId, Long organizationId) {
        User queryDTO = new User();
        queryDTO.setId(userId);
        queryDTO.setOrganizationId(organizationId);
        User user = userMapper.selectOne(queryDTO);
        Assert.notNull(user, "hiam.warn.user.notFound");
        if (BooleanUtils.isTrue(user.getEnabled())) {
            user.frozen();
            if (!StringUtils.isEmpty(user.getPhone())) {
                user.setPhone(user.getPhone() + PHONE_SUFFIX);
            }
            userRepository.updateOptional(user, User.FIELD_ENABLED, User.FIELD_PHONE);
            oauthAdminService.invalidByUsername(user.getLoginName());
        }
    }

    @Override
    public void unfrozenUser(Long userId, Long organizationId) {
        User queryDTO = new User();
        queryDTO.setId(userId);
        queryDTO.setOrganizationId(organizationId);
        User user = userMapper.selectOne(queryDTO);
        Assert.notNull(user, "hiam.warn.user.notFound");
        if (user.getEnabled() == null || BooleanUtils.isFalse(user.getEnabled())) {
            if (!StringUtils.isEmpty(user.getPhone())) {
                user.setPhone(user.getPhone().replace(PHONE_SUFFIX, ""));
            }
            user.unfrozen();
            userRepository.updateOptional(user, User.FIELD_ENABLED, User.FIELD_PHONE);
        }
    }
}
