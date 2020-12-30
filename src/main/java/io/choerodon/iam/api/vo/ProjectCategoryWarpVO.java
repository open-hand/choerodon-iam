package io.choerodon.iam.api.vo;

import java.util.List;

/**
 * Created by wangxiang on 2020/12/30
 */
public class ProjectCategoryWarpVO {
    /**
     * 项目已选项目类型
     */
    private List<ProjectCategoryVO> selectedProjectCategory;

    /**
     * 项目未选项目类型
     */
    private List<ProjectCategoryVO> unSelectedProjectCategory;

    public List<ProjectCategoryVO> getSelectedProjectCategory() {
        return selectedProjectCategory;
    }

    public void setSelectedProjectCategory(List<ProjectCategoryVO> selectedProjectCategory) {
        this.selectedProjectCategory = selectedProjectCategory;
    }

    public List<ProjectCategoryVO> getUnSelectedProjectCategory() {
        return unSelectedProjectCategory;
    }

    public void setUnSelectedProjectCategory(List<ProjectCategoryVO> unSelectedProjectCategory) {
        this.unSelectedProjectCategory = unSelectedProjectCategory;
    }
}
