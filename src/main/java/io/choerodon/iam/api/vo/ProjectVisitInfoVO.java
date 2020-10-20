package io.choerodon.iam.api.vo;

import java.util.Date;

import io.choerodon.iam.infra.dto.ProjectDTO;

/**
 * @author lihao
 * 最近访问项目信息
 */
public class ProjectVisitInfoVO {
    private ProjectDTO projectDTO;
    private Long projectId;
    private Date lastVisitTime;


    public Long getProjectId() {
        return projectId;
    }

    public ProjectVisitInfoVO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
    }

    public Date getLastVisitTime() {
        return lastVisitTime;
    }

    public ProjectVisitInfoVO setLastVisitTime(Date lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
        return this;
    }

    public ProjectDTO getProjectDTO() {
        return projectDTO;
    }

    public ProjectVisitInfoVO setProjectDTO(ProjectDTO projectDTO) {
        this.projectDTO = projectDTO;
        return this;
    }
}
