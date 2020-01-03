package io.choerodon.base.api.vo;

import io.choerodon.base.infra.dto.ApplicationVersionDTO;
import io.choerodon.base.infra.enums.ApplicationVersionStatus;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author wanghao
 * @date 2019/9/12
 */
public class ApplicationVersionWithStatusVO extends ApplicationVersionDTO {
    /**
     * {@link ApplicationVersionStatus}
     */
    @ApiModelProperty(value = "发布状态")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
