package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author bgzyy
 * @since 2019/9/9
 */
@Table(name = "FD_LOV")
public class LovDTO extends BaseDTO {

    @Id
    @GeneratedValue
    private Long id;
    private String code;
    private String description;
    private String resourceLevel;
    private String permissionCode;
    private String valueField;
    private String textField;
    private String title;
    private Double width;
    private Double height;
    private String placeholder;
    private Boolean delayLoadFlag;
    private Boolean editFlag;
    private Boolean treeFlag;
    private String idField;
    private String parentField;

    @Transient
    private String url;
    @Transient
    private String method;
    @Transient
    private List<LovQueryFieldDTO> queryFields;
    @Transient
    private List<LovGridFieldDTO> gridFields;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<LovQueryFieldDTO> getQueryFields() {
        return queryFields;
    }

    public void setQueryFields(List<LovQueryFieldDTO> queryFields) {
        this.queryFields = queryFields;
    }

    public List<LovGridFieldDTO> getGridFields() {
        return gridFields;
    }

    public void setGridFields(List<LovGridFieldDTO> gridFields) {
        this.gridFields = gridFields;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResourceLevel() {
        return resourceLevel;
    }

    public void setResourceLevel(String resourceLevel) {
        this.resourceLevel = resourceLevel;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getValueField() {
        return valueField;
    }

    public void setValueField(String valueField) {
        this.valueField = valueField;
    }

    public String getTextField() {
        return textField;
    }

    public void setTextField(String textField) {
        this.textField = textField;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public Boolean getDelayLoadFlag() {
        return delayLoadFlag;
    }

    public void setDelayLoadFlag(Boolean delayLoadFlag) {
        this.delayLoadFlag = delayLoadFlag;
    }

    public Boolean getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(Boolean editFlag) {
        this.editFlag = editFlag;
    }

    public Boolean getTreeFlag() {
        return treeFlag;
    }

    public void setTreeFlag(Boolean treeFlag) {
        this.treeFlag = treeFlag;
    }

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getParentField() {
        return parentField;
    }

    public void setParentField(String parentField) {
        this.parentField = parentField;
    }
}