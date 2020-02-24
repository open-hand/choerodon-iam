package io.choerodon.base.api.vo;

/**
 * User: Mr.Wang
 * Date: 2020/2/24
 */
public class ProjectOverViewVO {
    //组织下启用项目的总数
    private Integer enableSum;
    //组织下停用项目的总数
    private Integer stopSum;

    public ProjectOverViewVO() {
    }

    public ProjectOverViewVO(Integer enableSum, Integer stopSum) {
        this.enableSum = enableSum;
        this.stopSum = stopSum;
    }

    public Integer getEnableSum() {
        return enableSum;
    }

    public void setEnableSum(Integer enableSum) {
        this.enableSum = enableSum;
    }

    public Integer getStopSum() {
        return stopSum;
    }

    public void setStopSum(Integer stopSum) {
        this.stopSum = stopSum;
    }
}
