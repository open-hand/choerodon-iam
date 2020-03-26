package io.choerodon.base.infra.template;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;

/**
 * User: Mr.Wang
 * Date: 2020/2/11
 */
@Component
@NotifyBusinessType(code = "siteAddRoot",
        name = "管理员添加Root用户",
        description = "平台层添加Root用户通知",
        level = Level.SITE,
        categoryCode = "add-or-import-user",
        webhookOtherEnabledFlag = false, webhookJsonEnabledFlag = false,
        pmEnabledFlag = true,
        emailEnabledFlag = true,
        isAllowConfig = false)
public class SiteAddRootPmTemplate implements PmTemplate {
    @Override
    public String code() {
        return "sendSiteAddRoot";
    }

    @Override
    public String name() {
        return "管理员添加Root用户";
    }

    @Override
    public String businessTypeCode() {
        return "siteAddRoot";
    }

    @Override
    public String title() {
        return "角色添加通知";
    }

    @Override
    public String content() {
        return "<p>您已被添加为Choerodon平台的【Root用户】。</p>";
    }
}
