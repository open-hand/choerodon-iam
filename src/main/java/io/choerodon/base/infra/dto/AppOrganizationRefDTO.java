package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Table;

/**
 * @author zongw.lee@gmail.com
 * @since 2019/9/21
 */
@Table(name = "mkt_app_organization_ref")
public class AppOrganizationRefDTO {

    @ApiModelProperty(value = "本地应用Id")
    private Long appId;

    @ApiModelProperty(value = "本地应用版本Id")
    private Long appVersionId;

    @ApiModelProperty(value = "组织ID")
    private Long organizationId;

    @ApiModelProperty(value = "市场应用版本Id")
    private Long mktVersionId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public Long getAppVersionId() {
        return appVersionId;
    }

    public void setAppVersionId(Long appVersionId) {
        this.appVersionId = appVersionId;
    }

    public Long getMktVersionId() {
        return mktVersionId;
    }

    public void setMktVersionId(Long mktVersionId) {
        this.mktVersionId = mktVersionId;
    }
}
