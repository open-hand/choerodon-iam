package io.choerodon.base.api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author Eugen
 * <p>
 * 该VO用于前端远程连接Token的展示
 */
@JsonIgnoreProperties(value = "remoteToken")
public class RemoteTokenBase64VO {
    @ApiModelProperty(value = "主键ID/非必填")
    private Long id;

    @ApiModelProperty(value = "组织ID")
    private Long organizationId;

    @NotEmpty(message = "error.remote.token.create.name.cannot.be.empty")
    @ApiModelProperty(value = "令牌名称")
    private String name;

    @Email(message = "error.remote.token.create.email.invalid")
    @NotEmpty(message = "error.remote.token.create.email.cannot.be.empty")
    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "令牌状态")
    private Boolean expired;

    @ApiModelProperty(value = "Base64编码后的远程连接令牌")
    private String remoteTokenInBase64;

    @ApiModelProperty(value = "创建时间")
    private Date creationDate;

    @ApiModelProperty(value = "最近一次失效时间")
    private Date latestExpirationTime;

    @ApiModelProperty(value = "远程连接令牌")
    private String remoteToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
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

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }

    public String getRemoteTokenInBase64() {
        return remoteTokenInBase64;
    }

    public void setRemoteTokenInBase64(String remoteTokenInBase64) {
        this.remoteTokenInBase64 = remoteTokenInBase64;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLatestExpirationTime() {
        return latestExpirationTime;
    }

    public void setLatestExpirationTime(Date latestExpirationTime) {
        this.latestExpirationTime = latestExpirationTime;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }
}
