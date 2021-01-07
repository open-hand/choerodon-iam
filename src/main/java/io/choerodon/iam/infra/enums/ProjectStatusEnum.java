package io.choerodon.iam.infra.enums;

/**
 * Created by wangxiang on 2021/1/7
 */
public enum ProjectStatusEnum {
    /**
     * 项目状态失败
     */
    FAILED("failed"),
    /**
     * 项目正在创建
     */
    CREATING("creating"),
    /**
     * 项目正在修改
     */
    UPDATING("updating");

    private String value;

    public String value() {
        return value;
    }

    public static boolean contains(String code) {
        for (ProjectStatusEnum type : ProjectStatusEnum.values()) {
            if (type.value.equals(code)) {
                return true;
            }
        }
        return false;
    }

    ProjectStatusEnum(String value) {
        this.value = value;
    }

}
