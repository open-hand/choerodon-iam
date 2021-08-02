package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;

public class DashboardLayoutVO {
    private Long layoutId;
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "面板ID")
    private Long dashboardId;
    @ApiModelProperty(value = "卡片ID")
    private Long cardId;
    @ApiModelProperty(value = "卡片宽")
    private Long w;
    @ApiModelProperty(value = "卡片高")
    private Long h;
    @ApiModelProperty(value = "卡片位置x")
    private Long x;
    @ApiModelProperty(value = "卡片位置y")
    private Long y;
    @ApiModelProperty(value = "卡片编码")
    private String cardCode;
    @ApiModelProperty(value = "卡片名称")
    private String cardName;
    @ApiModelProperty(value = "卡片最小宽")
    private Long minW;
    @ApiModelProperty(value = "卡片最小高")
    private Long minH;
    @ApiModelProperty(value = "卡片最大宽")
    private Long maxW;
    @ApiModelProperty(value = "卡片最大高")
    private Long maxH;
    @ApiModelProperty(value = "分类")
    private String groupId;

    public Long getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(Long layoutId) {
        this.layoutId = layoutId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Long dashboardId) {
        this.dashboardId = dashboardId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public Long getW() {
        return w;
    }

    public void setW(Long w) {
        this.w = w;
    }

    public Long getH() {
        return h;
    }

    public void setH(Long h) {
        this.h = h;
    }

    public Long getX() {
        return x;
    }

    public void setX(Long x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }

    public String getCardCode() {
        return cardCode;
    }

    public void setCardCode(String cardCode) {
        this.cardCode = cardCode;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public Long getMinW() {
        return minW;
    }

    public void setMinW(Long minW) {
        this.minW = minW;
    }

    public Long getMinH() {
        return minH;
    }

    public void setMinH(Long minH) {
        this.minH = minH;
    }

    public Long getMaxW() {
        return maxW;
    }

    public void setMaxW(Long maxW) {
        this.maxW = maxW;
    }

    public Long getMaxH() {
        return maxH;
    }

    public void setMaxH(Long maxH) {
        this.maxH = maxH;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
