package io.choerodon.base.infra.dto;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;

@Table(name = "FD_ROUTE")
public class RouteDTO extends BaseDTO {
    @Id
    @GeneratedValue
    private Long id;
    private String serviceCode;
    private String backendPath;
    private String frontendPath;

    @ApiModelProperty(value = "路由唯一标识/必填")
    private String routeId;

    @ApiModelProperty(value = "是否去除前缀/非必填")
    private Boolean stripPrefix;

    @ApiModelProperty(value = "是否过滤敏感头信息/非必填")
    private String sensitiveHeaders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getBackendPath() {
        return backendPath;
    }

    public void setBackendPath(String backendPath) {
        this.backendPath = backendPath;
    }

    public String getFrontendPath() {
        return frontendPath;
    }

    public void setFrontendPath(String frontendPath) {
        this.frontendPath = frontendPath;
    }

    public Boolean getStripPrefix() {
        return stripPrefix;
    }

    public void setStripPrefix(Boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    public String getSensitiveHeaders() {
        return sensitiveHeaders;
    }

    public void setSensitiveHeaders(String sensitiveHeaders) {
        this.sensitiveHeaders = sensitiveHeaders;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
}
