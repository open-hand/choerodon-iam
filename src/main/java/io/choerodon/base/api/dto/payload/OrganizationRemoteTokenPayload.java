package io.choerodon.base.api.dto.payload;

/**
 * 组织生成远程连接Token的DTO
 */
public class OrganizationRemoteTokenPayload {
    //主键ID/非必填
    private Long id;

    //组织ID
    private Long organizationId;

    //令牌名称
    private String name;

    //联系邮箱
    private String email;

    //令牌状态
    private Boolean expired;

    //远程连接令牌
    private String remoteToken;

    public Long getId() {
        return id;
    }

    public OrganizationRemoteTokenPayload setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public OrganizationRemoteTokenPayload setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrganizationRemoteTokenPayload setName(String name) {
        this.name = name;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public OrganizationRemoteTokenPayload setEmail(String email) {
        this.email = email;
        return this;
    }

    public Boolean getExpired() {
        return expired;
    }

    public OrganizationRemoteTokenPayload setExpired(Boolean expired) {
        this.expired = expired;
        return this;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public OrganizationRemoteTokenPayload setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
        return this;
    }
}
