package io.choerodon.base.api.vo;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author jiameng.cao
 * @date 2019/8/6
 */
public class MarketServiceVersionDTO extends BaseDTO {

    private Long id;

    private Long marketServiceId;

    private String version;

    private String imageUrl;

    private String chartUrl;

    private String codeUrl;

    private Boolean newFixVersion;

    private Integer fixVersion;

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

    public Boolean getNewFixVersion() {
        return newFixVersion;
    }

    public void setNewFixVersion(Boolean newFixVersion) {
        this.newFixVersion = newFixVersion;
    }

    public Integer getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(Integer fixVersion) {
        this.fixVersion = fixVersion;
    }
}
