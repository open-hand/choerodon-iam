package io.choerodon.iam.api.vo.agile;


import java.util.Set;

/**
 * @author superlee
 * @since 2020-10-14
 */
public class AgileUserVO {

    private Set<Long> userIds;

    private Set<Long> projectIds;

    private String param;

    private Long organizationId;

    private Set<Long> ignoredUserIds;

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }

    public Set<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Long> projectIds) {
        this.projectIds = projectIds;
    }

    public Set<Long> getIgnoredUserIds() {
        return ignoredUserIds;
    }

    public void setIgnoredUserIds(Set<Long> ignoredUserIds) {
        this.ignoredUserIds = ignoredUserIds;
    }
}
