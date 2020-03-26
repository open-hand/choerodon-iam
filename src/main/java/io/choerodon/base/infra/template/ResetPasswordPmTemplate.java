package io.choerodon.base.infra.template;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;

/**
 * User: Mr.Wang
 * Date: 2019/12/25
 */
@Component
@NotifyBusinessType(code = "resetOrganizationUserPassword",
        name = "组织重置用户密码",
        description = "组织重置用户密码时发送通知",
        level = Level.ORGANIZATION,
        categoryCode = "org-management",
        pmEnabledFlag = true,
        emailEnabledFlag = true,
        isAllowConfig = false)
public class ResetPasswordPmTemplate implements PmTemplate {
    public static final String BUSINESS_TYPE_CODE = "resetOrganizationUserPassword";

    @Override
    public String code() {
        return "sendResetOrganizationUserPassword";
    }

    @Override
    public String name() {
        return "组织重置用户密码";
    }

    @Override
    public String businessTypeCode() {
        return BUSINESS_TYPE_CODE;
    }

    @Override
    public String title() {
        return "Choerodon通知";
    }

    @Override
    public String content() {
        return "<p>您好，您的登录密码已被管理员重置为默认密码：${password}</p>";
    }
}
