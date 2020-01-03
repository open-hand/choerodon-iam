package io.choerodon.base.infra.enums;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/1
 */
public enum ApplicationVersionStatus {
    /**
     * 未生成发布信息
     */
    FRESH("fresh"),

    /*
     * 发布中
     */
    PUBLISHING("publishing"),

    /*
     * 已发布
     */
    RELEASED("released");

    private String value;

    ApplicationVersionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
