package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author bgzyy
 * @since 2019/9/9
 */
@Table(name = "FD_LOV_QUERY_FIELD")
public class LovQueryFieldDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
    private String lovCode;
    private Boolean queryFieldDisplayFlag;
    private Boolean queryFieldRequiredFlag;
    private String queryFieldLabel;
    private String queryFieldName;
    private Double queryFieldWidth;
    private String queryFieldType;
    private String queryFieldDefault;
    private String queryFieldParamType;
    private String queryFieldLookupType;
    private String queryFieldLovCode;
    private Double queryFieldOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLovCode() {
        return lovCode;
    }

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
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

    public String getQueryFieldLookupType() {
        return queryFieldLookupType;
    }

    public void setQueryFieldLookupType(String queryFieldLookupType) {
        this.queryFieldLookupType = queryFieldLookupType;
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
