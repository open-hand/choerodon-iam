package io.choerodon.iam.infra.enums;

/**
 * @author superlee
 */
public enum RoleLabel {

    PROJECT_DEPLOY_ADMIN("project.deploy.admin"),

    PROJECT_OWNER("project.owner"),

    ORGANIZATION_OWNER("organization.owner"),

    ORGANIZATION_GITLAB_OWNER("organization.gitlab.owner");

    private final String value;

    RoleLabel(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
