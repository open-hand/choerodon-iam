package io.choerodon.iam.infra.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
public enum UserWizardStatusEnum {
    UNCOMPLETED("uncompleted"),
    COMPLETED("completed");
    private final String value;

    UserWizardStatusEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
