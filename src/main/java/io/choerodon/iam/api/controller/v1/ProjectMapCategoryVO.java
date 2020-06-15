package io.choerodon.iam.api.controller.v1;



import io.choerodon.iam.infra.dto.ProjectCategoryDTO;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

@VersionAudit
@ModifyAudit
public class ProjectMapCategoryVO extends AuditDomain {

    private Long id;

    private Long projectId;

    private Long categoryId;

    private ProjectCategoryDTO projectCategoryDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public ProjectCategoryDTO getProjectCategoryDTO() {
        return projectCategoryDTO;
    }

    public void setProjectCategoryDTO(ProjectCategoryDTO projectCategoryDTO) {
        this.projectCategoryDTO = projectCategoryDTO;
    }
}
