package io.choerodon.iam.api.vo;

import java.util.List;
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

    private String name;

    private String code;

    @Encrypt
    private Long createdBy;

    private String category;

    private Long id;

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
}
