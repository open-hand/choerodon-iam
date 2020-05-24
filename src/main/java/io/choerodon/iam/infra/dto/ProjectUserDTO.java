package io.choerodon.iam.infra.dto;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author scp
 * @date 2020/4/16
 * @description
 */
@VersionAudit
@ModifyAudit
@Table(name = "fd_project_user")
public class ProjectUserDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ApiModelProperty("用户id")
    private Long memberId;
    @ApiModelProperty("项目id")
    private Long projectId;
    @ApiModelProperty("角色id")
    private Long roleId;
    @ApiModelProperty("用户角色关系id")
    private Long memberRoleId;

    public ProjectUserDTO() {
    }

    public ProjectUserDTO(Long memberId, Long projectId, Long roleId) {
        this.memberId = memberId;
        this.projectId = projectId;
        this.roleId = roleId;
    }

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMemberRoleId() {
        return memberRoleId;
    }

    public void setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
    }
}
