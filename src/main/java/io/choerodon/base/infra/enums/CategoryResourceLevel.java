package io.choerodon.base.infra.enums;

/**
 * @author jiameng.cao
 * @date 2019/6/4
 */
public enum CategoryResourceLevel {
    ORGANIZATION("organization"),
    ORGANIZATION_PROJECT("organization_project"),
    PROJECT("project");

    private final String value;

    CategoryResourceLevel(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
