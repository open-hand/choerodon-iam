package io.choerodon.base.infra.utils;

public final class RegularExpression {

    private RegularExpression() {
    }

    /**
     * 字符串中只能由汉字、字母（大小写）、数字构成
     * 长度为1-30
     */
    public static final String CHINESE_AND_ALPHANUMERIC_30 = "^[A-Za-z0-9\\u4e00-\\u9fa5]{1,30}$";

    /**
     * 字符串中只能由汉字、字母（大小写）、数字、空格构成
     * 长度为1-30
     */
    public static final String CHINESE_AND_ALPHANUMERIC_AND_SPACE_30 = "^[A-Za-z0-9\\u4e00-\\u9fa5\\s]{1,30}$";

    /**
     * 字符串中只能由汉字、字母（大小写）、数字构成
     * 长度为1-50
     */
    public static final String CHINESE_AND_ALPHANUMERIC_50 = "^[A-Za-z0-9\\u4e00-\\u9fa5]{1,50}$";

    /**
     * 字符串中只能汉字、字母（大小写）、数字、"-"、"_"、"."构成
     * 长度为1-30
     */
    public static final String CHINESE_AND_ALPHANUMERIC_AND_SYMBOLS_30 = "^[A-Za-z0-9\\u4e00-\\u9fa5-_\\.]{1,30}$";

    /**
     * 字符串中只能汉字、字母（大小写）、数字、"-"、"_"、"."、空格构成
     * 长度为1-30
     */
    public static final String CHINESE_AND_ALPHANUMERIC_AND_SPACE_SYMBOLS_30 = "^[A-Za-z0-9\\u4e00-\\u9fa5-_\\.\\s]{1,30}$";

    /**
     * 字符串中只能由字母（大小写）、数字、"-"、"_"、"."构成
     * 长度为1-30
     */
    public static final String ALPHANUMERIC_AND_SYMBOLS = "^[a-zA-Z0-9-_\\.]{1,30}$";

    /**
     * 字符串中只能由字母（大小写）、数字、"-"、"_"、"."、空格构成
     * 长度为1-30
     */
    public static final String ALPHANUMERIC_AND_SPACE_SYMBOLS = "^[a-zA-Z0-9-_\\.\\s]{1,30}$";

    /**
     * 邮箱格式校验
     */
    public static final String EMAIL_COMMON = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 路由描述正则校验 任意字符 限制200长度
     */
    public static final String ROUTE_RULE_ALL_SYMBOLS_200 = "^.{0,200}$";

    /**
     * code 校验
     */
    public static final String CODE_REGULAR_EXPRESSION = "^[a-zA-Z][a-zA-Z0-9-_.]*$";
}
