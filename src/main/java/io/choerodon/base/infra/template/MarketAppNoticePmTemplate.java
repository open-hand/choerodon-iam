package io.choerodon.base.infra.template;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;
import org.springframework.stereotype.Component;

import static io.choerodon.base.infra.template.MarketAppNoticePmTemplate.BUSINESS_TYPE_CODE;


/**
 * @author jiameng.cao
 * @date 2019/9/20
 */

@NotifyBusinessType(code = BUSINESS_TYPE_CODE, name = "应用市场通知", level = Level.PROJECT,
        description = "应用市场通知", isAllowConfig = false, isManualRetry = true)
@Component
public class MarketAppNoticePmTemplate implements PmTemplate {
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
