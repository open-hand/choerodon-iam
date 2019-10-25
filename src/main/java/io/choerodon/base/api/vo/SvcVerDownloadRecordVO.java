package io.choerodon.base.api.vo;

import java.util.List;

/**
 * @author wanghao
 * @Date 2019/9/10 9:29
 */
public class SvcVerDownloadRecordVO {
    private Long versionId;
    private List<Long> svcVers;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public List<Long> getSvcVers() {
        return svcVers;
    }

    public void setSvcVers(List<Long> svcVers) {
        this.svcVers = svcVers;
    }
}
