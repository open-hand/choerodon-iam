package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @Author: scp
 * @Description:
 * @Date: Created in 2021/8/20
 * @Modified By:
 */
public class UserSearchVO {
    @ApiModelProperty("登录名")
    private String loginName;
    @ApiModelProperty("用户名")
    private String realName;
    @ApiModelProperty("角色名")
    private String roleName;
    @ApiModelProperty("手机")
    private String phone;
    @ApiModelProperty("邮箱")
    private String email;
    @ApiModelProperty("模糊搜索参数")
    private String params;
    @ApiModelProperty("用户状态")
    private Boolean enabled;
    @ApiModelProperty("用户锁定状态")
    private Boolean locked;
    @ApiModelProperty("角色Id")
    @Encrypt
    private Long roles;
    @ApiModelProperty("用户标签")
    private String userLabel;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getRoles() {
        return roles;
    }

    public void setRoles(Long roles) {
        this.roles = roles;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public String getUserLabel() {
        return userLabel;
    }

    public void setUserLabel(String userLabel) {
        this.userLabel = userLabel;
    }
}
