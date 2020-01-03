package io.choerodon.base.infra.enums;

/**
 * 市场应用状态
 *
 * @author Eugen
 */
public enum PublishAppVersionStatusEnum {
    /**
     * 未发布
     */
    UNPUBLISHED("unpublished"),
    /**
     * 已撤销
     */
    WITHDRAWN("withdrawn"),
    /**
     * 被驳回
     */
    REJECTED("rejected"),
    /**
     * 审批中
     */
    UNDER_APPROVAL("under_approval"),
    /**
     * 维护中
     */
    UNCONFIRMED("unconfirmed"),
    /**
     * 已发布
     */
    PUBLISHED("published");


    private final String value;

    PublishAppVersionStatusEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static boolean isInclude(String key) {
        boolean include = false;
        for (PublishAppVersionStatusEnum e : PublishAppVersionStatusEnum.values()) {
            if (e.value().equalsIgnoreCase(key)) {
                include = true;
                break;
            }
        }
        return include;
    }
}
