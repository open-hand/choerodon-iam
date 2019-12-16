package io.choerodon.base.infra.enums;

/**
 * @author scp
 * 用于区分ldap同步记录
 */
public enum LdapType {
    HAND("hand"),
    AUTO("auto");

    private String value;

    LdapType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
