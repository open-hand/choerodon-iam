package io.choerodon.iam.api.vo;

import java.util.Date;
import java.util.Set;

import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author scp
 * @date 2020/12/11
 * @description
 */
public class ProjectPermissionVO {
    @Encrypt
    private Set<Long> roleIds;
    @Encrypt
    private Long roleId;
    private Date startTime;
    private Date endTime;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Set<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Set<Long> roleIds) {
        this.roleIds = roleIds;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
