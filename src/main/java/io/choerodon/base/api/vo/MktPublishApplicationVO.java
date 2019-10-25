package io.choerodon.base.api.vo;

import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.infra.dto.MktPublishApplicationDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 此VO用于创建申请发布
 */
public class MktPublishApplicationVO extends MktPublishApplicationDTO {
    @ApiModelProperty(value = "是否新建应用版本（输入）")
    @NotNull(message = "error.mkt.publish.application.create.whether.to.create.can.not.be.null", groups = {Insert.class})
    private Boolean whetherToCreate;

    @ApiModelProperty(value = "选择新建应用版本（输入）")
    private ApplicationVersionQuickCreateVO createVersion;

    @ApiModelProperty("市场应用备注")
    @Size(max = 250, message = "error.mkt.publish.application.remark.size", groups = {Insert.class})
    private String remark;

    public Boolean getWhetherToCreate() {
        return whetherToCreate;
    }

    public void setWhetherToCreate(Boolean whetherToCreate) {
        this.whetherToCreate = whetherToCreate;
    }

    public ApplicationVersionQuickCreateVO getCreateVersion() {
        return createVersion;
    }

    public void setCreateVersion(ApplicationVersionQuickCreateVO createVersion) {
        this.createVersion = createVersion;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
