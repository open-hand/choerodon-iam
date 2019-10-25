package io.choerodon.base.api.vo;

/**
 * @author jiameng.cao
 * @date 2019/9/29
 */
public class ServiceVersionVO {
    private Long id;

    private Long marketServiceId;

    private String version;

    private Integer fixVersion;

    private Boolean newFixVersion;

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

    public Integer getFixVersion() {
        return fixVersion;
    }

    public void setFixVersion(Integer fixVersion) {
        this.fixVersion = fixVersion;
    }

    public Boolean getNewFixVersion() {
        return newFixVersion;
    }

    public void setNewFixVersion(Boolean newFixVersion) {
        this.newFixVersion = newFixVersion;
    }
}
