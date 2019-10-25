package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 应用版本信息VO
 * @author wanghao
 * @date 2019/9/12
 */
public class AppVersionInfoVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "应用主键")
    private Long applicationId;

    @ApiModelProperty(value = "版本名")
    @NotEmpty(message = "error.application.version.empty")
    @Size(min = 1, max = 32, message = "error.application.version.size")
    private String version;

    @ApiModelProperty(value = "版本说明")
    private String description;

    private Long objectVersionNumber;

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



    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public List<AppServiceDetailsVO> getAppServiceDetailsVOS() {
        return appServiceDetailsVOS;
    }

    public void setAppServiceDetailsVOS(List<AppServiceDetailsVO> appServiceDetailsVOS) {
        this.appServiceDetailsVOS = appServiceDetailsVOS;
    }
}
