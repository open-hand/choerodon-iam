package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/8/23 16:36
 */
public enum DownloadOrder {

    NOT_PURCHASE(3),
    NOT_DOWNLOADED(4),
    DOWNLOADING(2),
    COMPLETED(1),
    FAILED(5),
    UPDATE_FAILED(6),
    UPGRADE(7);

    DownloadOrder(int value) {
        this.value = value;
    }

    private int value;

    public int getValue() {
        return value;
    }

}
