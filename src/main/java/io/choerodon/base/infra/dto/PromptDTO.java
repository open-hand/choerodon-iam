package io.choerodon.base.infra.dto;

import io.choerodon.base.api.validator.Insert;
import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static io.choerodon.base.infra.utils.RegularExpression.ALPHANUMERIC_AND_SYMBOLS;

/**
 * @author wkj
 * @since 2019/10/30
 **/
@Table(name = "fd_prompt")
public class PromptDTO extends BaseDTO {

    @Id
    @GeneratedValue
    @ApiModelProperty(value = "id/非必填")
    Long id;

    @ApiModelProperty(value = "文本编码/非必填")
    @Pattern(regexp = ALPHANUMERIC_AND_SYMBOLS, message = "error.prompt.code.illegal", groups = {Insert.class})
    @NotEmpty(message = "error.prompt.code.empty", groups = {Insert.class})
    String promptCode;

    @NotEmpty(message = "error.lang.empty", groups = {Insert.class})
    @ApiModelProperty(value = "语言/非必填")
    String lang;

    @NotEmpty(message = "error.service.code.empty", groups = {Insert.class})
    @ApiModelProperty(value = "服务编码/非必填")
    String serviceCode;

    @NotEmpty(message = "error.description.empty", groups = {Insert.class})
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

    public PromptDTO setPromptCode(String promptCode) {
        this.promptCode = promptCode;
        return this;
    }

    public String getLang() {
        return lang;
    }

    public PromptDTO setLang(String lang) {
        this.lang = lang;
        return this;
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
