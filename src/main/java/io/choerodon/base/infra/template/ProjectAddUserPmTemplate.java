package io.choerodon.base.infra.template;

import org.springframework.stereotype.Component;

import io.choerodon.core.notify.Level;
import io.choerodon.core.notify.NotifyBusinessType;
import io.choerodon.core.notify.PmTemplate;

/**
 * User: Mr.Wang
 * Date: 2020/2/12
 */
@Component
@NotifyBusinessType(code = "projectAddUser",
        name = "管理员添加/导入成员",
        description = "项目层添加或者导入用户分角色的通知",
        level = Level.PROJECT,
        categoryCode = "add-or-import-user",
        pmEnabledFlag = true,
        emailEnabledFlag = true,
        isAllowConfig = false)
public class ProjectAddUserPmTemplate implements PmTemplate {
    @Override
    public String code() {
        return "sendProjectAddUser";
    }

    @Override
    public String name() {
        return "管理员添加/导入成员";
    }

    @Override
    public String businessTypeCode() {
        return "projectAddUser";
    }

    @Override
    public String title() {
        return "角色添加通知";
    }

    @Override
    public String content() {
        return "<p>您已被添加为项目【${projectName}】中【${roleName}】的角色。</p>";
    }
}
