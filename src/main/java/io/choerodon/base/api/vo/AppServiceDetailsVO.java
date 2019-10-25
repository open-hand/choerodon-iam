package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 此VO用于前端查看:应用服务 及 应用服务版本信息
 */
public class AppServiceDetailsVO {
    @ApiModelProperty("应用服务主键")
    private Long id;
    @ApiModelProperty("应用服务名称")
    private String name;
    @ApiModelProperty("应用服务编码")
    private String code;
    @ApiModelProperty("应用服务类型")
    private String type;
    @ApiModelProperty("应用服务状态")
    private String status;
    @ApiModelProperty("应用服务版本列表")
    private List<AppServiceVersionDetailsVO> appServiceVersions;
    @ApiModelProperty("应用服务所有版本列表")
    private List<AppServiceVersionDetailsVO> allAppServiceVersions;

    public Long getId() {
        return id;
    }

    public AppServiceDetailsVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public AppServiceDetailsVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public AppServiceDetailsVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getType() {
        return type;
    }

    public AppServiceDetailsVO setType(String type) {
        this.type = type;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AppServiceVersionDetailsVO> getAppServiceVersions() {
        return appServiceVersions;
    }

    public void setAppServiceVersions(List<AppServiceVersionDetailsVO> appServiceVersions) {
        this.appServiceVersions = appServiceVersions;
    }

    public List<AppServiceVersionDetailsVO> getAllAppServiceVersions() {
        return allAppServiceVersions;
    }

    public void setAllAppServiceVersions(List<AppServiceVersionDetailsVO> allAppServiceVersions) {
        this.allAppServiceVersions = allAppServiceVersions;
    }
}
