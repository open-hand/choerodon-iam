package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Eugen
 * @since 2019-09-10
 */
@Table(name = "fd_application_svc_version_ref")
public class ApplicationSvcVersionRefDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "应用版本主键")
    private Long applicationVersionId;

    @ApiModelProperty(value = "服务版本主键")
    private Long serviceVersionId;

    @ApiModelProperty(value = "发布状态")
    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationVersionId() {
        return applicationVersionId;
    }

    public ApplicationSvcVersionRefDTO setApplicationVersionId(Long applicationVersionId) {
        this.applicationVersionId = applicationVersionId;
        return this;
    }

    public Long getServiceVersionId() {
        return serviceVersionId;
    }

    public ApplicationSvcVersionRefDTO setServiceVersionId(Long serviceVersionId) {
        this.serviceVersionId = serviceVersionId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public ApplicationSvcVersionRefDTO setStatus(String status) {
        this.status = status;
        return this;
    }
}
