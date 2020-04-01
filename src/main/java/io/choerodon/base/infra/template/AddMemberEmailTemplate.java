package io.choerodon.base.infra.template;

import java.io.IOException;

import org.springframework.stereotype.Component;

import io.choerodon.core.exception.CommonException;


/**
 * User: Mr.Wang
 * Date: 2019/12/25
 */
@Component
public class AddMemberEmailTemplate implements DefaultEmailTemplate{
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
        return "addMember";
    }

    @Override
    public String title() {
        return "角色添加通知";
    }

    @Override
    public String content() {
        String content;
        try {
            content = content("/templates/AddMemberEmailTemplate.html");
        } catch (IOException e) {
            throw new CommonException(e);
        }
        return content;
    }
}
