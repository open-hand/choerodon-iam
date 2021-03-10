package io.choerodon.iam.api.vo;

import java.util.Set;

/**
 * @author superlee
 * @since 2021-03-04
 */
public class ProjectWithUserVO {

    private Long projectId;

    private Set<Long> userIds;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Set<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Long> userIds) {
        this.userIds = userIds;
    }
}
