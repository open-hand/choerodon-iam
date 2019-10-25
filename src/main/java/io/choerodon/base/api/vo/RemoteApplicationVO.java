package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;


/**
 * @author Eugen
 * 此 VO 用于 Paas平台 应用部署 的 远程应用市场
 */
public class RemoteApplicationVO {

    @ApiModelProperty(value = "主键")
    private Long id; // f:db 来自 数据库

    @ApiModelProperty(value = "应用ID")
    private Long appId; // f:db

    @ApiModelProperty(value = "应用名称")
    private String name; // f:db

    @ApiModelProperty(value = "应用编码")
    private String code; // f:db

    @ApiModelProperty(value = "图标url")
    private String imageUrl; // f:db

    @ApiModelProperty(value = "应用类别")
    private String category;  // f:db

    @ApiModelProperty(value = "描述")
    private String description; // f:db

    @ApiModelProperty(value = "贡献者，一般为项目名或者组织名")
    private String contributor; // f:db

    @ApiModelProperty(value = "贡献者头像")
    private String contributorUrl;

    @ApiModelProperty(value = "是否免费，默认 1， 表示免费。 0代表收费")
    private Boolean free; // f:db

    @ApiModelProperty(value = "购买是否过期，过期为1")
    private Boolean expired; // f:c

    @ApiModelProperty(value = "最近版本发布时间")
    private Date latestVersionDate; // f:db

    @ApiModelProperty(value = "应用被下载次数")
    private Integer downCount;

    @ApiModelProperty(value = "最近版本")
    private String latestVersion;

    @ApiModelProperty(value = "新版本/修复版本标识")
    private Boolean hasNewVersion;

    @ApiModelProperty(value = "应用的版本及最新修复版本批次")
    private List<MarketApplicationVersionVO> versionDTOS;

    public Long getId() {
        return id;
    }

    public RemoteApplicationVO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
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

    public void setCode(String code) {
        this.code = code;
    }


    public String getCategory() {
        return category;
    }

    public RemoteApplicationVO setCategory(String category) {
        this.category = category;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public RemoteApplicationVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getContributor() {
        return contributor;
    }

    public RemoteApplicationVO setContributor(String contributor) {
        this.contributor = contributor;
        return this;
    }

    public Boolean isFree() {
        return free;
    }

    public RemoteApplicationVO setFree(Boolean free) {
        this.free = free;
        return this;
    }

    public Boolean isExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public Date getLatestVersionDate() {
        return latestVersionDate;
    }

    public void setLatestVersionDate(Date latestVersionDate) {
        this.latestVersionDate = latestVersionDate;
    }

    public Integer getDownCount() {
        return downCount;
    }

    public void setDownCount(Integer downCount) {
        this.downCount = downCount;
    }

    public String getContributorUrl() {
        return contributorUrl;
    }

    public void setContributorUrl(String contributorUrl) {
        this.contributorUrl = contributorUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Boolean getHasNewVersion() {
        return hasNewVersion;
    }

    public void setHasNewVersion(Boolean hasNewVersion) {
        this.hasNewVersion = hasNewVersion;
    }

    public List<MarketApplicationVersionVO> getVersionDTOS() {
        return versionDTOS;
    }

    public void setVersionDTOS(List<MarketApplicationVersionVO> versionDTOS) {
        this.versionDTOS = versionDTOS;
    }
}
