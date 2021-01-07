package io.choerodon.iam.infra.enums;

/**
 * Created by wangxiang on 2021/1/7
 */
public enum ProjectOperatorTypeEnum {
    /**
     * 项目正在创建
     */
    CREATE("create"),
    /**
     * 项目正在修改
     */
    UPDATE("update");
    private String value;

    public String value() {
        return value;
    }

    public static boolean contains(String code) {
        for (ProjectOperatorTypeEnum type : ProjectOperatorTypeEnum.values()) {
            if (type.value.equals(code)) {
                return true;
            }
        }
        return false;
    }

    ProjectOperatorTypeEnum(String value) {
        this.value = value;
    }


}
