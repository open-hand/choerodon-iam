package io.choerodon.iam.api.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author scp
 * @since 2020/4/27
 */
public class TenantConfigVO {
    @ApiModelProperty("创建者Id/非必填/默认为登陆用户id")
    @Encrypt
    private Long userId;

    @ApiModelProperty("组织地址/非必填")
    private String address;

    @ApiModelProperty(value = "组织图标url")
    private String imageUrl;

    @ApiModelProperty(value = "组织官网地址")
    private String homePage;

    @ApiModelProperty(value = "组织规模")
    private String scale;

    @ApiModelProperty(value = "组织所在行业")
    private String businessType;

    @ApiModelProperty(value = "邮箱后缀，唯一。注册时必输，数据库非必输")
    private String emailSuffix;

    @ApiModelProperty(value = "是否是注册组织")
    private Boolean isRegister;


    @ApiModelProperty(value = "远程令牌连接功能是否开启，默认为true")
    private Boolean remoteTokenEnabled;

    @ApiModelProperty(value = "组织类别")
    private String category;
    @ApiModelProperty("组织来源")
    private String orgOrigin;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("访问人数")
    private Long visitors;
    @ApiModelProperty("客户成功经理")
    @Encrypt
    private Long successManager;
    private User successManagerUserVO;
    @ApiModelProperty("销售")
    @Encrypt
    private Long marketingManager;
    private User marketingManagerUserVO;
    @ApiModelProperty("剩余时间")
    private Integer daysRemaining;
    @ApiModelProperty("到期日期")
    private Date dueDate;


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getRemoteTokenEnabled() {
        return remoteTokenEnabled;
    }

    public void setRemoteTokenEnabled(Boolean remoteTokenEnabled) {
        this.remoteTokenEnabled = remoteTokenEnabled;
    }

    public Boolean getRegister() {
        return isRegister;
    }

    public void setRegister(Boolean register) {
        isRegister = register;
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

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
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

    public String getOrgOrigin() {
        return orgOrigin;
    }

    public void setOrgOrigin(String orgOrigin) {
        this.orgOrigin = orgOrigin;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getVisitors() {
        return visitors;
    }

    public void setVisitors(Long visitors) {
        this.visitors = visitors;
    }

    public Long getSuccessManager() {
        return successManager;
    }

    public void setSuccessManager(Long successManager) {
        this.successManager = successManager;
    }

    public Long getMarketingManager() {
        return marketingManager;
    }

    public void setMarketingManager(Long marketingManager) {
        this.marketingManager = marketingManager;
    }

    public User getSuccessManagerUserVO() {
        return successManagerUserVO;
    }

    public void setSuccessManagerUserVO(User successManagerUserVO) {
        this.successManagerUserVO = successManagerUserVO;
    }

    public User getMarketingManagerUserVO() {
        return marketingManagerUserVO;
    }

    public void setMarketingManagerUserVO(User marketingManagerUserVO) {
        this.marketingManagerUserVO = marketingManagerUserVO;
    }

    public Integer getDaysRemaining() {
        return daysRemaining;
    }

    public void setDaysRemaining(Integer daysRemaining) {
        this.daysRemaining = daysRemaining;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }
}
