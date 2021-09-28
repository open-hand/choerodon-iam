package io.choerodon.iam.infra.enums;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/28
 * @Modified By:
 */
public enum UserWizardStepEnum {
    CREATE_PROJECT("createProject"),
    CREATE_USER("createUser"),
    OPEN_SPRINT("openSprint");
    private final String value;

    UserWizardStepEnum(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }
}
