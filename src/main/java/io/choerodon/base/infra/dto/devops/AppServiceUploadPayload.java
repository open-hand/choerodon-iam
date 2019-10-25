package io.choerodon.base.infra.dto.devops;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Creator: ChangpingShi0213@gmail.com
 * Date:  17:24 2019/8/7
 * Description:
 */
public class AppServiceUploadPayload {
    @ApiModelProperty("应用服务Id")
    private Long appServiceId;

    @ApiModelProperty("应用服务Code")
    private String appServiceCode;

    @ApiModelProperty("应用服务名称")
    private String appServiceName;

    @ApiModelProperty("harbor镜像仓库地址")
    private String harborUrl;

    @ApiModelProperty("应用服务版本")
    private List<AppServiceVersionUploadPayload> appServiceVersionUploadPayloads;

    public Long getAppServiceId() {
        return appServiceId;
    }

    public void setAppServiceId(Long appServiceId) {
        this.appServiceId = appServiceId;
    }

    public String getHarborUrl() {
        return harborUrl;
    }

    public void setHarborUrl(String harborUrl) {
        this.harborUrl = harborUrl;
    }

    public String getAppServiceCode() {
        return appServiceCode;
    }

    public void setAppServiceCode(String appServiceCode) {
        this.appServiceCode = appServiceCode;
    }

    public String getAppServiceName() {
        return appServiceName;
    }

    public void setAppServiceName(String appServiceName) {
        this.appServiceName = appServiceName;
    }

    public List<AppServiceVersionUploadPayload> getAppServiceVersionUploadPayloads() {
        return appServiceVersionUploadPayloads;
    }

    public void setAppServiceVersionUploadPayloads(List<AppServiceVersionUploadPayload> appServiceVersionUploadPayloads) {
        this.appServiceVersionUploadPayloads = appServiceVersionUploadPayloads;
    }

    public AppServiceUploadPayload() {
    }

    public AppServiceUploadPayload(Long appServiceId, String appServiceCode, String appServiceName, String harborUrl, List<AppServiceVersionUploadPayload> appServiceVersionUploadPayloads) {
        this.appServiceId = appServiceId;
        this.appServiceCode = appServiceCode;
        this.appServiceName = appServiceName;
        this.harborUrl = harborUrl;
        this.appServiceVersionUploadPayloads = appServiceVersionUploadPayloads;
    }
}
