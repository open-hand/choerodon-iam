package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;
import org.hzero.starter.keyencrypt.core.Encrypt;


public class RegistrantInfoDTO {
    @ApiModelProperty(value = "注册人Id")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "注册人登录名")
    private String loginName;

    @ApiModelProperty(value = "注册人邮箱")
    private String email;

    @ApiModelProperty(value = "注册人用户名")
    private String realName;

    @ApiModelProperty(value = "注册组织ID")
    private Long organizationId;

    @ApiModelProperty(value = "注册组织名称")
    private String organizationName;

    @ApiModelProperty(value = "adminId")
    @Encrypt
    private Long adminId;

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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getAdminId() {
        return adminId;
    }

    public void setAdminId(Long adminId) {
        this.adminId = adminId;
    }

    public void setUser(User userDTO) {
        this.id = userDTO.getId();
        this.email = userDTO.getEmail();
        this.loginName = userDTO.getLoginName();
        this.realName = userDTO.getRealName();
        this.organizationId = userDTO.getOrganizationId();
    }
}
