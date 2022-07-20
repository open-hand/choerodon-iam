package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author zhaotianxin
 * @date 2021-11-08 17:15
 */
public class MoveWorkGroupVO {
    @Encrypt(ignoreValue = {"0"})
    @ApiModelProperty(value = "父级id")
    private Long parentId;

    @Encrypt
    @ApiModelProperty(value = "工作组id")
    private Long workGroupId;
    @ApiModelProperty(value = "是否在之前")
    private Boolean before;

    @Encrypt(ignoreValue = {"0"})
    @ApiModelProperty(value = "outSetId")
    private Long  outSetId;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Boolean getBefore() {
        return before;
    }

    public void setBefore(Boolean before) {
        this.before = before;
    }

    public Long getOutSetId() {
        return outSetId;
    }

    public void setOutSetId(Long outSetId) {
        this.outSetId = outSetId;
    }
}
