package io.choerodon.base.infra.enums;

public enum LdapAutoFrequencyType {
    DAY("day"),
    WEEK("week"),
    MONTH("month");

    private String value;

    LdapAutoFrequencyType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
