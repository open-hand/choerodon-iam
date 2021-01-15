package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author bgzyy
 * @since 2019/9/11
 */
@VersionAudit
@ModifyAudit
@Table(name = "FD_REPORT")
public class ReportDTO extends AuditDomain {

    @Id
    @GeneratedValue
    private Long id;
    private String reportType;
    private String icon;
    private String title;
    private String description;
    private String path;
    private Long sort;
    private Long typeSequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }

    public Long getTypeSequence() {
        return typeSequence;
    }

    public void setTypeSequence(Long typeSequence) {
        this.typeSequence = typeSequence;
    }
}