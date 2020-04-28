package io.choerodon.iam.infra.enums;

/**
 * @author superlee
 */
public enum RoleLabelEnum {

    PROJECT_OWNER("project.owner"),

    PROJECT_ROLE("project_role"),

    PROJECT_GITLAB_OWNER("project.gitlab.owner"),

    ORGANIZATION_OWNER("organization.owner"),

    ORGANIZATION_GITLAB_OWNER("organization.gitlab.owner");

    private final String value;

    RoleLabelEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
