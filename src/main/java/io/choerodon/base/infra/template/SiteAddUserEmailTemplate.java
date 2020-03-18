package io.choerodon.base.infra.template;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * User: Mr.Wang
 * Date: 2020/2/11
 */
@Component
public class SiteAddUserEmailTemplate  implements DefaultEmailTemplate {
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
        String content;
        try {
            content = content("/templates/SiteAddUserEmailTemplate.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
