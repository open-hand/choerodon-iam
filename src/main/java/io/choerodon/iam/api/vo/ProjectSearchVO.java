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
}
