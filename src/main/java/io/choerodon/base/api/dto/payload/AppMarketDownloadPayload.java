package io.choerodon.base.api.dto.payload;

import io.swagger.annotations.ApiModelProperty;

import java.util.List;

import io.choerodon.base.infra.dto.mkt.RobotUser;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/22
 */
public class AppMarketDownloadPayload {
    @ApiModelProperty("iam用户Id")
    private Long iamUserId;

    @ApiModelProperty("应用Id")
    private Long appId;

    @ApiModelProperty("应用name")
    private String appName;

    @ApiModelProperty("应用code")
    private String appCode;

    @ApiModelProperty("下载的应用类型")
    private String downloadAppType;

    @ApiModelProperty("PasS端应用版本Id")
    private Long appVersionId;

    @ApiModelProperty("SasS端应用版本Id")
    private Long mktAppVersionId;

    @ApiModelProperty("历史记录Id")
    private Long appDownloadRecordId;

    @ApiModelProperty("下载的组织Id")
    private Long organizationId;

    @ApiModelProperty("harbor用户")
    private RobotUser user;

    @ApiModelProperty("应用服务")
    private List<AppServiceDownloadPayload> appServiceDownloadPayloads;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getIamUserId() {
        return iamUserId;
    }

    public void setIamUserId(Long iamUserId) {
        this.iamUserId = iamUserId;
    }

    public List<AppServiceDownloadPayload> getAppServiceDownloadPayloads() {
        return appServiceDownloadPayloads;
    }

    public void setAppServiceDownloadPayloads(List<AppServiceDownloadPayload> appServiceDownloadPayloads) {
        this.appServiceDownloadPayloads = appServiceDownloadPayloads;
    }

    public Long getMktAppVersionId() {
        return mktAppVersionId;
    }

    public void setMktAppVersionId(Long mktAppVersionId) {
        this.mktAppVersionId = mktAppVersionId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getDownloadAppType() {
        return downloadAppType;
    }

    public void setDownloadAppType(String downloadAppType) {
        this.downloadAppType = downloadAppType;
    }

    public RobotUser getUser() {
        return user;
    }

    public void setUser(RobotUser user) {
        this.user = user;
    }

    public Long getAppVersionId() {
        return appVersionId;
    }

    public void setAppVersionId(Long appVersionId) {
        this.appVersionId = appVersionId;
    }

    public Long getAppDownloadRecordId() {
        return appDownloadRecordId;
    }

    public void setAppDownloadRecordId(Long appDownloadRecordId) {
        this.appDownloadRecordId = appDownloadRecordId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
