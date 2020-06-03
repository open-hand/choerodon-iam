package io.choerodon.iam.infra.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mr.Wang
 * Date: 2020/3/23
 */
public enum SendSettingBaseEnum {
    DISABLE_ORGANIZATION("DISABLEORGANIZATION"),
    ENABLE_ORGANIZATION("ENABLEORGANIZATION"),
    ADD_MEMBER("ADDMEMBER"),
    STOP_USER("STOPUSER"),
    CREATE_PROJECT("CREATEPROJECT"),
    ENABLE_PROJECT("ENABLEPROJECT"),
    DISABLE_PROJECT("DISABLEPROJECT"),
    PROJECT_ADD_USER("PROJECTADDUSER"),
    DELETE_USER_ROLES("DELETEUSERROLES");


    private String sendSettingCode;

    SendSettingBaseEnum(String sendSettingCode) {
        this.sendSettingCode = sendSettingCode;
    }

    public String value() {
        return this.sendSettingCode;
    }

    public static Map<String, String> map = new HashMap<>();

    static {
        map.put(SendSettingBaseEnum.DISABLE_ORGANIZATION.value(), "停用组织");
        map.put(SendSettingBaseEnum.ENABLE_ORGANIZATION.value(), "启用组织");
        map.put(SendSettingBaseEnum.ADD_MEMBER.value(), "管理员添加成员(组织层)");
        map.put(SendSettingBaseEnum.STOP_USER.value(), "停用用户");
        map.put(SendSettingBaseEnum.CREATE_PROJECT.value(), "创建项目");
        map.put(SendSettingBaseEnum.ENABLE_PROJECT.value(), "启用项目");
        map.put(SendSettingBaseEnum.DISABLE_PROJECT.value(), "禁用项目");
        map.put(SendSettingBaseEnum.PROJECT_ADD_USER.value(), "管理员添加/导入组织成员（项目层）");
        map.put(SendSettingBaseEnum.DELETE_USER_ROLES.value(), "删除用户所有项目角色");
    }
}