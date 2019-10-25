package io.choerodon.base.api.dto.payload;

/**
 * @author jiameng.cao
 * @date 2019/9/18
 */
public class MarketAppPayload {
    private String id;
    private String appName;
    private String appCode;
    private Long versionId;
    private String version;
    private String notificationEmail;
    private Long organizationId;
    private Long actionId;
    private Boolean fixFlag;

    private String contributor;


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public Boolean getFixFlag() {
        return fixFlag;
    }

    public void setFixFlag(Boolean fixFlag) {
        this.fixFlag = fixFlag;
    }

    public MarketAppPayload() {
    }

    public MarketAppPayload(String appName, String appCode, Long versionId, String version, Long actionId, Boolean fixFlag) {
        this.appName = appName;
        this.appCode = appCode;
        this.versionId = versionId;
        this.version = version;
        this.actionId = actionId;
        this.fixFlag = fixFlag;
    }
}
