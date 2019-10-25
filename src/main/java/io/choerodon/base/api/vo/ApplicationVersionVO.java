package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author wanghao
 * @date 2019/9/12
 */
public class ApplicationVersionVO {

    private static final String APPLICATION_VERSION_NAME_REG = "^[.·_\\-a-zA-Z0-9\\s]{1,30}$";

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @NotNull(message = "error.application.id.null")
    @ApiModelProperty(value = "应用主键")
    private Long applicationId;

    @ApiModelProperty(value = "版本名")
    @NotEmpty(message = "error.application.version.empty")
    @Size(min = 1, max = 32, message = "error.application.version.size")
    @Pattern(regexp = APPLICATION_VERSION_NAME_REG, message = "error.application.version.name.illegal")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String description;

    @ApiModelProperty(value = "发布状态")
    private String status;

    private Long objectVersionNumber;

    private int order;

    @NotEmpty(message = "error.application.version.service.empty")
    private List<AppServiceDetailsVO> appServiceDetailsVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AppServiceDetailsVO> getAppServiceDetailsVOS() {
        return appServiceDetailsVOS;
    }

    public void setAppServiceDetailsVOS(List<AppServiceDetailsVO> appServiceDetailsVOS) {
        this.appServiceDetailsVOS = appServiceDetailsVOS;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
