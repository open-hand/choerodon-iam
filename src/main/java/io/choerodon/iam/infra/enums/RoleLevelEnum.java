package io.choerodon.iam.infra.enums;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/4/22 17:14
 */
public enum RoleLevelEnum {

    ORGANIZATION("organization"),

    PROJECT("project");

    private final String value;

    RoleLevelEnum(String value) {
        this.value = value;
    }
    public String value() {
        return this.value;
    }
}
