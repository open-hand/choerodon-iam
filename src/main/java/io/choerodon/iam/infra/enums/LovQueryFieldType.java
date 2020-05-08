package io.choerodon.iam.infra.enums;

/**
 * @author bgzyy
 * @since 2019/10/31
 */
public enum LovQueryFieldType {

    /**
     * 文本选择框
     */
    TEXT_INPUT("text_input"),
    /**
     * 数字选择框
     */
    NUM_INPUT("num_input"),
    /**
     * 时间选择框
     */
    TIME_INPUT("time_input"),
    /**
     * 日期选择框
     */
    DATE_INPUT("date_input"),
    /**
     * lookup 选择框
     */
    LOOKUP_INPUT("lookup_input"),
    /**
     * lov 选择框
     */
    LOV_INPUT("lov_input");

    LovQueryFieldType(String value) {
        this.value = value;
    }

    private String value;

    public String getValue() {
        return value;
    }
}