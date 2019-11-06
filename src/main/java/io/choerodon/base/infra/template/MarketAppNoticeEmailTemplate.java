package io.choerodon.base.infra.template;

import io.choerodon.core.notify.EmailTemplate;
import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import org.springframework.stereotype.Component;

import static io.choerodon.base.infra.template.MarketAppNoticeEmailTemplate.BUSINESS_TYPE_CODE;


@NotifyBusinessType(code = BUSINESS_TYPE_CODE, name = "base应用市场通知", level = Level.PROJECT,
        description = "base应用市场通知", isAllowConfig = false, isManualRetry = true,categoryCode = "app-market-notice")
@Component
public class MarketAppNoticeEmailTemplate implements EmailTemplate {
    public static final String BUSINESS_TYPE_CODE = "marketApplicationNotice-base";

    @Override
    public String businessTypeCode() {
        return BUSINESS_TYPE_CODE;
    }

    @Override
    public String code() {
        return "marketAppNoticeBase";
    }

    @Override
    public String name() {
        return "应用市场通知";
    }

    @Override
    public String title() {
        return "应用市场通知";
    }

    /**
     * 邮件参数:title(标题)、message（信息）
     */
    @Override
    public String content() {
        return " <p>${title}</p><p>${message}</p>";
    }
}
