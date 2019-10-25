package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * @author 王灏
 * @date 2019/8/25
 */
public class PublishedApplicationVO{

    private Long id;

    @ApiModelProperty("租户ID")
    private Long organizationId;

    @ApiModelProperty("应用名称")
    private String name;

    @ApiModelProperty("应用编码")
    private String code;

    @ApiModelProperty(value = "图标url")
    private String imageUrl;

    @ApiModelProperty(value = "贡献者，一般为项目名或者组织名")
    private String contributor;

    @ApiModelProperty("应用类型Id")
    private Long categoryId;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("应用概览")
    private String overview;

    @ApiModelProperty("最新版本号，仅用于应用详情显示")
    private String latestVersion;

    @ApiModelProperty("最新版本更新时间，仅用于应用详情显示")
    private Date latestVersionDate;

    @ApiModelProperty(value = "是否是免费应用")
    private Boolean free;

    @ApiModelProperty(value = "发布类型")
    private String type;

    @ApiModelProperty(value = "备注")
    private String remark;

    private List<CustomerApplicationVersionVO> customerApplicationVersionVOS;

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

    public String getContributor() {
        return contributor;
    }

    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public Date getLatestVersionDate() {
        return latestVersionDate;
    }

    public void setLatestVersionDate(Date latestVersionDate) {
        this.latestVersionDate = latestVersionDate;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<CustomerApplicationVersionVO> getCustomerApplicationVersionVOS() {
        return customerApplicationVersionVOS;
    }

    public void setCustomerApplicationVersionVOS(List<CustomerApplicationVersionVO> customerApplicationVersionVOS) {
        this.customerApplicationVersionVOS = customerApplicationVersionVOS;
    }
}
