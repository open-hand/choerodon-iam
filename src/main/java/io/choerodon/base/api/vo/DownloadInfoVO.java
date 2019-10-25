package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

import io.choerodon.base.infra.dto.mkt.RobotUser;

/**
 * @author wanghao
 * @Date 2019/8/21 18:46
 */
public class DownloadInfoVO {
    private Long id;

    @ApiModelProperty("应用的code")
    private String marketAppCode;

    @ApiModelProperty("应用的版本")
    private String version;

    @ApiModelProperty("版本日志")
    private String changelog;

    @ApiModelProperty("文档")
    private String document;

    @ApiModelProperty("审批状态，默认审批中doing, success, failed")
    private String approveStatus;

    @ApiModelProperty("审批返回信息")
    private String approveMessage;

    @ApiModelProperty("版本创建时间")
    private Date versionCreationDate;
    @ApiModelProperty("版本发布时间")
    private Date publishDate;

    private RobotUser user;

    private MarketPublishApplicationVO marketPublishApplicationVO;

    private List<MarketServiceVO> marketServiceVOS;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarketAppCode() {
        return marketAppCode;
    }

    public void setMarketAppCode(String marketAppCode) {
        this.marketAppCode = marketAppCode;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChangelog() {
        return changelog;
    }

    public void setChangelog(String changelog) {
        this.changelog = changelog;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(String approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveMessage() {
        return approveMessage;
    }

    public void setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
    }

    public Date getVersionCreationDate() {
        return versionCreationDate;
    }

    public void setVersionCreationDate(Date versionCreationDate) {
        this.versionCreationDate = versionCreationDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public MarketPublishApplicationVO getMarketPublishApplicationVO() {
        return marketPublishApplicationVO;
    }

    public void setMarketPublishApplicationVO(MarketPublishApplicationVO marketPublishApplicationVO) {
        this.marketPublishApplicationVO = marketPublishApplicationVO;
    }

    public List<MarketServiceVO> getMarketServiceVOS() {
        return marketServiceVOS;
    }

    public void setMarketServiceVOS(List<MarketServiceVO> marketServiceVOS) {
        this.marketServiceVOS = marketServiceVOS;
    }

    public RobotUser getUser() {
        return user;
    }

    public void setUser(RobotUser user) {
        this.user = user;
    }
}
