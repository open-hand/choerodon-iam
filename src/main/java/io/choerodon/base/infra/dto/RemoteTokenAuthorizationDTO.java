package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/05
 */
@Table(name = "fd_remote_token_authorization")
public class RemoteTokenAuthorizationDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "令牌名称")
    private String name;

    @ApiModelProperty(value = "联系邮箱")
    private String email;

    @ApiModelProperty(value = "远程连接的UUID")
    private String remoteToken;

    @ApiModelProperty(value = "token校验地址")
    private String authorizationUrl;

    @ApiModelProperty(value = "令牌状态")
    @Column(name = "status")
    private String status;

    @ApiModelProperty(value = "SaaS端当前组织Code")
    private String organizationCode;

    @ApiModelProperty(value = "SaaS端当前组织Name")
    private String organizationName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
}
