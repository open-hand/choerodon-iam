package io.choerodon.base.api.vo;

import java.util.Set;

/**
 * @author wanghao
 * @Date 2019/9/9 16:06
 */
public class AppVersionVO {
    private Long versionId;
    private Set<Long> serviceVersionIds;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Set<Long> getServiceVersionIds() {
        return serviceVersionIds;
    }

    public void setServiceVersionIds(Set<Long> serviceVersionIds) {
        this.serviceVersionIds = serviceVersionIds;
    }
}
