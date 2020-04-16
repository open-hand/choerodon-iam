package io.choerodon.iam.infra.dto;

import javax.persistence.Table;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author scp
 * @date 2020/4/16
 * @description
 */
@Table(name = "fd_project_user")
public class ProjectUserDTO extends AuditDomain {
    private Long id;
    private Long memberId;
    private Long projectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
