package io.choerodon.iam.app.service.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.hzero.iam.app.service.impl.UserServiceImpl;
import org.hzero.iam.domain.entity.User;
import org.hzero.iam.domain.repository.UserRepository;
import org.hzero.iam.infra.feign.OauthAdminFeignClient;
import org.hzero.iam.infra.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import io.choerodon.iam.app.service.MessageSendService;

/**
 * @author scp
 * @date 2020/12/17
 * @description 通过bean注入的方式覆盖hzero起停用方法
 */
@Component
public class C7nUserServiceImpl extends UserServiceImpl {

    private static final String PHONE_SUFFIX = "*#*";
    private static final Logger LOGGER = LoggerFactory.getLogger(C7nUserServiceImpl.class);


    @Autowired
    @Lazy
    private OauthAdminFeignClient oauthAdminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MessageSendService messageSendService;


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
        if (user == null) {
            LOGGER.warn("hiam.warn.user.notFound:{}", userId);
            return;
        }
        if (BooleanUtils.isTrue(user.getEnabled())) {
            user.frozen();
            if (!StringUtils.isEmpty(user.getPhone())) {
                user.setPhone(user.getPhone() + PHONE_SUFFIX);
            }
            userRepository.updateOptional(user, User.FIELD_ENABLED, User.FIELD_PHONE);
            oauthAdminService.invalidByUsername(user.getOrganizationId(), user.getLoginName());
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

    /**
     * 重写hzero创建用户的方法，仅处理平台层
     *
     * @param user
     * @return
     */
//    @Override
//    public User createUser(User user) {
//        User dbUser = super.createUser(user);
//        //发送邮件通知，用户创建成功
//        if (user.getOrganizationId() == BaseConstants.DEFAULT_TENANT_ID){
//            messageSendService.sendSiteCreateUser(dbUser);
//        }
//        return dbUser;
//
//    }
}
