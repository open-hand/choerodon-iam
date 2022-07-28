package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-11-08 20:41
 */
public class WorkGroupUserRelParamVO {

    @ApiModelProperty(value = "工作组id")
    @Encrypt
    private Long workGroupId;

    @ApiModelProperty(value = "用户id集合")
    @Encrypt
    private List<Long> userIds;

    @ApiModelProperty(value = "查询参数")
    private String param;

    @ApiModelProperty(value = "登录名")
    private String loginName;

    @ApiModelProperty(value = "真实名称")
    private String realName;

    @ApiModelProperty(value = "是否启用")
    private Boolean enabled;

    @ApiModelProperty(value = "工作组id集合")
    @Encrypt(ignoreValue = {"0"})
    private List<Long> workGroupIds;

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public List<Long> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<Long> userIds) {
        this.userIds = userIds;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

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

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Long> getWorkGroupIds() {
        return workGroupIds;
    }

    public void setWorkGroupIds(List<Long> workGroupIds) {
        this.workGroupIds = workGroupIds;
    }
}
