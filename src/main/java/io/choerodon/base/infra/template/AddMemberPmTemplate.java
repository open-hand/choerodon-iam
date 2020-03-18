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
@NotifyBusinessType(code = "addMember",
        name = "管理员添加成员",
        description = "管理员添加或导入新用户时，成员收到添加用户结果的反馈",
        level = Level.ORGANIZATION,
        categoryCode = "org-management",
        pmEnabledFlag = true,
        emailEnabledFlag = true,
        isAllowConfig = false)
public class AddMemberPmTemplate implements PmTemplate {
    public static final String BUSINESS_TYPE_CODE = "addMember";

    @Override
    public String code() {
        return "sendAddMember";
    }

    @Override
    public String name() {
        return "发送添加成员通知";
    }

    @Override
    public String businessTypeCode() {
        return BUSINESS_TYPE_CODE;
    }

    @Override
    public String title() {
        return "角色添加通知";
    }

    @Override
    public String content() {
        return "<p>您已被添加为组织【${organizationName}】中【${roleName}】的角色。</p>";
    }
}
