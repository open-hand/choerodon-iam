package io.choerodon.base.infra.enums;


/**
 * @author wkj
 * @since 2019/11/5
 **/
public enum Language {
    Chinese("zh_CN"),
    English("en_US");
    private String value;
    Language(String value) {
        this.value = value;
    }
    public String getValue() { return value; }

    public static boolean contains(String str) {
        for (Language language : Language.values()) {
            if (language.value.equals(str)) {
                return true;
            }
        }
        return false;
    }

}
