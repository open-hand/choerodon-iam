package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/9/10 16:30
 */
public enum AppVersionDisplayStatus {
    UPGRADE("upgrade"),
    UPDATE_FAILED("update_failed"),
    DOWNLOAD_FAILED("download_failed"),
    NOT_DOWNLOADED("not_downloaded"),
    NOT_PURCHASED("not_purchased"),
    DOWNLOADING("downloading"),
    COMPLETED("completed");


    AppVersionDisplayStatus(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}
