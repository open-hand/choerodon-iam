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
@Table(name = "fd_application_service_ref")
public class ApplicationServiceRefDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "应用主键")
    private Long applicationId;

    @ApiModelProperty(value = "服务主键")
    private Long serviceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public ApplicationServiceRefDTO setServiceId(Long serviceId) {
        this.serviceId = serviceId;
        return this;
    }
}
