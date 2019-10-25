package io.choerodon.base.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author jiameng.cao
 * @date 2019/8/6
 */
public class MarketApplicationVersionVO {
    private Long id;

    @ApiModelProperty("应用的Id")
    private String marketAppCode;


    @ApiModelProperty("应用的版本")
    private String version;

    @ApiModelProperty("版本日志")
    private String changelog;

    @ApiModelProperty("文档")
    private String document;

    @ApiModelProperty("审批状态，默认审批中doing, success, failed")
    private String approveStatus;

    @ApiModelProperty("审批返回信息")
    private String approveMessage;

    @ApiModelProperty("版本创建时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private Date versionCreationDate;
    @ApiModelProperty("版本发布时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;

    @ApiModelProperty("版本下载状态")
    private String downloadStatus;

    @ApiModelProperty("是否已购买")
    private Boolean purchased;

    @ApiModelProperty("最新修复版本批次")
    private Integer latestFixVersion;

    @ApiModelProperty("最新修复版本时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date latestFixVersionDate;

    @ApiModelProperty("是否未下载的新版本")
    private Boolean newVersion;

    private List<MarketAppServiceVO> marketAppServiceVOS;
    private List<Long> serviceVersionId;

    private String displayStatus;

    public String getVersion() {
        return version;
    }

    public MarketApplicationVersionVO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getChangelog() {
        return changelog;
    }

    public MarketApplicationVersionVO setChangelog(String changelog) {
        this.changelog = changelog;
        return this;
    }

    public String getDocument() {
        return document;
    }

    public MarketApplicationVersionVO setDocument(String document) {
        this.document = document;
        return this;
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

    public Date getVersionCreationDate() {
        return versionCreationDate;
    }

    public MarketApplicationVersionVO setVersionCreationDate(Date versionCreationDate) {
        this.versionCreationDate = versionCreationDate;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public List<MarketAppServiceVO> getMarketAppServiceVOS() {
        return marketAppServiceVOS;
    }

    public void setMarketAppServiceVOS(List<MarketAppServiceVO> marketAppServiceVOS) {
        this.marketAppServiceVOS = marketAppServiceVOS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketAppCode() {
        return marketAppCode;
    }

    public MarketApplicationVersionVO setMarketAppCode(String marketAppCode) {
        this.marketAppCode = marketAppCode;
        return this;
    }

    public String getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(String downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public Boolean getPurchased() {
        return purchased;
    }

    public void setPurchased(Boolean purchased) {
        this.purchased = purchased;
    }

    public List<Long> getServiceVersionId() {
        return serviceVersionId;
    }

    public void setServiceVersionId(List<Long> serviceVersionId) {
        this.serviceVersionId = serviceVersionId;
    }

    public Integer getLatestFixVersion() {
        return latestFixVersion;
    }

    public void setLatestFixVersion(Integer latestFixVersion) {
        this.latestFixVersion = latestFixVersion;
    }

    public Date getLatestFixVersionDate() {
        return latestFixVersionDate;
    }

    public void setLatestFixVersionDate(Date latestFixVersionDate) {
        this.latestFixVersionDate = latestFixVersionDate;
    }

    public Boolean getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(Boolean newVersion) {
        this.newVersion = newVersion;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }
}
