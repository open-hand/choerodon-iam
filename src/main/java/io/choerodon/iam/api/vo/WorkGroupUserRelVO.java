package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-11-08 20:44
 */
public class WorkGroupUserRelVO {

    @ApiModelProperty(value = "工作组id集合")
    @Encrypt
    private List<Long> workGroupIds;

    @ApiModelProperty(value = "用户id")
    @Encrypt
    private Long userId;

    @ApiModelProperty(value = "用户")
    private UserVO userVO;

    @ApiModelProperty(value = "用户所属工作组")
    private List<WorkGroupVO> workGroupVOS;

    public List<Long> getWorkGroupIds() {
        return workGroupIds;
    }

    public void setWorkGroupIds(List<Long> workGroupIds) {
        this.workGroupIds = workGroupIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public UserVO getUserVO() {
        return userVO;
    }

    public void setUserVO(UserVO userVO) {
        this.userVO = userVO;
    }

    public List<WorkGroupVO> getWorkGroupVOS() {
        return workGroupVOS;
    }

    public void setWorkGroupVOS(List<WorkGroupVO> workGroupVOS) {
        this.workGroupVOS = workGroupVOS;
    }
}
