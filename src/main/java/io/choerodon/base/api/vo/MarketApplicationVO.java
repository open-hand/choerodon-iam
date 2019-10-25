package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/7/8
 */
public class MarketApplicationVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "租户")
    @NotNull(message = "error.organizationId.cannot.be.null")
    private Long organizationId;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "应用编码")
    private String code;

    @ApiModelProperty(value = "图标url")
    @NotNull(message = "error.imgUrl.cannot.be.null")
    private String imageUrl;

    @ApiModelProperty(value = "描述")
    @Size(max = 128, message = "error.description.size")
    @NotNull(message = "error.description.cannot.be.null")
    private String description;

    @ApiModelProperty(value = "贡献者，一般为项目名或者组织名")
    @Size(max = 100, message = "error.contributor.size")
    @NotNull(message = "error.contributor.cannot.be.null")
    private String contributor;

    @ApiModelProperty("通知邮箱")
    private String notificationEmail;

    @ApiModelProperty(value = "贡献者头像")
    private String contributorUrl;

    @ApiModelProperty("应用概览")
    @NotNull(message = "error.overview.cannot.be.null")
    private String overview;

    @ApiModelProperty("应用发布日期")
    private Date publishDate;

    @ApiModelProperty("最新版本号，仅用于应用详情显示")
    private String latestVersion;

    @ApiModelProperty("最新版本更新时间，仅用于应用详情显示")
    private Date latestVersionDate;

    @ApiModelProperty(value = "是否免费，默认 1， 表示免费。 0代表收费")
    @NotNull(message = "error.isFree.cannot.be.null")
    private Boolean free;

    @ApiModelProperty(value = "应用类别名")
    private String categoryName;

    @ApiModelProperty("发布类型")
    @Size(max = 50, message = "error.publishType.size")
    private String type;

    private String remark;

    @ApiModelProperty("应用类型Id")
    @NotNull(message = "error.categoryId.cannot.be.null")
    private Long categoryId;

    private List<MarketApplicationVersionVO> appVersionList;
    private MarketApplicationVersionVO marketApplicationVersionVO;
    private String version;
    private String approveStatus;
    private String approveMessage;
    private Integer downCount;
    private Long marketVersionId;
    private String document;

    private Boolean enableDownload;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public MarketApplicationVO setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
        return this;
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

    public MarketApplicationVO setCode(String code) {
        this.code = code;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveMessage() {
        return approveMessage;
    }

    public void setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
    }

    public Long getMarketVersionId() {
        return marketVersionId;
    }

    public void setMarketVersionId(Long marketVersionId) {
        this.marketVersionId = marketVersionId;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getType() {
        return type;
    }

    public MarketApplicationVO setType(String type) {
        this.type = type;
        return this;
    }


    public List<MarketApplicationVersionVO> getAppVersionList() {
        return appVersionList;
    }

    public void setAppVersionList(List<MarketApplicationVersionVO> appVersionList) {
        this.appVersionList = appVersionList;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public MarketApplicationVersionVO getMarketApplicationVersionVO() {
        return marketApplicationVersionVO;
    }

    public MarketApplicationVO setMarketApplicationVersionVO(MarketApplicationVersionVO marketApplicationVersionVO) {
        this.marketApplicationVersionVO = marketApplicationVersionVO;
        return this;
    }

    public Date getLatestVersionDate() {
        return latestVersionDate;
    }

    public void setLatestVersionDate(Date latestVersionDate) {
        this.latestVersionDate = latestVersionDate;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
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

    public Boolean getEnableDownload() {
        return enableDownload;
    }

    public void setEnableDownload(Boolean enableDownload) {
        this.enableDownload = enableDownload;
    }

    public String getRemark() {
        return remark;
    }

    public MarketApplicationVO setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
