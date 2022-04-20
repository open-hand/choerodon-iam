package io.choerodon.iam.infra.dto;

import java.util.Date;
import java.util.Set;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.*;

/**
 * @author scp
 * @since 2020/4/16
 *
 */
@VersionAudit
@ModifyAudit
@Table(name = "fd_project_permission")
public class ProjectPermissionDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Encrypt
    private Long id;

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("用户角色关系id")
    @Encrypt
    private Long memberRoleId;

    @Transient
    @ApiModelProperty("用户id")
    @Encrypt
    private Long memberId;

    @Transient
    @ApiModelProperty("角色id")
    @Encrypt
    private Long roleId;

    @Encrypt
    @Transient
    Set<Long> roleIds;

    @ApiModelProperty("进场时间")
    @Transient
    private Date scheduleEntryTime;

    @ApiModelProperty("离场时间")
    @Transient
    private Date scheduleExitTime;

    public ProjectPermissionDTO() {
    }

    public ProjectPermissionDTO(Long memberId, Long projectId, Long roleId) {
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

    public ProjectPermissionDTO setMemberRoleId(Long memberRoleId) {
        this.memberRoleId = memberRoleId;
        return this;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Date getScheduleEntryTime() {
        return scheduleEntryTime;
    }

    public void setScheduleEntryTime(Date scheduleEntryTime) {
        this.scheduleEntryTime = scheduleEntryTime;
    }

    public Date getScheduleExitTime() {
        return scheduleExitTime;
    }

    public void setScheduleExitTime(Date scheduleExitTime) {
        this.scheduleExitTime = scheduleExitTime;
    }
}
