package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.*;

import io.choerodon.mybatis.annotation.*;
import io.choerodon.mybatis.entity.*;

import static io.choerodon.base.infra.utils.RegularExpression.*;

/**
 * @author superlee
 * @since 2019-04-23
 */
@MultiLanguage
@Table(name = "fd_lookup_value")
public class LookupValueDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "快码值code")
    @NotEmpty(message = "error.lookup.code.empty")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION,message = "error.lookup.code.format.incorrect")
    private String code;

    @ApiModelProperty(value = "所属快码id", hidden = true)
    private Long lookupId;

    @MultiLanguageField
    @ApiModelProperty(value = "快码值描述")
    @NotEmpty(message = "error.lookup.description.empty")
    private String description;

    @ApiModelProperty(value = "排列顺序")
    private Integer displayOrder;

    @Transient
    @ApiModelProperty(value = "同code")
    private String  value;

    @Transient
    @ApiModelProperty(value = "同description")
    private String  meaning;


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

    public Long getLookupId() {
        return lookupId;
    }

    public void setLookupId(Long lookupId) {
        this.lookupId = lookupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
}
