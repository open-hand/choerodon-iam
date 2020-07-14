package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Set;

/**
 * 用户在项目下的角色标签
 *
 * @author zmf
 * @since 2020/6/11
 */
public class UserProjectLabelVO {
    @ApiModelProperty("项目id")
    @Encrypt
    private Long projectId;
    @ApiModelProperty("用户id")
    @Encrypt
    private Long UserId;
    @ApiModelProperty("角色标签")
    private Set<String> roleLabels;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public Set<String> getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(Set<String> roleLabels) {
        this.roleLabels = roleLabels;
    }
}
