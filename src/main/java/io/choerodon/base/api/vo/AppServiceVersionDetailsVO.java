package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 此VO用于应用服务版本信息
 */
public class AppServiceVersionDetailsVO {
    @ApiModelProperty("应用服务版本主键")
    private Long id;
    @ApiModelProperty("应用服务版本名称")
    private String version;
    @ApiModelProperty(value = "发布状态")
    private String status;

    public Long getId() {
        return id;
    }

    public AppServiceVersionDetailsVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public AppServiceVersionDetailsVO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public AppServiceVersionDetailsVO setStatus(String status) {
        this.status = status;
        return this;
    }
}
