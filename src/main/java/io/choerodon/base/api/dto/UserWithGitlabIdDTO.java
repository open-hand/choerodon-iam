package io.choerodon.base.api.dto;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.base.infra.dto.UserDTO;

/**
 * @author zmf
 * @since 20-3-18
 */
public class UserWithGitlabIdDTO extends UserDTO {
    @ApiModelProperty("用户对应的gitlab用户id")
    private Long gitlabUserId;

    public Long getGitlabUserId() {
        return gitlabUserId;
    }

    public void setGitlabUserId(Long gitlabUserId) {
        this.gitlabUserId = gitlabUserId;
    }
}
