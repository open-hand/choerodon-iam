package io.choerodon.base.infra.template;

import static io.choerodon.base.infra.template.InviteUserTemplate.BUSINESS_TYPE_CODE;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;

/**
 * @author jiameng.cao
 * @date 2019/10/15
 */
@NotifyBusinessType(code = BUSINESS_TYPE_CODE, name = "邀请用户", level = Level.PROJECT,
        emailEnabledFlag = true,
        webhookOtherEnabledFlag = false, webhookJsonEnabledFlag = false,
        description = "发送邀请用户邮件，完善信息", isAllowConfig = false, isManualRetry = true, categoryCode = "pro-management")
@Component
public class InviteUserTemplate implements DefaultEmailTemplate {
    public static final String BUSINESS_TYPE_CODE = "inviteUser";

    @Override
    public String businessTypeCode() {
        return BUSINESS_TYPE_CODE;
    }

    @Override
    public String code() {
        return "sendInviteUser";
    }

    @Override
    public String name() {
        return "发送邀请用户邮件";
    }

    @Override
    public String title() {
        return "邀请成员";
    }

    /**
     * 邮件参数:registrant(注册人姓名)、reason（审批失败原因）
     */
    @Override
    public String content() {
        String content;
        try {
            content = content("/templates/InviteUser.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
