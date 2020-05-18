package io.choerodon.iam.api.vo.agile;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.api.dto.RoleDTO;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/5/13 9:25
 */
public class RoleVO extends RoleDTO {
    @ApiModelProperty(value = "已分配用户数量/非必填")
    private Integer userCount = 0;

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }
}
