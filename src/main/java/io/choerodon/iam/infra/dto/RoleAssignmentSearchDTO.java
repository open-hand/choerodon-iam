package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * @author superlee
 */
public class RoleAssignmentSearchDTO {

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "角色名")
    private String roleName;

    @ApiModelProperty(value = "用户名")
    private String realName;

    @ApiModelProperty(value = "用户是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "参数")
    private String[] param;

    private Set<Long> roleIds;

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String[] getParam() {
        return param;
    }

    public void setParam(String[] param) {
        this.param = param;
    }
}
