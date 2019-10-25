package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/8/22
 */
@Table(name = "mkt_app_ver_download_record")
public class AppVerDownloadRecordDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "SaaS端应用Id")
    private Long mktAppId;

    @ApiModelProperty(value = "SaaS端应用Code")
    private String mktAppCode;

    @ApiModelProperty(value = "SaaS端应用名称")
    private String mktAppName;

    @ApiModelProperty(value = "SaaS端应用版本Id")
    private Long mktVersionId;

    @ApiModelProperty(value = "SaaS端应用版本名称")
    private String mktVersionName;

    @ApiModelProperty(value = "Saas端应用类型名称")
    private String categoryName;

    @ApiModelProperty(value = "下载状态")
    private String status;

    @ApiModelProperty(value = "组织ID")
    private Long organizationId;

    @ApiModelProperty(value = "下载人")
    private Long createdBy;

    @ApiModelProperty(value = "下载时间")
    private Date creationDate;

    @Transient
    private String downloaderImgUrl;

    @Transient
    private String downloaderRealName;

    @Transient
    private String downloaderLoginName;

    @Transient
    private String mktAppImageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMktAppId() {
        return mktAppId;
    }

    public void setMktAppId(Long mktAppId) {
        this.mktAppId = mktAppId;
    }

    public String getMktAppCode() {
        return mktAppCode;
    }

    public void setMktAppCode(String mktAppCode) {
        this.mktAppCode = mktAppCode;
    }

    public String getMktAppName() {
        return mktAppName;
    }

    public void setMktAppName(String mktAppName) {
        this.mktAppName = mktAppName;
    }

    public Long getMktVersionId() {
        return mktVersionId;
    }

    public void setMktVersionId(Long mktVersionId) {
        this.mktVersionId = mktVersionId;
    }

    public String getMktVersionName() {
        return mktVersionName;
    }

    public void setMktVersionName(String mktVersionName) {
        this.mktVersionName = mktVersionName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Long getCreatedBy() {
        return createdBy;
    }

    @Override
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getDownloaderImgUrl() {
        return downloaderImgUrl;
    }

    public void setDownloaderImgUrl(String downloaderImgUrl) {
        this.downloaderImgUrl = downloaderImgUrl;
    }

    public String getDownloaderRealName() {
        return downloaderRealName;
    }

    public void setDownloaderRealName(String downloaderRealName) {
        this.downloaderRealName = downloaderRealName;
    }

    public String getDownloaderLoginName() {
        return downloaderLoginName;
    }

    public void setDownloaderLoginName(String downloaderLoginName) {
        this.downloaderLoginName = downloaderLoginName;
    }

    public String getMktAppImageUrl() {
        return mktAppImageUrl;
    }

    public void setMktAppImageUrl(String mktAppImageUrl) {
        this.mktAppImageUrl = mktAppImageUrl;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
