package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/8/28 15:49
 */
public enum RemoteTokenStatus {
    SUCCESS("success"),
    BREAK("break"),
    FAILED("failed");
    private final String value;
    RemoteTokenStatus(String value) {
        this.value = value;
    }
    public String value() {
        return value;
    }
}
