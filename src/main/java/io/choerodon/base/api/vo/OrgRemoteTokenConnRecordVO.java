package io.choerodon.base.api.vo;

/**
 * 组织远程令牌连接记录VO
 *
 * @author pengyuhua
 * @author 2019/9/2
 */
public class OrgRemoteTokenConnRecordVO {

    // 远程令牌连接记录ID
    private Long id;
    // 远程令牌ID
    private Long remoteTokenId;
    // 连接时间
    private String connectTime;
    // 远程令牌
    private String remoteToken;
    // 远程令牌 有效、失效
    private Boolean expired;
    // remote token 创建者name
    private String name;
    // remote token 创建者邮箱
    private String email;
    // 连接ip
    private String sourceIp;
    // 操作类型
    private String operation;
    // 组织ID
    private Long organizationId;
    // 组织名称
    private String organizationName;
    // 组织远程令牌连接功能 启用、停用
    private Boolean organizationRemoteTokenEnabled;
    //组织头像地址
    private String organizationImageUrl;
    // 组织乐观锁版本号
    private Long organizationObjectVersionNumber;

    private Long queryOrgIDs;

    private String[] params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRemoteTokenId() {
        return remoteTokenId;
    }

    public void setRemoteTokenId(Long remoteTokenId) {
        this.remoteTokenId = remoteTokenId;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Boolean getOrganizationRemoteTokenEnabled() {
        return organizationRemoteTokenEnabled;
    }

    public void setOrganizationRemoteTokenEnabled(Boolean organizationRemoteTokenEnabled) {
        this.organizationRemoteTokenEnabled = organizationRemoteTokenEnabled;
    }

    public String getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(String connectTime) {
        this.connectTime = connectTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getQueryOrgIDs() {
        return queryOrgIDs;
    }

    public void setQueryOrgIDs(Long queryOrgIDs) {
        this.queryOrgIDs = queryOrgIDs;
    }

    public String getOrganizationImageUrl() {
        return organizationImageUrl;
    }

    public void setOrganizationImageUrl(String organizationImageUrl) {
        this.organizationImageUrl = organizationImageUrl;
    }

    public Long getOrganizationObjectVersionNumber() {
        return organizationObjectVersionNumber;
    }

    public void setOrganizationObjectVersionNumber(Long organizationObjectVersionNumber) {
        this.organizationObjectVersionNumber = organizationObjectVersionNumber;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
