package io.choerodon.iam.infra.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: shanyu
 * @Description:
 * @Date: Created in 2021/10/13
 * @Modified By:
 */
public enum EnableFlagEnum {
    ENABLE(1, "启用"),
    DISABLE(0, "停用");

    private final Integer value;
    private final String status;

    EnableFlagEnum(Integer value, String status) {
        this.value = value;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public Integer getValue() {
        return value;
    }

    private static final Map<String, EnableFlagEnum> enumMap;

    static {
        enumMap = new HashMap<>();
        for (EnableFlagEnum value : EnableFlagEnum.values()) {
            enumMap.put(value.getValue().toString(), value);
        }
    }

    public static EnableFlagEnum forEnableFlag(Integer value) {
        return enumMap.get(value.toString());
    }
}