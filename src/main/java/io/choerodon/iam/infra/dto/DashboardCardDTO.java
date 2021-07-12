package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.NotBlank;
import io.choerodon.mybatis.domain.AuditDomain;
import org.hzero.mybatis.annotation.Unique;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 
 *
 * @author jian.zhang02@hand-china.com 2021-07-08 10:05:56
 */
@ApiModel("")
@VersionAudit
@ModifyAudit
@Table(name = "fd_dashboard_card")
public class DashboardCardDTO extends AuditDomain {

    public static final String FIELD_CARD_ID = "cardId";
    public static final String FIELD_FD_LEVEL = "fdLevel";
    public static final String FIELD_CARD_CODE = "cardCode";
    public static final String FIELD_CARD_NAME = "cardName";
    public static final String FIELD_W = "w";
    public static final String FIELD_H = "h";
    public static final String FIELD_MIN_W = "minW";
    public static final String FIELD_MIN_H = "minH";
    public static final String FIELD_MAX_W = "maxW";
    public static final String FIELD_MAX_H = "maxH";
    public static final String FD_DASHBOARD_CARD_U1 = "fd_dashboard_card_u1";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @ApiModelProperty("卡片ID")
    @Id
    @GeneratedValue
    private Long cardId;
    @ApiModelProperty(value = "层级(SITE/平台层,ORGANIZATION/组织层,PROJECT/项目层)", required = true)
    @NotBlank
    private String fdLevel;
	@ApiModelProperty(value = "分类")
	private String groupId;
    @ApiModelProperty(value = "卡片编码", required = true)
    @NotBlank
    @Unique(FD_DASHBOARD_CARD_U1)
    private String cardCode;
    @ApiModelProperty(value = "卡片名称", required = true)
    @NotBlank
    private String cardName;
	@ApiModelProperty(value = "关联的服务")
	private String linkService;
	@ApiModelProperty(value = "卡片默认宽", required = true)
    @NotNull
    private Long w;
    @ApiModelProperty(value = "卡盘默认高", required = true)
    @NotNull
    private Long h;
    @ApiModelProperty(value = "卡片最小宽")
    private Long minW;
    @ApiModelProperty(value = "卡片最小高")
    private Long minH;
    @ApiModelProperty(value = "卡片最大宽")
    private Long maxW;
    @ApiModelProperty(value = "卡片最大高")
    private Long maxH;

	//
    // 非数据库字段
    // ------------------------------------------------------------------------------
    //
    // getter/setter
    // ------------------------------------------------------------------------------

    /**
     * @return 卡片ID
     */
	public Long getCardId() {
		return cardId;
	}

	public DashboardCardDTO setCardId(Long cardId) {
		this.cardId = cardId;
        return this;
	}
    /**
     * @return 层级(SITE/平台层,ORGANIZATION/组织层,PROJECT/项目层)
     */
	public String getFdLevel() {
		return fdLevel;
	}

	public DashboardCardDTO setFdLevel(String fdLevel) {
		this.fdLevel = fdLevel;
        return this;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	/**
     * @return 卡片编码
     */
	public String getCardCode() {
		return cardCode;
	}

	public DashboardCardDTO setCardCode(String cardCode) {
		this.cardCode = cardCode;
        return this;
	}
    /**
     * @return 卡片名称
     */
	public String getCardName() {
		return cardName;
	}

	public DashboardCardDTO setCardName(String cardName) {
		this.cardName = cardName;
        return this;
	}

	public String getLinkService() {
		return linkService;
	}

	public void setLinkService(String linkService) {
		this.linkService = linkService;
	}

	/**
     * @return 卡片默认宽
     */
	public Long getW() {
		return w;
	}

	public DashboardCardDTO setW(Long w) {
		this.w = w;
        return this;
	}
    /**
     * @return 卡盘默认高
     */
	public Long getH() {
		return h;
	}

	public DashboardCardDTO setH(Long h) {
		this.h = h;
        return this;
	}
    /**
     * @return 卡片最小宽
     */
	public Long getMinW() {
		return minW;
	}

	public DashboardCardDTO setMinW(Long minW) {
		this.minW = minW;
        return this;
	}
    /**
     * @return 卡片最小高
     */
	public Long getMinH() {
		return minH;
	}

	public DashboardCardDTO setMinH(Long minH) {
		this.minH = minH;
        return this;
	}
    /**
     * @return 卡片最大宽
     */
	public Long getMaxW() {
		return maxW;
	}

	public DashboardCardDTO setMaxW(Long maxW) {
		this.maxW = maxW;
        return this;
	}
    /**
     * @return 卡片最大高
     */
	public Long getMaxH() {
		return maxH;
	}

	public DashboardCardDTO setMaxH(Long maxH) {
		this.maxH = maxH;
        return this;
	}

}

