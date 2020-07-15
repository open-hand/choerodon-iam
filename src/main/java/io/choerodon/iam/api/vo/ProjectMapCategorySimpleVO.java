package io.choerodon.iam.api.vo;

import org.hzero.starter.keyencrypt.core.Encrypt;

public class ProjectMapCategorySimpleVO {
    private Long projectId;
    private String category;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
