package io.choerodon.iam.api.vo;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/9/27
 * @Modified By:
 */
public class UserWizardStatusVO {
    private String code;
    private String name;
    private String status;
    private Boolean enableClick;
    private String remark;
    private String operationLink;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getEnableClick() {
        return enableClick;
    }

    public void setEnableClick(Boolean enableClick) {
        this.enableClick = enableClick;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperationLink() {
        return operationLink;
    }

    public void setOperationLink(String operationLink) {
        this.operationLink = operationLink;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
