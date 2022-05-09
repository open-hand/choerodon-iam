package io.choerodon.iam.api.vo;

/**
 * @author scp
 * @since 2022/5/5
 */
public class SyncStatusVO {
    private Integer completedStepCount;
    private Integer allStepCount;
    private String status;

    public SyncStatusVO(Integer completedStepCount, Integer allStepCount) {
        this.completedStepCount = completedStepCount;
        this.allStepCount = allStepCount;
    }

    public Integer getCompletedStepCount() {
        return completedStepCount;
    }

    public void setCompletedStepCount(Integer completedStepCount) {
        this.completedStepCount = completedStepCount;
    }

    public Integer getAllStepCount() {
        return allStepCount;
    }

    public void setAllStepCount(Integer allStepCount) {
        this.allStepCount = allStepCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
