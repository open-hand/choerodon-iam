package io.choerodon.iam.api.vo;

import java.util.Date;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * Created by wangxiang on 2021/1/21
 */
public class ProjectSearchVO {
    /**
     * 项目类型的id集合
     */
    @Encrypt
    private List<Long> categoryIds;

    /**
     * 项目是否启用
     */
    private Boolean enable;

    /**
     * 类别code的集合
     */
    private List<String> categoryCodes;

    private List<Long> ignoredProjectIds;

    private List<Long> filterProjectIds;

    @ApiModelProperty("项目id")
    private Long id;
    @ApiModelProperty("项目名称")
    private String name;
    @ApiModelProperty("项目code")
    private String code;
    @ApiModelProperty("项目类型")
    private String category;
    @ApiModelProperty("项目是否启用")
    private Boolean enabled;
    @ApiModelProperty("创建者")
    @Encrypt
    private Long createdBy;
    @ApiModelProperty("更新者")
    @Encrypt
    private Long lastUpdatedBy;

    private List<Long> topProjectIds;
    @ApiModelProperty("模糊搜索参数")
    private String searchContent;
    @ApiModelProperty("项目类型id")
    @Encrypt
    private Long categoryId;
    @ApiModelProperty("工作组id")
    @Encrypt
    private Long workGroupId;
    @ApiModelProperty("项目群id")
    private Long programId;

    @ApiModelProperty("创建时间左边界")
    private Date creationDateStart;
    @ApiModelProperty("创建时间右边界")
    private Date creationDateEnd;
    @ApiModelProperty("更新时间左边界")
    private Date lastUpdateDateStart;
    @ApiModelProperty("更新时间右边界")
    private Date lastUpdateDateEnd;

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public List<String> getCategoryCodes() {
        return categoryCodes;
    }

    public void setCategoryCodes(List<String> categoryCodes) {
        this.categoryCodes = categoryCodes;
    }

    public List<Long> getIgnoredProjectIds() {
        return ignoredProjectIds;
    }

    public void setIgnoredProjectIds(List<Long> ignoredProjectIds) {
        this.ignoredProjectIds = ignoredProjectIds;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getFilterProjectIds() {
        return filterProjectIds;
    }

    public void setFilterProjectIds(List<Long> filterProjectIds) {
        this.filterProjectIds = filterProjectIds;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public List<Long> getTopProjectIds() {
        return topProjectIds;
    }

    public void setTopProjectIds(List<Long> topProjectIds) {
        this.topProjectIds = topProjectIds;
    }

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Date getCreationDateStart() {
        return creationDateStart;
    }

    public void setCreationDateStart(Date creationDateStart) {
        this.creationDateStart = creationDateStart;
    }

    public Date getCreationDateEnd() {
        return creationDateEnd;
    }

    public void setCreationDateEnd(Date creationDateEnd) {
        this.creationDateEnd = creationDateEnd;
    }

    public Date getLastUpdateDateStart() {
        return lastUpdateDateStart;
    }

    public void setLastUpdateDateStart(Date lastUpdateDateStart) {
        this.lastUpdateDateStart = lastUpdateDateStart;
    }

    public Date getLastUpdateDateEnd() {
        return lastUpdateDateEnd;
    }

    public void setLastUpdateDateEnd(Date lastUpdateDateEnd) {
        this.lastUpdateDateEnd = lastUpdateDateEnd;
    }
}
