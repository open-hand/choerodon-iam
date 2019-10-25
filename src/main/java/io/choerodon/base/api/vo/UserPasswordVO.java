package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;


public class UserPasswordVO {
    public static final String DEFAULT_USER_PASSWORD = "abcd1234";

    @ApiModelProperty(value = "新密码/必填")
    @NotEmpty
    private String password;

    @ApiModelProperty(value = "原始密码/必填")
    @NotEmpty
    private String originalPassword;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOriginalPassword() {
        return originalPassword;
    }

    public void setOriginalPassword(String originalPassword) {
        this.originalPassword = originalPassword;
    }
}
