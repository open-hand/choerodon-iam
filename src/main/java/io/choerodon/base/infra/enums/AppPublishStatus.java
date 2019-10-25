package io.choerodon.base.infra.enums;

/**
 * 市场应用发布状态
 *
 * @author PENGYUHUA
 * @date 2019/08/21
 */
public enum AppPublishStatus {
    // 待处理
    PENDING("pending"),
    // 成功
    SUCCESS("success"),
    // 失败
    FAILURE("failure");

    private final String value;

    AppPublishStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

}
