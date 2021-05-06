package io.choerodon.iam.infra.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/4/13
 * @Modified By:
 */
public enum MenuVisibilityEnum {
    /**
     * 标准
     */
    STANDARD(1L),
    /**
     * 高级
     */
    SENIOR(10L),
    /**
     * 开发
     */
    DEV(20L);

    private final Long level;

    MenuVisibilityEnum(Long level) {
        this.level = level;
    }

    public Long getLevel() {
        return level;
    }

}
