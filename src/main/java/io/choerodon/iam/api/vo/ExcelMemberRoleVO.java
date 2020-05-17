package io.choerodon.iam.api.vo;

/**
 * @author superlee
 */
public class ExcelMemberRoleVO {
    private String loginName;
    private String roleCode;
    private String cause;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
}
