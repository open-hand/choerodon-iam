package io.choerodon.iam.infra.dto;

import org.hzero.iam.domain.entity.Role;
import org.hzero.mybatis.common.query.Where;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author superlee
 * @since 2019-04-22
 */
@Table(name = "fd_organization")
public class OrganizationDTO extends AuditDomain {

    private static final String CODE_REGULAR_EXPRESSION = "^[a-z](([a-z0-9]|-(?!-))*[a-z0-9])*$";

    private static final String NAME_REGULAR_EXPRESSION = "^[-—\\.\\w\\s\\u4e00-\\u9fa5]{1,32}$";

    public static final String FIELD_NAME = "name";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键/非必填")
    private Long id;

    @ApiModelProperty(value = "组织名/必填")
    @NotEmpty(message = "error.organization.name.empty")
    @Size(min = 1, max = 32, message = "error.organization.name.size")
    @Pattern(regexp = NAME_REGULAR_EXPRESSION, message = "error.organization.name.illegal")
    @Where
    private String name;

    @ApiModelProperty(value = "组织编码/必填")
    @NotEmpty(message = "error.organization.code.empty")
    @Pattern(regexp = CODE_REGULAR_EXPRESSION, message = "error.organization.code.illegal")
    @Size(min = 1, max = 15, message = "error.organization.code.length")
    private String code;

    @ApiModelProperty("创建者Id/非必填/默认为登陆用户id")
    private Long userId;

    @ApiModelProperty("组织地址/非必填")
    private String address;

    @ApiModelProperty(value = "组织类别")
    private String category;

    @ApiModelProperty(value = "组织图标url")
    private String imageUrl;

    @Column(name = "is_enabled")
    @ApiModelProperty(value = "是否启用/非必填/默认：true")
    private Boolean enabled;

    @ApiModelProperty(value = "组织官网地址")
    private String homePage;

    @ApiModelProperty(value = "组织规模")
    private Integer scale;

    @ApiModelProperty(value = "组织所在行业")
    private String businessType;

    @ApiModelProperty(value = "邮箱后缀，唯一。注册时必输，数据库非必输")
    private String emailSuffix;

    @ApiModelProperty(value = "是否是注册组织")
    private Boolean isRegister;

    @Transient
    private List<ProjectDTO> projects;

    @Transient
    @ApiModelProperty(value = "项目数量")
    private Integer projectCount;

    @Transient
    @ApiModelProperty(value = "用户数量")
    private Integer userCount;

    @Column(name = "is_remote_token_enabled")
    @ApiModelProperty(value = "远程令牌连接功能是否开启，默认为true")
    private Boolean remoteTokenEnabled;

    @Transient
    private List<Role> roles;

    @Transient
    private String ownerLoginName;

    @Transient
    private String ownerRealName;

    @Transient
    private String ownerPhone;

    @Transient
    private String ownerEmail;

    @Transient
    private Boolean isInto = true;

    @Transient
    private Date creationDate;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getOwnerLoginName() {
        return ownerLoginName;
    }

    public void setOwnerLoginName(String ownerLoginName) {
        this.ownerLoginName = ownerLoginName;
    }

    public String getOwnerRealName() {
        return ownerRealName;
    }

    public void setOwnerRealName(String ownerRealName) {
        this.ownerRealName = ownerRealName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Boolean getInto() {
        return isInto;
    }

    public void setInto(Boolean into) {
        isInto = into;
    }

    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEmailSuffix() {
        return emailSuffix;
    }

    public void setEmailSuffix(String emailSuffix) {
        this.emailSuffix = emailSuffix;
    }

    public Boolean getRegister() {
        return isRegister;
    }

    public void setRegister(Boolean register) {
        isRegister = register;
    }

    public Boolean getRemoteTokenEnabled() {
        return remoteTokenEnabled;
    }

    public void setRemoteTokenEnabled(Boolean remoteTokenEnabled) {
        this.remoteTokenEnabled = remoteTokenEnabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationDTO that = (OrganizationDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
}
