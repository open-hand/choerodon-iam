package io.choerodon.base.infra.template;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;


/**
 * User: Mr.Wang
 * Date: 2019/12/25
 */
@Component
public class RsetPasswordEmailTemplate implements DefaultEmailTemplate{
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
        return "resetOrganizationUserPassword";
    }

    @Override
    public String title() {
        return "组织重置用户密码";
    }

    @Override
    public String content() {
        String content;
        try {
            content = content("/templates/ResetPasswordEmailTemplate.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
