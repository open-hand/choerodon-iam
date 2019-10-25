package io.choerodon.base.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author Eugen
 * <p>
 * 此VO用于Saas查看远程连接
 */
@JsonIgnoreProperties(value = "remoteToken")
public class RemoteConnectionRecordVO {

    @ApiModelProperty(value = "远程连接记录主键")
    private Long id;

    @ApiModelProperty(value = "远程连接Token主键")
    private Long remoteTokenId;

    @ApiModelProperty(value = "token名称")
    private String name;

    @ApiModelProperty(value = "token")
    private String remoteTokenInBase64;

    @ApiModelProperty(value = "组织")
    private String organizationName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "连接时间")
    private Date connectDate;

    @ApiModelProperty(value = "连接Ip")
    private String sourceIp;

    @ApiModelProperty(value = "远程连接令牌")
    private String remoteToken;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemoteTokenInBase64() {
        return remoteTokenInBase64;
    }

    public void setRemoteTokenInBase64(String remoteTokenInBase64) {
        this.remoteTokenInBase64 = remoteTokenInBase64;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getConnectDate() {
        return connectDate;
    }

    public void setConnectDate(Date connectDate) {
        this.connectDate = connectDate;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getRemoteToken() {
        return remoteToken;
    }

    public void setRemoteToken(String remoteToken) {
        this.remoteToken = remoteToken;
    }
}
