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
     * 平台层菜单标签
     */
    SITE_MENU("SITE_MENU"),
    /**
     * 组织层菜单标签
     */
    TENANT_MENU("TENANT_MENU"),
    /**
     * 组织层通用菜单标签
     */
    TENANT_GENERAL("TENANT_GENERAL"),
    /**
     * 个人信息菜单标签
     */
    USER_MENU("USER_MENU"),
    /**
     * 项目层全流程项目标签
     */
    GENERAL_MENU("GENERAL_MENU"),
    /**
     * 运维项目标签
     */
    OPERATIONS_MENU("OPERATIONS_MENU"),
    /**
     * 项目群项目标签
     */
    PROGRAM_MENU("PROGRAM_MENU"),

    /**
     * 项目群子项目项目标签
     */
    PROGRAM_PROJECT_MENU("PROGRAM_PROJECT_MENU"),
    /**
     * 项目层全流程项目标签
     */
    AGILE_MENU("AGILE_MENU"),

    /*-----------------------------新标签--------------------------------*/

    /**
     * 标记所有项目都有的菜单
     */
    N_GENERAL_PROJECT_MENU("N_GENERAL_PROJECT_MENU"),
    N_AGILE_MENU("N_AGILE_MENU"),
    N_REQUIREMENT_MENU("N_REQUIREMENT_MENU"),
    N_PROGRAM_PROJECT_MENU("N_PROGRAM_PROJECT_MENU"),
    N_PROGRAM_MENU("N_PROGRAM_MENU"),
    N_TEST_MENU("N_TEST_MENU"),
    N_DEVOPS_MENU("N_DEVOPS_MENU"),
    N_WATERFALL_MENU("N_WATERFALL_MENU"),
    N_WATERFALL_AGILE_MENU("N_WATERFALL_AGILE_MENU"),
    N_OPERATIONS_MENU("N_OPERATIONS_MENU");
    private final String value;

    MenuLabelEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
