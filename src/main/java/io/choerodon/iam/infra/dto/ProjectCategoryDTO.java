package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.MultiLanguage;
import io.choerodon.mybatis.annotation.MultiLanguageField;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jiameng.cao
 * @since 2019/6/4
 */
@VersionAudit
@ModifyAudit
@MultiLanguage
@Table(name = "fd_project_category")
public class ProjectCategoryDTO extends AuditDomain {
    public static final String FIELD_SEQUENCE = "sequence";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Encrypt
    private Long id;
    @MultiLanguageField
    private String name;
    private String description;
    private String code;

    private Long organizationId;

    private Boolean displayFlag;

    private Boolean builtInFlag;

    private String labelCode;

    private Long sequence;

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public Boolean getDisplayFlag() {
        return displayFlag;
    }

    public void setDisplayFlag(Boolean displayFlag) {
        this.displayFlag = displayFlag;
    }

    public Boolean getBuiltInFlag() {
        return builtInFlag;
    }

    public void setBuiltInFlag(Boolean builtInFlag) {
        this.builtInFlag = builtInFlag;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getSequence() {
        return sequence;
    }

    public void setSequence(Long sequence) {
        this.sequence = sequence;
    }
}
