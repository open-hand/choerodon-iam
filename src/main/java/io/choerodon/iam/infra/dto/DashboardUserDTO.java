package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.mybatis.annotation.Unique;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

/**
 * 
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@ApiModel("")
@VersionAudit
@ModifyAudit
@Table(name = "fd_dashboard_user")
public class DashboardUserDTO extends AuditDomain {

    public static final String FIELD_DASHBOARD_USER_ID = "dashboardUserId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_DASHBOARD_ID = "dashboardId";
    public static final String FIELD_RANK = "rank";
    public static final String FD_DASHBOARD_USER_U1 = "fd_dashboard_user_u1";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("面板用户ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long dashboardUserId;
    @ApiModelProperty(value = "用户ID")
    @Unique(FD_DASHBOARD_USER_U1)
    private Long userId;
    @ApiModelProperty(value = "面板ID", required = true)
    @NotNull
    @Unique(FD_DASHBOARD_USER_U1)
    @Encrypt
    private Long dashboardId;
    @ApiModelProperty(value = "序号")
    private Integer rank;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 面板用户ID
     */
	public Long getDashboardUserId() {
		return dashboardUserId;
	}

	public DashboardUserDTO setDashboardUserId(Long dashboardUserId) {
		this.dashboardUserId = dashboardUserId;
        return this;
	}
    /**
     * @return 用户ID
     */
	public Long getUserId() {
		return userId;
	}

	public DashboardUserDTO setUserId(Long userId) {
		this.userId = userId;
        return this;
	}
    /**
     * @return 面板ID
     */
	public Long getDashboardId() {
		return dashboardId;
	}

	public DashboardUserDTO setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
        return this;
	}

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }
}

