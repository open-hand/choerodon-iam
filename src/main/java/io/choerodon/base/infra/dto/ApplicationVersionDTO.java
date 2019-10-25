package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/7/30
 */
@Table(name = "fd_application_version")
public class ApplicationVersionDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @NotNull(message = "error.application.id.null")
    @ApiModelProperty(value = "应用主键")
    private Long applicationId;

    @ApiModelProperty(value = "版本名")
    @NotEmpty(message = "error.application.version.empty")
    @Size(min = 1, max = 32, message = "error.application.version.size")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String description;

    public Long getId() {
        return id;
    }

    public ApplicationVersionDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public ApplicationVersionDTO setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public ApplicationVersionDTO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ApplicationVersionDTO setDescription(String description) {
        this.description = description;
        return this;
    }
}
