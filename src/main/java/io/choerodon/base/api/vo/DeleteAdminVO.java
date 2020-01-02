package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * 删除用户的管理员身份时发送saga的载体
 *
 * @author zmf
 * @since 19-12-24
 */
public class DeleteAdminVO {
    @ApiModelProperty("管理员用户id")
    private Long adminUserId;

    public DeleteAdminVO() {
    }

    public DeleteAdminVO(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    public Long getAdminUserId() {
        return adminUserId;
    }

    public void setAdminUserId(Long adminUserId) {
        this.adminUserId = adminUserId;
    }

    @Override
    public String toString() {
        return "DeleteAdminVO{" +
                "adminUserId=" + adminUserId +
                '}';
    }
}
