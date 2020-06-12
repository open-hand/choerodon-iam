package io.choerodon.iam.infra.enums;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 16:22
 */
public enum  QuickLinkShareScopeEnum {

    SELF("self"),
    PROJECT("project");
    private String value;

    public String value() {
        return value;
    }

    public static boolean contains(String code) {
        for (QuickLinkShareScopeEnum type : QuickLinkShareScopeEnum.values()) {
            if (type.value.equals(code)) {
                return true;
            }
        }
        return false;
    }

    QuickLinkShareScopeEnum(String value) {
        this.value = value;
    }

}
