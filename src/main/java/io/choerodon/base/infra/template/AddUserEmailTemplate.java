package io.choerodon.base.infra.template;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;


/**
 * User: Mr.Wang
 * Date: 2019/12/25
 */
@Component
public class AddUserEmailTemplate implements DefaultEmailTemplate{
    @Override
    public String code() {
        return "sendAddUser";
    }

    @Override
    public String name() {
        return "管理员添加/导入成员";
    }

    @Override
    public String businessTypeCode() {
        return "addUser";
    }

    @Override
    public String title() {
        return "添加新用户";
    }

    @Override
    public String content() {
        String content;
        try {
            content = content("/templates/AddUserEmailTemplate.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
