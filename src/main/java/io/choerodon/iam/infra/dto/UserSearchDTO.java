package io.choerodon.iam.infra.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author superlee
 */
public class UserSearchDTO extends UserDTO {

    @ApiModelProperty(value = "其他参数")
    private String[] params;
    @ApiModelProperty(value = "角色名")
    private String roleName;
    @ApiModelProperty(value = "当前项目Id")
    private Long projectId;

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
