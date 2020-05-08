package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.api.dto.UserPasswordDTO;

public class UserInfoDTO extends UserPasswordDTO {
    @ApiModelProperty(value = "用户名/非必填")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
