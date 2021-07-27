package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;

import io.choerodon.mybatis.domain.AuditDomain;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@ApiModel("")
@VersionAudit
@ModifyAudit
@Table(name = "fd_dashboard")
public class DashboardDTO extends AuditDomain {

    public static final String FIELD_DASHBOARD_ID = "dashboardId";
    public static final String FIELD_DASHBOARD_TYPE = "dashboardType";
    public static final String FIELD_DASHBOARD_NAME = "dashboardName";
    public static final String FIELD_DEFAULT_FLAG = "defaultFlag";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("面板ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long dashboardId;
    @ApiModelProperty(value = "面板类型(CUSTOMIZE/自定义;INTERNAL/内置)")
    private String dashboardType;
    @ApiModelProperty(value = "面板名称", required = true)
    @NotBlank
    private String dashboardName;
    @ApiModelProperty(value = "默认面板")
    private Integer defaultFlag;
    @Transient
    private Long dashboardUserId;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 面板ID
     */
    public Long getDashboardId() {
        return dashboardId;
    }

    public DashboardDTO setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
        return this;
    }

    /**
     * @return 面板类型(CUSTOMIZE / 自定义 ; INTERNAL / 内置)
     */
    public String getDashboardType() {
        return dashboardType;
    }

    public DashboardDTO setDashboardType(String dashboardType) {
        this.dashboardType = dashboardType;
        return this;
    }

    /**
     * @return 面板名称
     */
    public String getDashboardName() {
        return dashboardName;
    }

    public DashboardDTO setDashboardName(String dashboardName) {
        this.dashboardName = dashboardName;
        return this;
    }

    /**
     * @return 默认面板
     */
    public Integer getDefaultFlag() {
        return defaultFlag;
    }

    public DashboardDTO setDefaultFlag(Integer defaultFlag) {
        this.defaultFlag = defaultFlag;
        return this;
    }

    public Long getDashboardUserId() {
        return dashboardUserId;
    }

    public void setDashboardUserId(Long dashboardUserId) {
        this.dashboardUserId = dashboardUserId;
    }
}

