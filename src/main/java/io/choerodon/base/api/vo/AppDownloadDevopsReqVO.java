package io.choerodon.base.api.vo;

import java.util.Set;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/18
 */
public class AppDownloadDevopsReqVO {

    private Long serviceId;

    private Set<Long> serviceVersionIds;

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Set<Long> getServiceVersionIds() {
        return serviceVersionIds;
    }

    public void setServiceVersionIds(Set<Long> serviceVersionIds) {
        this.serviceVersionIds = serviceVersionIds;
    }
}
