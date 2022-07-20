package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-11-08 15:31
 */
public class WorkGroupTreeVO {
    @ApiModelProperty(value = "根节点id集合")
   @Encrypt
   private List<Long> rootIds;

    @ApiModelProperty(value = "工作组集合")
   List<WorkGroupVO> workGroupVOS;

    public List<Long> getRootIds() {
        return rootIds;
    }

    public void setRootIds(List<Long> rootIds) {
        this.rootIds = rootIds;
    }

    public List<WorkGroupVO> getWorkGroupVOS() {
        return workGroupVOS;
    }

    public void setWorkGroupVOS(List<WorkGroupVO> workGroupVOS) {
        this.workGroupVOS = workGroupVOS;
    }
}
