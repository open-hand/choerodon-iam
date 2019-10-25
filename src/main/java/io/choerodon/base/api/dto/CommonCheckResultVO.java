package io.choerodon.base.api.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 远程连接Toke校验结果
 */
public class CommonCheckResultVO {
    @ApiModelProperty(value = "校验是否失败，失败为true")
    private Boolean failed;
    @ApiModelProperty(value = "失败原因")
    private String failMessage;

    public Boolean getFailed() {
        return failed;
    }

    public CommonCheckResultVO setFailed(Boolean failed) {
        this.failed = failed;
        return this;
    }

    public String getFailMessage() {
        return failMessage;
    }

    public CommonCheckResultVO setFailMessage(String failMessage) {
        this.failMessage = failMessage;
        return this;
    }
}
