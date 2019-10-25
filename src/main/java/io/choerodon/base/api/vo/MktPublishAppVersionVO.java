package io.choerodon.base.api.vo;

import java.util.Date;
import java.util.List;

/**
 * 市场发布页版本信息VO
 *
 * @author pengyuhua
 * @date 2019/9/16
 */
public class MktPublishAppVersionVO {
    private Long id;
    private String version;
    private String status;
    private String changelog;
    private String document;
    private String publishErrorCode;
    private String approveMessage;
    private Date lastUpdateDate;
    private Date creationDate;
    private List<AppServiceDetailsVO> containServices;
    private Long objectVersionNumber;

    // 市场发布应用相关
    private Long mktAppId;
    private String imageUrl;
    private String description;
    private String overview;
    private String remark;

    // 展示判断
    private Boolean released;
    private Boolean publishing;

    public Long getId() {
        return id;
    }

    public MktPublishAppVersionVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public MktPublishAppVersionVO setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public MktPublishAppVersionVO setStatus(String status) {
        this.status = status;
        return this;
    }

    public List<AppServiceDetailsVO> getContainServices() {
        return containServices;
    }

    public String getChangelog() {
        return changelog;
    }

    public MktPublishAppVersionVO setChangelog(String changelog) {
        this.changelog = changelog;
        return this;
    }

    public String getDocument() {
        return document;
    }

    public MktPublishAppVersionVO setDocument(String document) {
        this.document = document;
        return this;
    }

    public MktPublishAppVersionVO setContainServices(List<AppServiceDetailsVO> containServices) {
        this.containServices = containServices;
        return this;
    }

    public String getApproveMessage() {
        return approveMessage;
    }

    public MktPublishAppVersionVO setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
        return this;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public MktPublishAppVersionVO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public MktPublishAppVersionVO setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MktPublishAppVersionVO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MktPublishAppVersionVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MktPublishAppVersionVO setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public Long getMktAppId() {
        return mktAppId;
    }

    public MktPublishAppVersionVO setMktAppId(Long mktAppId) {
        this.mktAppId = mktAppId;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public MktPublishAppVersionVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public MktPublishAppVersionVO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Boolean getReleased() {
        return released;
    }

    public MktPublishAppVersionVO setReleased(Boolean released) {
        this.released = released;
        return this;
    }

    public String getPublishErrorCode() {
        return publishErrorCode;
    }

    public MktPublishAppVersionVO setPublishErrorCode(String publishErrorCode) {
        this.publishErrorCode = publishErrorCode;
        return this;
    }

    public Boolean getPublishing() {
        return publishing;
    }

    public MktPublishAppVersionVO setPublishing(Boolean publishing) {
        this.publishing = publishing;
        return this;
    }
}
