package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * @author Eugen
 */
@Table(name = "fd_remote_token")
public class RemoteTokenDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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


    @ApiModelProperty(value = "连接远程应用市场的令牌")
    private String remoteToken;

    @ApiModelProperty(value = "令牌是否过期")
    @Column(name = "is_expired")
    private Boolean expired;

    @ApiModelProperty(value = "最近一次失效时间")
    private Date latestExpirationTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public RemoteTokenDTO setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
        return this;
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

    public Date getLatestExpirationTime() {
        return latestExpirationTime;
    }

    public void setLatestExpirationTime(Date latestExpirationTime) {
        this.latestExpirationTime = latestExpirationTime;
    }
}
