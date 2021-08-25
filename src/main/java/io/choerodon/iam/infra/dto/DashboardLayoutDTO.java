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
@Table(name = "fd_dashboard_layout")
public class DashboardLayoutDTO extends AuditDomain {

    public static final String FIELD_LAYOUT_ID = "layoutId";
    public static final String FIELD_USER_ID = "userId";
    public static final String FIELD_DASHBOARD_ID = "dashboardId";
    public static final String FIELD_CARD_ID = "cardId";
    public static final String FIELD_W = "w";
    public static final String FIELD_H = "h";
    public static final String FIELD_X = "x";
    public static final String FIELD_Y = "y";
    public static final String FD_DASHBOARD_LAYOUT_U1 = "fd_dashboard_layout_u1";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("卡片布局ID")
    @Id
    @GeneratedValue
    @Encrypt
    private Long layoutId;
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "面板ID", required = true)
    @NotNull
    @Unique(FD_DASHBOARD_LAYOUT_U1)
    private Long dashboardId;
    @ApiModelProperty(value = "卡片ID", required = true)
    @NotNull
    @Unique(FD_DASHBOARD_LAYOUT_U1)
    private Long cardId;
    @ApiModelProperty(value = "卡片宽", required = true)
    @NotNull
    private Long w;
    @ApiModelProperty(value = "卡片高", required = true)
    @NotNull
    private Long h;
    @ApiModelProperty(value = "卡片位置x", required = true)
    @NotNull
    private Long x;
    @ApiModelProperty(value = "卡片位置y", required = true)
    @NotNull
    private Long y;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 卡片布局ID
     */
	public Long getLayoutId() {
		return layoutId;
	}

	public DashboardLayoutDTO setLayoutId(Long layoutId) {
		this.layoutId = layoutId;
        return this;
	}
    /**
     * @return 用户ID
     */
	public Long getUserId() {
		return userId;
	}

	public DashboardLayoutDTO setUserId(Long userId) {
		this.userId = userId;
        return this;
	}
    /**
     * @return 面板ID
     */
	public Long getDashboardId() {
		return dashboardId;
	}

	public DashboardLayoutDTO setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
        return this;
	}
    /**
     * @return 卡片ID
     */
	public Long getCardId() {
		return cardId;
	}

	public DashboardLayoutDTO setCardId(Long cardId) {
		this.cardId = cardId;
        return this;
	}
    /**
     * @return 卡片宽
     */
	public Long getW() {
		return w;
	}

	public DashboardLayoutDTO setW(Long w) {
		this.w = w;
        return this;
	}
    /**
     * @return 卡片高
     */
	public Long getH() {
		return h;
	}

	public DashboardLayoutDTO setH(Long h) {
		this.h = h;
        return this;
	}
    /**
     * @return 卡片位置x
     */
	public Long getX() {
		return x;
	}

	public DashboardLayoutDTO setX(Long x) {
		this.x = x;
        return this;
	}
    /**
     * @return 卡片位置y
     */
	public Long getY() {
		return y;
	}

	public DashboardLayoutDTO setY(Long y) {
		this.y = y;
        return this;
	}

}

