package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/11/4 17:01
 */
@VersionAudit
@ModifyAudit
@Table(name = "fd_enterprise_info")
public class EnterpriseInfoDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "组织名称")
    private String organizationName;
    @ApiModelProperty(value = "公司名称")
    private String enterpriseName;
    @ApiModelProperty(value = "所属行业")
    private String enterpriseType;
    @ApiModelProperty(value = "公司规模")
    private String enterpriseScale;

    @ApiModelProperty(value = "admin姓名")
    private String adminName;
    @ApiModelProperty(value = "admin邮箱")
    private String adminEmail;
    @ApiModelProperty(value = "admin手机号")
    private String adminPhone;



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

    @Override
    public String toString() {
        return "EnterpriseInfoDTO{" +
                "id=" + id +
                ", organizationName='" + organizationName + '\'' +
                ", adminName='" + adminName + '\'' +
                ", adminPhone='" + adminPhone + '\'' +
                ", adminEmail='" + adminEmail + '\'' +
                ", enterpriseName='" + enterpriseName + '\'' +
                ", enterpriseScale='" + enterpriseScale + '\'' +
                ", enterpriseType='" + enterpriseType + '\'' +
                '}';
    }
}
