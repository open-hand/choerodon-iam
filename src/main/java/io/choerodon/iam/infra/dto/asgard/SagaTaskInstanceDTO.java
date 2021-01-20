package io.choerodon.iam.infra.dto.asgard;

import java.util.Date;

public class SagaTaskInstanceDTO {

    private Long id;

    private Long sagaInstanceId;

    private String taskCode;

    private String sagaCode;

    private String instanceLock;

    private String status;

    private Long inputDataId;

    private Long outputDataId;

    private Integer seq;

    private Integer maxRetryCount;

    private Integer retriedCount;

    private String exceptionMessage;

    private String concurrentLimitPolicy;

    private Integer concurrentLimitNum;

    private Date plannedStartTime;

    private Date actualStartTime;

    private Date actualEndTime;

    private String service;

    private String failureCallbackStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSagaInstanceId() {
        return sagaInstanceId;
    }

    public void setSagaInstanceId(Long sagaInstanceId) {
        this.sagaInstanceId = sagaInstanceId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getSagaCode() {
        return sagaCode;
    }

    public void setSagaCode(String sagaCode) {
        this.sagaCode = sagaCode;
    }

    public String getInstanceLock() {
        return instanceLock;
    }

    public void setInstanceLock(String instanceLock) {
        this.instanceLock = instanceLock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getInputDataId() {
        return inputDataId;
    }

    public void setInputDataId(Long inputDataId) {
        this.inputDataId = inputDataId;
    }

    public Long getOutputDataId() {
        return outputDataId;
    }

    public void setOutputDataId(Long outputDataId) {
        this.outputDataId = outputDataId;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public Integer getMaxRetryCount() {
        return maxRetryCount;
    }

    public void setMaxRetryCount(Integer maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public Integer getRetriedCount() {
        return retriedCount;
    }

    public void setRetriedCount(Integer retriedCount) {
        this.retriedCount = retriedCount;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }

    public String getConcurrentLimitPolicy() {
        return concurrentLimitPolicy;
    }

    public void setConcurrentLimitPolicy(String concurrentLimitPolicy) {
        this.concurrentLimitPolicy = concurrentLimitPolicy;
    }

    public Integer getConcurrentLimitNum() {
        return concurrentLimitNum;
    }

    public void setConcurrentLimitNum(Integer concurrentLimitNum) {
        this.concurrentLimitNum = concurrentLimitNum;
    }

    public Date getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(Date plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public Date getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime) {
        this.actualEndTime = actualEndTime;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFailureCallbackStatus() {
        return failureCallbackStatus;
    }

    public void setFailureCallbackStatus(String failureCallbackStatus) {
        this.failureCallbackStatus = failureCallbackStatus;
    }

    @Override
    public String toString() {
        return "SagaTaskInstanceDTO{" +
                "id=" + id +
                ", status='" + status + '\'' +
                '}';
    }


}
