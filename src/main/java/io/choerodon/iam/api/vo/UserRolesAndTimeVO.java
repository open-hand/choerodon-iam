package io.choerodon.iam.api.vo;

import java.util.Date;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author scp
 * @since 2022/4/20
 */
public class UserRolesAndTimeVO {
    @Encrypt
    private Set<Long> roleIds;
    @ApiModelProperty(value = "进场时间")
    private Date scheduleEntryTime;
    @ApiModelProperty(value = "撤场时间")
    private Date scheduleExitTime;

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Date getScheduleEntryTime() {
        return scheduleEntryTime;
    }

    public void setScheduleEntryTime(Date scheduleEntryTime) {
        this.scheduleEntryTime = scheduleEntryTime;
    }

    public Date getScheduleExitTime() {
        return scheduleExitTime;
    }

    public void setScheduleExitTime(Date scheduleExitTime) {
        this.scheduleExitTime = scheduleExitTime;
    }
}
