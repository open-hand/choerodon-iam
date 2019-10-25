package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by superlee on 2018/10/11.
 */
public class ResendEmailMsgDTO {
    private String email;
    @ApiModelProperty("是否可重发")
    private Boolean canResend;
    @ApiModelProperty("重发剩余时间/单位秒")
    private Long remainingSecond;
    @ApiModelProperty("发送是否成功")
    private Boolean successful;
    @ApiModelProperty("失败原因")
    private String failedCause;

    public Boolean getSuccessful() {
        return successful;
    }

    public void setSuccessful(Boolean successful) {
        this.successful = successful;
    }

    public String getFailedCause() {
        return failedCause;
    }

    public void setFailedCause(String failedCause) {
        this.failedCause = failedCause;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getCanResend() {
        return canResend;
    }

    public void setCanResend(Boolean canResend) {
        this.canResend = canResend;
    }

    public Long getRemainingSecond() {
        return remainingSecond;
    }

    public void setRemainingSecond(Long remainingSecond) {
        this.remainingSecond = remainingSecond;
    }
}
