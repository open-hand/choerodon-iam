package io.choerodon.iam.infra.enums;

/**
 * @author Eugen
 **/
public enum ProjectCategoryEnum {
    AGILE("AGILE"),
    GENERAL("GENERAL"),
    OPERATIONS("OPERATIONS");
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
}
