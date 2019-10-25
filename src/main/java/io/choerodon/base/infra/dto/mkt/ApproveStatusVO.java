package io.choerodon.base.infra.dto.mkt;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author jiameng.cao
 * @date 2019/8/14
 */
public class ApproveStatusVO {

    @ApiModelProperty(value = "应用编码")
    private String code;

    @ApiModelProperty(value = "应用版本名")
    private String version;

    @ApiModelProperty(value = "PaaS应用版本状态")
    private String status;

    @ApiModelProperty("审批信息")
    private String approveMessage;

    @ApiModelProperty("市场应用类别编码")
    private String categoryCode;

    @ApiModelProperty("市场应用类别名称")
    private String categoryName;

    @ApiModelProperty("最近修复版本批次")
    private Integer latestFixVersion;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApproveMessage() {
        return approveMessage;
    }

    public void setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getLatestFixVersion() {
        return latestFixVersion;
    }

    public void setLatestFixVersion(Integer latestFixVersion) {
        this.latestFixVersion = latestFixVersion;
    }
}
