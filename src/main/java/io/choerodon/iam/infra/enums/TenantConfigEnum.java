package io.choerodon.iam.infra.enums;

import java.util.HashMap;

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
    IMAGE_URL("imageUrl"),
    REMOTE_TOKEN_ENABLED("remoteTokenEnabled"),
    USER_WIZARD("userWizard");

    private final String value;

    TenantConfigEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    private static HashMap<String, TenantConfigEnum> valuesMap = new HashMap<>(10);

    static {
        TenantConfigEnum[] var0 = values();

        for (TenantConfigEnum accessLevel : var0) {
            valuesMap.put(accessLevel.value, accessLevel);
        }

    }



    /**
     * 根据string类型返回枚举类型
     *
     * @param value String
     */
    public static TenantConfigEnum forValue(String value) {
        return valuesMap.get(value);
    }

}

