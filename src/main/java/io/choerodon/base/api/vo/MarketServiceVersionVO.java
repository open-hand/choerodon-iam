package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author wanghao
 * @Date 2019/8/21 17:17
 */
public class MarketServiceVersionVO {
    @ApiModelProperty(value = "主键")
    private Long id;
    @ApiModelProperty(value = "服务id")
    private Long marketServiceId;
    @ApiModelProperty(value = "版本")
    private String version;
    @ApiModelProperty(value = "镜像地址")
    private String imageUrl;
    @ApiModelProperty(value = "chart地址")
    private String chartUrl;
    @ApiModelProperty(value = "源码地址")
    private String codeUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMarketServiceId() {
        return marketServiceId;
    }

    public void setMarketServiceId(Long marketServiceId) {
        this.marketServiceId = marketServiceId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChartUrl() {
        return chartUrl;
    }

    public void setChartUrl(String chartUrl) {
        this.chartUrl = chartUrl;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }
}
