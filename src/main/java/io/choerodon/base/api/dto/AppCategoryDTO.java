package io.choerodon.base.api.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

/**
 * @author jiameng.cao
 * @date 2019/6/27
 */

public class AppCategoryDTO extends BaseDTO {

    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "应用类别CODE")
    private String code;

    @ApiModelProperty(value = "应用类别NAME")
    private String name;

    @ApiModelProperty(value = "是否有效")
    @Column(name = "is_enabled")
    private Boolean enabled;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
