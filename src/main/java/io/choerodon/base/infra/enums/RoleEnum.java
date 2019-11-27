package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/11/19 15:08
 */
public enum RoleEnum {
    ORG_ADMINISTRATOR("role/organization/default/administrator"),
    ORG_MEMBER("role/organization/default/organization-member"),
    PROJECT_MEMBER("role/project/default/project-member"),
    PROJECT_OWNER("role/project/default/project-owner");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
