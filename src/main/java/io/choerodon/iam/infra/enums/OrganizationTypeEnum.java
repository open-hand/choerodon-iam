package io.choerodon.iam.infra.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/29
 * @Modified By:
 */
public enum OrganizationTypeEnum {
    PLATFORM("platform"),
    SAAS("saas"),
    REGISTER("register");

    private final String value;

    OrganizationTypeEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
