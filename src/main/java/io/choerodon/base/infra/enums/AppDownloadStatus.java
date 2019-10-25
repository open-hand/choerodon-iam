package io.choerodon.base.infra.enums;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/22
 */
public enum AppDownloadStatus {

    DOWNLOADING("downloading"),
    COMPLETED("completed"),
    FAILED("failed");

    AppDownloadStatus(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
