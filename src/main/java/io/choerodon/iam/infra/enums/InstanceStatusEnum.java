package io.choerodon.iam.infra.enums;

/**
 * Created by wangxiang on 2021/1/20
 */
public enum InstanceStatusEnum {
    RUNNING("RUNNING"),
    FAILED("FAILED"),
    COMPLETED("COMPLETED");

    private String value;

    InstanceStatusEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
