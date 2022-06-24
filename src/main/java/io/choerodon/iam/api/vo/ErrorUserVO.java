package io.choerodon.iam.api.vo;

/**
 * excel导入失败用户
 *
 * @author superlee
 */
public class ErrorUserVO {
    private String realName;
    private String loginName;
    private String email;
    private String roleCodes;
    private String roleLabels;
    private String password;
    private String phone;
    //导入失败的原因
    private String cause;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleCodes() {
        return roleCodes;
    }

    public void setRoleCodes(String roleCodes) {
        this.roleCodes = roleCodes;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(String roleLabels) {
        this.roleLabels = roleLabels;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }
}
