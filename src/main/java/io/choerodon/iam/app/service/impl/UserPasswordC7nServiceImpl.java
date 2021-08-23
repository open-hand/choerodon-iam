package io.choerodon.iam.app.service.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.hzero.boot.oauth.domain.entity.BasePasswordHistory;
import org.hzero.boot.oauth.domain.entity.BasePasswordPolicy;
import org.hzero.boot.oauth.domain.entity.BaseUser;
import org.hzero.boot.oauth.domain.entity.BaseUserInfo;
import org.hzero.boot.oauth.domain.repository.BasePasswordHistoryRepository;
import org.hzero.boot.oauth.domain.repository.BasePasswordPolicyRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserInfoRepository;
import org.hzero.boot.oauth.domain.repository.BaseUserRepository;
import org.hzero.boot.oauth.domain.service.PasswordErrorTimesService;
import org.hzero.boot.oauth.policy.PasswordPolicyManager;
import org.hzero.boot.oauth.policy.PasswordPolicyMap;
import org.hzero.boot.oauth.policy.PasswordPolicyType;
import org.hzero.boot.oauth.strategy.PasswordStrategy;
import org.hzero.boot.oauth.strategy.PasswordStrategyStore;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.CheckStrength;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.iam.app.service.UserPasswordC7nService;

/**
 * @Author: scp
 * @Description: c7n 重置密码跳过 最近密码逻辑校验
 * 升级hzero版本需注意
 * @Date: Created in 2021/8/11
 * @Modified By:
 */
@Service
public class UserPasswordC7nServiceImpl implements UserPasswordC7nService {
    private static final String ERROR_EMPTY = "error.password.null";

    @Autowired
    private BaseUserRepository baseUserRepository;
    @Autowired
    private BasePasswordPolicyRepository basePasswordPolicyRepository;
    @Autowired
    private BaseUserInfoRepository baseUserInfoRepository;
    @Autowired
    private BasePasswordHistoryRepository basePasswordHistoryRepository;
    @Autowired
    private PasswordPolicyManager passwordPolicyManager;
    @Autowired
    private PasswordErrorTimesService passwordErrorTimesService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordStrategyStore passwordStrategyStore;


    @Override
    public void updateUserPassword(Long userId, String newPassword, boolean ldapUpdatable, boolean skipRecentPassword) {
        // 下面没用到手机号和邮箱，无需根据租户是否开启加密解密做对应处理
        BaseUser user = baseUserRepository.selectByPrimaryKey(userId);
        BaseUserInfo userInfo = baseUserInfoRepository.selectByPrimaryKey(userId);

        if (user == null || userInfo == null) {
            throw new CommonException("hoth.warn.password.userNotFound");
        }

        if (!ldapUpdatable) {
            checkLdapUser(user);
        }

        checkPasswordSame(user, newPassword);

        checkPasswordPolicy(user, newPassword, skipRecentPassword);

        recordHistoryPassword(user);

        updatePassword(user, newPassword);

        updateUserInfo(user, userInfo, newPassword);

        afterHandle(user);
    }

    /**
     * Ldap 用户不能更改密码
     */
    protected void checkLdapUser(BaseUser user) {
        if (user.getLdap() != null && user.getLdap()) {
            throw new CommonException("hoth.warn.password.ldapUserCantUpdatePassword");
        }
    }

    /**
     * 校验新密码不能与原密码相同
     */
    protected void checkPasswordSame(BaseUser user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new CommonException("hoth.warn.password.same");
        }
    }

    /**
     * 校验新密码是否符合密码策略
     */
    protected void checkPasswordPolicy(BaseUser user, String newPassword, Boolean skipRecentPassword) {
        passwordValidate(newPassword, user.getOrganizationId(), user, skipRecentPassword);
    }

    /**
     * 记录历史密码
     */
    protected void recordHistoryPassword(BaseUser user) {
        BasePasswordHistory passwordHistory = new BasePasswordHistory(user.getId(), user.getPassword()).setTenantId(user.getOrganizationId());
        basePasswordHistoryRepository.insertSelective(passwordHistory);
    }

    /**
     * 更新密码
     */
    protected void updatePassword(BaseUser user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setLastPasswordUpdatedAt(new Date());
        user.setLocked(false);
        user.setLockedUntilAt(null);
        baseUserRepository.updateOptional(user,
                BaseUser.FIELD_PASSWORD,
                BaseUser.FIELD_LAST_PASSWORD_UPDATED_AT,
                BaseUser.FIELD_LOCKED,
                BaseUser.FIELD_LOCKED_UNTIL_AT
        );
    }

    protected void updateUserInfo(BaseUser user, BaseUserInfo baseUserInfo, String newPassword) {
        baseUserInfo.setPasswordResetFlag(BaseConstants.Flag.YES);
        baseUserInfo.setSecurityLevelCode(CheckStrength.getPasswordLevel(newPassword).name());
        baseUserInfoRepository.updateOptional(baseUserInfo,
                BaseUserInfo.FIELD_PASSWORD_RESET_FLAG,
                BaseUserInfo.FIELD_SECURITY_LEVEL_CODE);
    }

    protected void afterHandle(BaseUser user) {
        user.setPassword(null);
        passwordErrorTimesService.clearErrorTimes(user.getLoginName());
    }

    public void passwordValidate(String password, Long tenantId, BaseUser baseUser, Boolean skipRecentPassword) {
        if (password == null) {
            throw new CommonException(ERROR_EMPTY);
        }
        BasePasswordPolicy passwordPolicy = basePasswordPolicyRepository.selectPasswordPolicy(tenantId);
        baseUser = Optional.ofNullable(baseUser).orElse(new BaseUser(null, null, tenantId));
        PasswordPolicyMap passwordPolicyMap = PasswordPolicyMap.parse(passwordPolicy);
        if (passwordPolicyMap.isEnablePassword()) {
            for (PasswordStrategy p : getPasswordProviders(passwordPolicyMap, passwordStrategyStore)) {
                if (skipRecentPassword != null && skipRecentPassword && p.getType().equals(PasswordPolicyType.NOT_RECENT.getValue())) {
                    continue;
                }
                p.validate(passwordPolicyMap, baseUser, password);
            }
        }
    }

    private List<PasswordStrategy> getPasswordProviders(PasswordPolicyMap policy, PasswordStrategyStore store) {
        LinkedList<PasswordStrategy> list = new LinkedList<>();
        for (String id : policy.getPasswordPolicies()) {
            PasswordStrategy provider = store.getProvider(id);
            if (provider != null) {
                list.add(provider);
            }
        }
        return list;
    }

}
