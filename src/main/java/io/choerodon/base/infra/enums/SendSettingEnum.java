package io.choerodon.base.infra.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Mr.Wang
 * Date: 2020/3/23
 */
public enum SendSettingEnum {
    DISABLE_ORGANIZATION("disableOrganization"),
    ENABLE_ORGANIZATION("enableOrganization"),
    ADD_MEMBER("addMember"),
    STOP_USER("stopUser"),
    CREATE_PROJECT("createProject"),
    ENABLE_PROJECT("enableProject"),
    DISABLE_PROJECT("disableProject"),
    PROJECT_ADDUSER("projectAddUser"),
    DELETE_USERROLES("deleteUserRoles");


    private String sendSettingCode;

    SendSettingEnum(String sendSettingCode) {
        this.sendSettingCode = sendSettingCode;
    }

    public String value() {
        return this.sendSettingCode;
    }

    public static Map<String, String> map = new HashMap<>();

    static {
        map.put(SendSettingEnum.DISABLE_ORGANIZATION.value(), "停用组织");
        map.put(SendSettingEnum.ENABLE_ORGANIZATION.value(), "启用组织");
        map.put(SendSettingEnum.ADD_MEMBER.value(), "管理员添加成员(组织层)");
        map.put(SendSettingEnum.STOP_USER.value(), "停用用户");
        map.put(SendSettingEnum.CREATE_PROJECT.value(), "创建项目");
        map.put(SendSettingEnum.ENABLE_PROJECT.value(), "启用项目");
        map.put(SendSettingEnum.DISABLE_PROJECT.value(), "禁用项目");
        map.put(SendSettingEnum.PROJECT_ADDUSER.value(), "管理员添加/导入组织成员（项目层）");
        map.put(SendSettingEnum.DISABLE_PROJECT.value(),"删除用户所有项目角色");
    }
}