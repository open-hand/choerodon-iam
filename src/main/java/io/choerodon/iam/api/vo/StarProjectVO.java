package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.iam.infra.dto.ProjectDTO;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 11:14
 */
public class StarProjectVO {

    @Encrypt
    private Long id;

    @ApiModelProperty("项目id/新增时必须")
    @Encrypt
    private Long projectId;

    @ApiModelProperty("用户id")
    @Encrypt
    private Long userId;

    private ProjectDTO projectDTO;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectDTO getProjectDTO() {
        return projectDTO;
    }

    public void setProjectDTO(ProjectDTO projectDTO) {
        this.projectDTO = projectDTO;
    }
}
