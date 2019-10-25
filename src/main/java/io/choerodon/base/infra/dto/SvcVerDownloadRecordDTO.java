package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/6
 */
@Table(name = "mkt_svc_ver_download_record")
public class SvcVerDownloadRecordDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "市场应用版本Id")
    private Long mktVersionId;

    @ApiModelProperty(value = "市场应用下服务版本Id")
    private Long mktSvcVersionId;

    @ApiModelProperty(value = "下载状态")
    private String status;

    @ApiModelProperty(value = "组织ID")
    private Long organizationId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMktVersionId() {
        return mktVersionId;
    }

    public void setMktVersionId(Long mktVersionId) {
        this.mktVersionId = mktVersionId;
    }

    public Long getMktSvcVersionId() {
        return mktSvcVersionId;
    }

    public void setMktSvcVersionId(Long mktSvcVersionId) {
        this.mktSvcVersionId = mktSvcVersionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}