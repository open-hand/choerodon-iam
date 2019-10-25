package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 远程令牌管理（启用、停用）
 *
 * @author pengyuhua
 * @author 2019/8/30
 */
public class RemoteTokenManagementResultVO {

    @ApiModelProperty(value = "是否已启用remoteToken")
    private Boolean enabled;
    @ApiModelProperty(value = "是否已生成remoteToken")
    private Boolean generated;
    @ApiModelProperty(value = "附加返回信息")
    private String message;

    public Boolean getEnabled() {
        return enabled;
    }

    public RemoteTokenManagementResultVO setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Boolean getGenerated() {
        return generated;
    }

    public RemoteTokenManagementResultVO setGenerated(Boolean generated) {
        this.generated = generated;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public RemoteTokenManagementResultVO setMessage(String message) {
        this.message = message;
        return this;
    }
}
