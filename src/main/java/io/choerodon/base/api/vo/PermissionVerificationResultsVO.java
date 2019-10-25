package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author : longhe6699@gmail.com
 * 用于返回市场发布入口校验接口
 */
public class PermissionVerificationResultsVO {
    @ApiModelProperty("平台是否存在配置")
    private Boolean configurationValid;
    @ApiModelProperty("远程连接令牌是否过期")
    private Boolean tokenValid;
    @ApiModelProperty("客户是否可发布（客户是否启用/客户是否有发布权限）")
    private Boolean publishingPermissionValid;
    @ApiModelProperty("更新状态是否成功")
    private Boolean updateSuccessFlag;

    public Boolean getConfigurationValid() {
        return configurationValid;
    }

    public PermissionVerificationResultsVO setConfigurationValid(Boolean configurationValid) {
        this.configurationValid = configurationValid;
        return this;
    }

    public Boolean getPublishingPermissionValid() {
        return publishingPermissionValid;
    }

    public PermissionVerificationResultsVO setPublishingPermissionValid(Boolean publishingPermissionValid) {
        this.publishingPermissionValid = publishingPermissionValid;
        return this;
    }

    public Boolean getTokenValid() {
        return tokenValid;
    }

    public PermissionVerificationResultsVO setTokenValid(Boolean tokenValid) {
        this.tokenValid = tokenValid;
        return this;
    }

    public Boolean getUpdateSuccessFlag() {
        return updateSuccessFlag;
    }

    public PermissionVerificationResultsVO setUpdateSuccessFlag(Boolean updateSuccessFlag) {
        this.updateSuccessFlag = updateSuccessFlag;
        return this;
    }
}
