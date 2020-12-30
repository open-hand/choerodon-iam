package io.choerodon.iam.infra.enums;

import java.util.HashMap;

/**
 * @author Eugen
 **/
public enum ProjectCategoryEnum {
    AGILE("AGILE"),
    GENERAL("GENERAL"),
    OPERATIONS("OPERATIONS"),
    /**
     * 模块解耦后的新项目类型
     */
    N_AGILE("N_AGILE"), //敏捷管理
    N_REQUIREMENT("N_REQUIREMENT"), //需求管理
    N_DEVOPS("N_DEVOPS"), //devops流程
    N_OPERATIONS("N_OPERATIONS"), //运维项目
    N_TEST("N_TEST"); //测试管理


    private String value;

    public String value() {
        return value;
    }

    public static boolean contains(String code) {
        for (ProjectCategoryEnum category : ProjectCategoryEnum.values()) {
            if (category.value.equals(code)) {
                return true;
            }
        }
        return false;
    }

    ProjectCategoryEnum(String value) {
        this.value = value;
    }

    private static HashMap<String, ProjectCategoryEnum> valuesMap = new HashMap<>(3);

    static {
        ProjectCategoryEnum[] var0 = values();

        for (ProjectCategoryEnum accessLevel : var0) {
            valuesMap.put(accessLevel.value, accessLevel);
        }

    }


    /**
     * 根据string类型返回枚举类型
     *
     * @param value String
     */
    public static ProjectCategoryEnum forValue(String value) {
        return valuesMap.get(value);
    }
}
