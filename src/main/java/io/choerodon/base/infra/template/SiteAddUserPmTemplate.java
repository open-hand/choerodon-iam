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
@NotifyBusinessType(code = "siteAddUser",
        name = "管理员添加/导入用户",
        description = "平台层添加或者导入用户分角色的通知",
        level = Level.SITE,
        categoryCode = "add-or-import-user",
        pmEnabledFlag = true,
        emailEnabledFlag = true,
        isAllowConfig = false)
public class SiteAddUserPmTemplate implements PmTemplate {
    @Override
    public String code() {
        return "snedSiteAddUser";
    }

    @Override
    public String name() {
        return "管理员添加/导入用户";
    }

    @Override
    public String businessTypeCode() {
        return "siteAddUser";
    }

    @Override
    public String title() {
        return "Choerodon角色添加通知";
    }

    @Override
    public String content() {
        return "<p>您已被添加为Choerodon【${roleName}】的角色。</p>";
    }
}
