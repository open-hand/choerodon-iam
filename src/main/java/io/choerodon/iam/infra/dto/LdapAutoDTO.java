package io.choerodon.iam.infra.dto;


import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @author scp
 * @since 2019-11-21
 */
@VersionAudit
@ModifyAudit
@Table(name = "oauth_ldap_auto")
public class LdapAutoDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "主键/非必填")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "组织ID")
    @Encrypt
    private Long organizationId;

    @ApiModelProperty(value = "频率/必填")
    @NotNull(message = "error.ldap.auto.frequency.null")
    private String frequency;

    @ApiModelProperty(value = "开始时间/必填")
    @NotNull(message = "error.ldap.auto.start.time.null")
    private Date startTime;

    @ApiModelProperty(value = "定时任务Id")
    @Encrypt
    private Long quartzTaskId;

    @ApiModelProperty(value = "是否启用")
    private Boolean active;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getQuartzTaskId() {
        return quartzTaskId;
    }

    public void setQuartzTaskId(Long quartzTaskId) {
        this.quartzTaskId = quartzTaskId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
