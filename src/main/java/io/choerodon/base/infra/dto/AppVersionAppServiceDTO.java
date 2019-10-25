package io.choerodon.base.infra.dto;

import javax.persistence.Table;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/26
 */
@Table(name = "fd_app_version_service_version")
public class AppVersionAppServiceDTO {

    private Long versionId;
    private Long serviceVersionId;

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Long getServiceVersionId() {
        return serviceVersionId;
    }

    public void setServiceVersionId(Long serviceVersionId) {
        this.serviceVersionId = serviceVersionId;
    }
}