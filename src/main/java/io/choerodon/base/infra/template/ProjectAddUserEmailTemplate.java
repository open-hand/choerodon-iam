package io.choerodon.base.infra.template;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;

/**
 * User: Mr.Wang
 * Date: 2020/2/12
 */
@Component
public class ProjectAddUserEmailTemplate implements DefaultEmailTemplate {
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
        return "Choerodon 角色添加通知";
    }

    @Override
    public String content() {
        String content;
        try {
            content = content("/templates/ProjectAddUserEmailTemplate.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
