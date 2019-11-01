package io.choerodon.base.infra.dto;

import javax.persistence.*;
import javax.validation.constraints.*;

import io.choerodon.mybatis.entity.*;

/**
 * @author bgzyy
 * @since 2019/9/9
 */
@Table(name = "FD_LOV_GRID_FIELD")
public class LovGridFieldDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
    @NotEmpty(message = "lov.grid.code.empty")
    private String lovCode;
    private Boolean gridFieldDisplayFlag;
    @NotEmpty(message = "lov.grid.label.empty")
    private String gridFieldLabel;
    @NotEmpty(message = "lov.grid.name.empty")
    private String gridFieldName;
    @NotEmpty(message = "lov.grid.order.empty")
    private Double gridFieldOrder;
    private String gridFieldAlign;
    private Double gridFieldWidth;
    private Boolean gridFieldQueryFlag;

    public LovGridFieldDTO() {
    }

    public LovGridFieldDTO(@NotEmpty(message = "lov.grid.code.empty") String lovCode) {
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

    public void setLovCode(String lovCode) {
        this.lovCode = lovCode;
    }

    public Boolean getGridFieldDisplayFlag() {
        return gridFieldDisplayFlag;
    }

    public void setGridFieldDisplayFlag(Boolean gridFieldDisplayFlag) {
        this.gridFieldDisplayFlag = gridFieldDisplayFlag;
    }

    public String getGridFieldLabel() {
        return gridFieldLabel;
    }

    public void setGridFieldLabel(String gridFieldLabel) {
        this.gridFieldLabel = gridFieldLabel;
    }

    public String getGridFieldName() {
        return gridFieldName;
    }

    public void setGridFieldName(String gridFieldName) {
        this.gridFieldName = gridFieldName;
    }

    public String getGridFieldAlign() {
        return gridFieldAlign;
    }

    public void setGridFieldAlign(String gridFieldAlign) {
        this.gridFieldAlign = gridFieldAlign;
    }

    public Double getGridFieldOrder() {
        return gridFieldOrder;
    }

    public void setGridFieldOrder(Double gridFieldOrder) {
        this.gridFieldOrder = gridFieldOrder;
    }

    public Double getGridFieldWidth() {
        return gridFieldWidth;
    }

    public void setGridFieldWidth(Double gridFieldWidth) {
        this.gridFieldWidth = gridFieldWidth;
    }

    public Boolean getGridFieldQueryFlag() {
        return gridFieldQueryFlag;
    }

    public void setGridFieldQueryFlag(Boolean gridFieldQueryFlag) {
        this.gridFieldQueryFlag = gridFieldQueryFlag;
    }
}