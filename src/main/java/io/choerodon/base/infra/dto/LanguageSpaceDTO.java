package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * @author wkj
 * @since 2019/10/30
 **/
@Table(name = "fd_prompt")
public class LanguageSpaceDTO extends BaseDTO {

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "id/非必填")
    Long id;

    @ApiModelProperty(value = "文本编码/非必填")
    String promptCode;

    @ApiModelProperty(value = "语言/非必填")
    String lang;

    @ApiModelProperty(value = "服务编码/非必填")
    String serviceCode;

    @ApiModelProperty(value = "描述/非必填")
    String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPromptCode() {
        return promptCode;
    }

    public void setPromptCode(String promptCode) {
        this.promptCode = promptCode;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
