package io.choerodon.base.infra.dto;

import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.Update;
import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/7/30
 */
@Table(name = "fd_application")
public class ApplicationDTO extends BaseDTO {

    private static final String APPLICATION_NAME_REG = "^[-—\\.\\w\\s\\u4e00-\\u9fa5]{1,32}$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "所属项目ID/非必填")
    private Long projectId;

    @ApiModelProperty(value = "应用名/必填")
    @NotEmpty(message = "error.application.name.empty", groups = {Insert.class, Update.class})
    @Size(min = 1, max = 32, message = "error.application.name.size", groups = {Insert.class, Update.class})
    @Pattern(regexp = APPLICATION_NAME_REG, message = "error.application.name.illegal", groups = {Insert.class, Update.class})
    private String name;

    @ApiModelProperty(value = "应用编码(自动生成的UUID)")
    private String code;

    @ApiModelProperty(value = "应用类型")
    private String type;

    @ApiModelProperty(value = "应用描述")
    private String description;

    @ApiModelProperty(value = "应用来源编码（自定义、应用市场下载）")
    private String sourceCode;

    @ApiModelProperty(value = "FEEDBACK使用的应用TOKEN(自动生成的UUID)")
    private String feedbackToken;

    @ApiModelProperty(value = "是否已生成市场发布信息")
    private Boolean hasGenerated;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public ApplicationDTO setProjectId(Long projectId) {
        this.projectId = projectId;
        return this;
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

    public ApplicationDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public ApplicationDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getFeedbackToken() {
        return feedbackToken;
    }

    public void setFeedbackToken(String feedbackToken) {
        this.feedbackToken = feedbackToken;
    }

    public Boolean getHasGenerated() {
        return hasGenerated;
    }

    public ApplicationDTO setHasGenerated(Boolean hasGenerated) {
        this.hasGenerated = hasGenerated;
        return this;
    }

    @Override
    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    @Override
    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
