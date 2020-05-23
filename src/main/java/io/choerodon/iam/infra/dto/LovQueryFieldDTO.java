package io.choerodon.iam.infra.dto;

import static io.choerodon.iam.infra.utils.RegularExpression.CODE_REGULAR_EXPRESSION;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * @author bgzyy
 * @since 2019/9/9
 */
@VersionAudit
@ModifyAudit
@Table(name = "FD_LOV_QUERY_FIELD")
public class LovQueryFieldDTO extends AuditDomain {
    @Id
    @GeneratedValue
    private Long id;
    @NotEmpty(message = "lov.query.code.empty")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.lov.code.format.incorrect")
    private String lovCode;
    @ApiModelProperty(value = "是否显示")
    private Boolean queryFieldDisplayFlag;
    @ApiModelProperty(value = "是否必输")
    private Boolean queryFieldRequiredFlag;
    @ApiModelProperty(value = "组件 label")
    private String queryFieldLabel;
    @ApiModelProperty(value = "1")
    @NotEmpty(message = "lov.query.name.empty")
    private String queryFieldName;
    @ApiModelProperty(value = "组件宽度")
    private Double queryFieldWidth;
    @ApiModelProperty(value = "组件类型")
    @NotEmpty(message = "lov.query.type.empty")
    private String queryFieldType;
    @ApiModelProperty(value = "组件默认值")
    private String queryFieldDefault;
    @ApiModelProperty(value = "参数类型")
    private String queryFieldParamType;
    @ApiModelProperty(value = "paramType 为 lookup 时该项必填")
    @NotEmpty(message = "lov.query.lookup.empty")
    private String queryFieldLookupCode;
    @ApiModelProperty(value = "paramType 为 lov 时该项必填")
    @NotEmpty(message = "lov.query.lov.empty")
    private String queryFieldLovCode;
    @ApiModelProperty(value = "组件排序号")
    private Double queryFieldOrder;

    public LovQueryFieldDTO() {
    }

    public LovQueryFieldDTO(@NotEmpty(message = "lov.query.code.empty") String lovCode) {
        this.lovCode = lovCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLovCode() {
        return lovCode;
    }

    public io.choerodon.iam.infra.dto.LovQueryFieldDTO setLovCode(String lovCode) {
        this.lovCode = lovCode;
        return this;
    }

    public Boolean getQueryFieldDisplayFlag() {
        return queryFieldDisplayFlag;
    }

    public void setQueryFieldDisplayFlag(Boolean queryFieldDisplayFlag) {
        this.queryFieldDisplayFlag = queryFieldDisplayFlag;
    }

    public Boolean getQueryFieldRequiredFlag() {
        return queryFieldRequiredFlag;
    }

    public void setQueryFieldRequiredFlag(Boolean queryFieldRequiredFlag) {
        this.queryFieldRequiredFlag = queryFieldRequiredFlag;
    }

    public String getQueryFieldLabel() {
        return queryFieldLabel;
    }

    public void setQueryFieldLabel(String queryFieldLabel) {
        this.queryFieldLabel = queryFieldLabel;
    }

    public String getQueryFieldName() {
        return queryFieldName;
    }

    public void setQueryFieldName(String queryFieldName) {
        this.queryFieldName = queryFieldName;
    }

    public String getQueryFieldType() {
        return queryFieldType;
    }

    public void setQueryFieldType(String queryFieldType) {
        this.queryFieldType = queryFieldType;
    }

    public String getQueryFieldLookupCode() {
        return queryFieldLookupCode;
    }

    public void setQueryFieldLookupCode(String queryFieldLookupCode) {
        this.queryFieldLookupCode = queryFieldLookupCode;
    }

    public String getQueryFieldLovCode() {
        return queryFieldLovCode;
    }

    public void setQueryFieldLovCode(String queryFieldLovCode) {
        this.queryFieldLovCode = queryFieldLovCode;
    }

    public Double getQueryFieldWidth() {
        return queryFieldWidth;
    }

    public void setQueryFieldWidth(Double queryFieldWidth) {
        this.queryFieldWidth = queryFieldWidth;
    }

    public Double getQueryFieldOrder() {
        return queryFieldOrder;
    }

    public void setQueryFieldOrder(Double queryFieldOrder) {
        this.queryFieldOrder = queryFieldOrder;
    }

    public String getQueryFieldDefault() {
        return queryFieldDefault;
    }

    public void setQueryFieldDefault(String queryFieldDefault) {
        this.queryFieldDefault = queryFieldDefault;
    }

    public String getQueryFieldParamType() {
        return queryFieldParamType;
    }

    public void setQueryFieldParamType(String queryFieldParamType) {
        this.queryFieldParamType = queryFieldParamType;
    }
}
