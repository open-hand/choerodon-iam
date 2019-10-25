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
public class MarketPublishApplicationVO {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "租户")
    private Long organizationId;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "应用编码")
    private String code;

    @ApiModelProperty(value = "图标url")
    @NotNull(message = "error.imgUrl.cannot.be.null")
    @Size(max = 255, message = "error.imgUrl.size")
    private String imageUrl;

    @ApiModelProperty(value = "描述")
    @Size(max = 128, message = "error.description.size")
    @NotNull(message = "error.description.cannot.be.null")
    private String description;

    @ApiModelProperty(value = "贡献者，一般为项目名或者组织名")
    @Size(max = 100, message = "error.contributor.size")
    private String contributor;

    @ApiModelProperty("应用概览")
    private String overview;

    @ApiModelProperty("应用发布日期")
    private Date publishDate;

    @ApiModelProperty("最新版本号，仅用于应用详情显示")
    private String lastVersion;

    @ApiModelProperty("最新版本更新时间，仅用于应用详情显示")
    private Date lastVersionDate;

    @ApiModelProperty(value = "是否免费，默认 1， 表示免费。 0代表收费")
    @NotNull(message = "error.isFree.cannot.be.null")
    private Boolean free;

    @ApiModelProperty(value = "应用类别名")
    private String categoryName;

    @ApiModelProperty("发布类型")
    @Size(max = 50, message = "error.publishType.size")
    private String type;

    @ApiModelProperty("是否是预置应用类型")
    private Boolean categoryDefault;

    @ApiModelProperty("应用类型Id")
    private Long categoryId;

    private String remark;
    private MarketApplicationVersionVO marketApplicationVersionVO;
    private List<MarketApplicationVersionVO> appVersionList;
    private String version;
    private String approveStatus;
    private String approveMessage;
    private Integer versionNum;
    private Long marketVersionId;
    private String document;
    @ApiModelProperty(value = "应用来源")
    private String sourceApplicationName;
    @ApiModelProperty(value = "应用名称是否可编辑")
    private Boolean appNameEditable;
    @ApiModelProperty(value = "通知邮箱")
    private String notificationEmail;
    @ApiModelProperty("发布类型")
    private String publishType;
    @ApiModelProperty(value = "是否已发布")
    private Boolean released;

    @ApiModelProperty(value = "乐观锁版本号")
    private Long objectVersionNumber;

    // 判断remark是否展示
    private Boolean RemarkVisitable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public MarketPublishApplicationVO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public MarketPublishApplicationVO setDescription(String description) {
        this.description = description;
        return this;
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

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public Date getLastVersionDate() {
        return lastVersionDate;
    }

    public void setLastVersionDate(Date lastVersionDate) {
        this.lastVersionDate = lastVersionDate;
    }

    public Boolean getFree() {
        return free;
    }

    public MarketPublishApplicationVO setFree(Boolean free) {
        this.free = free;
        return this;
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

    public Integer getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(Integer versionNum) {
        this.versionNum = versionNum;
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

    public void setType(String type) {
        this.type = type;
    }

    public MarketApplicationVersionVO getMarketApplicationVersionVO() {
        return marketApplicationVersionVO;
    }

    public void setMarketApplicationVersionVO(MarketApplicationVersionVO marketApplicationVersionVO) {
        this.marketApplicationVersionVO = marketApplicationVersionVO;
    }

    public String getSourceApplicationName() {
        return sourceApplicationName;
    }

    public MarketPublishApplicationVO setSourceApplicationName(String sourceApplicationName) {
        this.sourceApplicationName = sourceApplicationName;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public String getPublishType() {
        return publishType;
    }

    public MarketPublishApplicationVO setPublishType(String publishType) {
        this.publishType = publishType;
        return this;
    }

    public Boolean getAppNameEditable() {
        return appNameEditable;
    }

    public MarketPublishApplicationVO setAppNameEditable(Boolean appNameEditable) {
        this.appNameEditable = appNameEditable;
        return this;
    }

    public Boolean getReleased() {
        return released;
    }

    public MarketPublishApplicationVO setReleased(Boolean released) {
        this.released = released;
        return this;
    }

    public Boolean getCategoryDefault() {
        return categoryDefault;
    }

    public void setCategoryDefault(Boolean categoryDefault) {
        this.categoryDefault = categoryDefault;
    }

    public Boolean getRemarkVisitable() {
        return RemarkVisitable;
    }

    public MarketPublishApplicationVO setRemarkVisitable(Boolean remarkVisitable) {
        RemarkVisitable = remarkVisitable;
        return this;
    }
}
