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

    //项目的名称
    private String projectName;
    //项目下应用服务的数量
    private Integer appServerSum;
    //项目的id
    private Long id;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getAppServerSum() {
        return appServerSum;
    }

    public void setAppServerSum(Integer appServerSum) {
        this.appServerSum = appServerSum;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
