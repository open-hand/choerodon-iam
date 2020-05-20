package io.choerodon.iam.infra.enums;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/20 14:41
 */
public enum MenuLabelEnum {
    /**
     * 组织层菜单标签
     */
    TENANT_MENU("TENANT_MENU"),

    /**
     * 项目层全流程项目标签
     */
    GENERAL_MENU("GENERAL_MENU");
    private final String value;

    MenuLabelEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
