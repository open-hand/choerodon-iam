package io.choerodon.base.infra.enums;

/**
 * @author wanghao
 * @Date 2019/9/12 15:09
 */
public enum AppVersionStatusEnum {
    /**
     * 未发布
     */
    UNPUBLISHED("unpublished"),
    /**
     * 发布中
     */
    PUBLISHING("publishing"),
    /**
     * 已发布
     */
    PUBLISHED("published");
    AppVersionStatusEnum(String value) {
        this.value = value;
    }

    private String value;

    public String value() {
        return value;
    }
}
