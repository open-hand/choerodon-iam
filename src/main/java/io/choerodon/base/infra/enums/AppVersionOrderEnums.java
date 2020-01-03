package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/9/19 15:38
 */
public enum AppVersionOrderEnums {
    UPPUBLISHED(1),
    PUBLISHING(2),
    PUBLISHED(3);

    AppVersionOrderEnums(int value) {
        this.value = value;
    }

    private int value;

    public int value() {
        return value;
    }
}
