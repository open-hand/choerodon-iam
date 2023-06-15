package io.choerodon.iam.api.vo;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import io.swagger.annotations.ApiModelProperty;

import org.hzero.core.util.Regexs;

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
    @ApiModelProperty(value = "组织编码")
    @NotNull(message = "error.tenant.num.is.null")
    private String tenantNum;
    @ApiModelProperty(value = "admin姓名")
    @NotNull(message = "error.admin.name.is.null")
    private String adminName;
    @ApiModelProperty(value = "admin手机号")
    @Pattern(regexp = Regexs.MOBILE, message = "error.admin.phone.format.invalid")
    private String adminPhone;
    @ApiModelProperty(value = "admin邮箱")
    @Pattern(regexp = Regexs.EMAIL, message = "error.admin.email.format.invalid")
    private String adminEmail;
    @ApiModelProperty(value = "公司名称")
    @NotNull(message = "error.enterprise.name.is.null")
    private String enterpriseName;
    @ApiModelProperty(value = "公司规模")
    @NotNull(message = "error.enterprise.scale.is.null")
    private String enterpriseScale;
    @ApiModelProperty(value = "所属行业")
    @NotNull(message = "error.enterprise.type.is.null")
    private String enterpriseType;
    @ApiModelProperty(value = "当前猪齿鱼版本")
    private String version;

    public String getVersion() {
        return version;
    }

    public EnterpriseInfoVO setVersion(String version) {
        this.version = version;
        return this;
    }

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

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    @Override
    public String toString() {
        return "EnterpriseInfoVO{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", tenantNum='" + tenantNum + '\'' +
                ", adminName='" + adminName + '\'' +
                ", adminPhone='" + adminPhone + '\'' +
                ", adminEmail='" + adminEmail + '\'' +
                ", enterpriseName='" + enterpriseName + '\'' +
                ", enterpriseScale='" + enterpriseScale + '\'' +
                ", enterpriseType='" + enterpriseType + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
