package io.choerodon.iam.infra.enums;

/**
 * @author zmf
 * @since 2020/5/22
 */
public enum TenantConfigEnum {
    ADDRESS("address"),
    BUSINESS_TYPE("businessType"),
    HOME_PAGE("homePage"),
    SCALE("scale"),
    USER_ID("userId"),
    CATEGORY("category"),
    IS_REGISTER("isRegister"),
    EMAIL_SUFFIX("emailSuffix"),
    REMOTE_TOKEN_ENABLED("remoteTokenEnabled");

    private final String value;

    TenantConfigEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}

