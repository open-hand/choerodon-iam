package io.choerodon.iam.api.vo;

/**
 * @author shanyu
 * @since 2021/10/9
 */
public class ExportTenantVO {

    /**
     * 组织名称
     */
    private String tenantName;

    /**
     * 组织编码
     */
    private String tenantNum;

    /**
     * 组织所在地
     */
    private String address;

    /**
     * 组织来源
     */
    private String orgOrigin;

    /**
     * 组织所有者
     */
    private String ownerRealName;

    /**
     * 邮箱
     */
    private String ownerEmail;

    /**
     * 手机号码
     */
    private String ownerPhone;

    /**
     * 项目数量
     */
    private Integer projectCount;

    /**
     * 用户数量
     */
    private Integer userCount;

    /**
     * 访问量
     */
    private Long visitors;

    /**
     * 官网地址
     */
    private String homePage;

    /**
     * 创建时间
     */
    private String creationDate;

    /**
     * 状态
     */
    private String enabledFlag;

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantNum() {
        return tenantNum;
    }

    public void setTenantNum(String tenantNum) {
        this.tenantNum = tenantNum;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrgOrigin() {
        return orgOrigin;
    }

    public void setOrgOrigin(String orgOrigin) {
        this.orgOrigin = orgOrigin;
    }

    public String getOwnerRealName() {
        return ownerRealName;
    }

    public void setOwnerRealName(String ownerRealName) {
        this.ownerRealName = ownerRealName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Long getVisitors() {
        return visitors;
    }

    public void setVisitors(Long visitors) {
        this.visitors = visitors;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(String enabledFlag) {
        this.enabledFlag = enabledFlag;
    }
}
