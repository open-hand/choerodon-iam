package io.choerodon.iam.infra.enums;

public enum DashboardType {
    // 内置视图
    INTERNAL("INTERNAL"),
    // 客户自定义视图
    CUSTOMIZE("CUSTOMIZE");

    private String value;

    DashboardType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
