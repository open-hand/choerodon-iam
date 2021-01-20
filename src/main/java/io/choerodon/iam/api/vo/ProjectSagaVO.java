package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

import io.choerodon.iam.infra.enums.ProjectStatusEnum;

/**
 * Created by wangxiang on 2021/1/19
 */
public class ProjectSagaVO {

    /**
     * 项目id
     */
    private Long projectId;

    /**
     * 项目当前的操作
     * {@link ProjectStatusEnum}
     */
    private String operateType;

    /**
     * 需要重试的失败的实例id
     */
    private List<Long> sagaInstanceIds;

    /**
     * 当前的实例
     */
    private SagaInstanceDetails sagaInstanceDetails;

    @ApiModelProperty(value = "实例下完成的任务个数")
    private Integer completedCount;
    @ApiModelProperty(value = "实例下运行的任务个数")
    private Integer runningCount;
    @ApiModelProperty(value = "实例下回滚的任务个数")
    private Integer rollbackCount;
    @ApiModelProperty(value = "实例下失败的任务个数")
    private Integer failedCount;
    @ApiModelProperty(value = "实例下等待被拉取里的任务个数")
    private Integer waitToBePulledCount;
    @ApiModelProperty(value = "实例下排队任务个数")
    private Integer queueCount;

    /**
     * 项目目前的
     */
    private String status;

    /**
     * 已完成的sagaTask
     */
    private Integer completedTask;

    /**
     * 总的sagaTask
     */
    private Integer allTask;

    public Integer getCompletedTask() {
        return completedTask;
    }

    public void setCompletedTask(Integer completedTask) {
        this.completedTask = completedTask;
    }

    public Integer getAllTask() {
        return allTask;
    }

    public void setAllTask(Integer allTask) {
        this.allTask = allTask;
    }

    public Integer getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(Integer completedCount) {
        this.completedCount = completedCount;
    }

    public Integer getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(Integer runningCount) {
        this.runningCount = runningCount;
    }

    public Integer getRollbackCount() {
        return rollbackCount;
    }

    public void setRollbackCount(Integer rollbackCount) {
        this.rollbackCount = rollbackCount;
    }

    public Integer getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(Integer failedCount) {
        this.failedCount = failedCount;
    }

    public Integer getWaitToBePulledCount() {
        return waitToBePulledCount;
    }

    public void setWaitToBePulledCount(Integer waitToBePulledCount) {
        this.waitToBePulledCount = waitToBePulledCount;
    }

    public Integer getQueueCount() {
        return queueCount;
    }

    public void setQueueCount(Integer queueCount) {
        this.queueCount = queueCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public List<Long> getSagaInstanceIds() {
        return sagaInstanceIds;
    }

    public void setSagaInstanceIds(List<Long> sagaInstanceIds) {
        this.sagaInstanceIds = sagaInstanceIds;
    }

    public SagaInstanceDetails getSagaInstanceDetails() {
        return sagaInstanceDetails;
    }

    public void setSagaInstanceDetails(SagaInstanceDetails sagaInstanceDetails) {
        this.sagaInstanceDetails = sagaInstanceDetails;
    }
}
