package io.choerodon.iam.infra.constant;

/**
 * @author superlee
 * @since 0.16.0
 */
public enum LdapSyncC7nType {

    /**
     * 同步用户
     */
    SYNC("sync"),

    /**
     * 禁用用户
     */
    DISABLE("disable");

    private String value;

    LdapSyncC7nType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
