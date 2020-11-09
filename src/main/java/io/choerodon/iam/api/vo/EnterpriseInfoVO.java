package io.choerodon.iam.api.vo;


import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:42
 */
public class EnterpriseInfoVO {
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "组织名称")
    @NotNull(message = "error.organization.name.is.null")
    private String organizationName;
    @ApiModelProperty(value = "admin姓名")
    @NotNull(message = "error.admin.name.is.null")
    private String adminName;
    @ApiModelProperty(value = "admin手机号")
    @NotNull(message = "error.admin.phone.is.null")
    private String adminPhone;
    @ApiModelProperty(value = "admin邮箱")
    @NotNull(message = "error.admin.email.is.null")
    private String adminEmail;
    @ApiModelProperty(value = "公司规模")
    @NotNull(message = "error.enterprise.scale.is.null")
    private String enterpriseScale;
    @ApiModelProperty(value = "所属行业")
    @NotNull(message = "error.enterprise.type.is.null")
    private String enterpriseType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getEnterpriseScale() {
        return enterpriseScale;
    }

    public void setEnterpriseScale(String enterpriseScale) {
        this.enterpriseScale = enterpriseScale;
    }

    public String getEnterpriseType() {
        return enterpriseType;
    }

    public void setEnterpriseType(String enterpriseType) {
        this.enterpriseType = enterpriseType;
    }

    @Override
    public String toString() {
        return "EnterpriseInfoVO{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", adminName='" + adminName + '\'' +
                ", adminPhone='" + adminPhone + '\'' +
                ", adminEmail='" + adminEmail + '\'' +
                ", enterpriseScale='" + enterpriseScale + '\'' +
                ", enterpriseType='" + enterpriseType + '\'' +
                '}';
    }
}
