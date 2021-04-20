package io.choerodon.iam.api.vo;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/4/20
 * @Modified By:
 */
public class ResourceLimitVO {
    private Integer userMaxNumber;
    private Integer projectMaxNumber;
    private Integer appSvcMaxNumber;
    private Integer clusterMaxNumber;
    private Integer envMaxNumber;

    public Integer getUserMaxNumber() {
        return userMaxNumber;
    }

    public void setUserMaxNumber(Integer userMaxNumber) {
        this.userMaxNumber = userMaxNumber;
    }

    public Integer getProjectMaxNumber() {
        return projectMaxNumber;
    }

    public void setProjectMaxNumber(Integer projectMaxNumber) {
        this.projectMaxNumber = projectMaxNumber;
    }

    public Integer getAppSvcMaxNumber() {
        return appSvcMaxNumber;
    }

    public void setAppSvcMaxNumber(Integer appSvcMaxNumber) {
        this.appSvcMaxNumber = appSvcMaxNumber;
    }

    public Integer getClusterMaxNumber() {
        return clusterMaxNumber;
    }

    public void setClusterMaxNumber(Integer clusterMaxNumber) {
        this.clusterMaxNumber = clusterMaxNumber;
    }

    public Integer getEnvMaxNumber() {
        return envMaxNumber;
    }

    public void setEnvMaxNumber(Integer envMaxNumber) {
        this.envMaxNumber = envMaxNumber;
    }
}
