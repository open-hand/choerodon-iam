package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

/**
 * 市场发布修改版本信息VOs
 *
 * @author pengyuhua
 * @date 2019/9/17
 */
public class MarketPublishVersionInfoVO {
    // 版本相关

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "应用版本ID")
    private Long applicationVersionId;


    @ApiModelProperty(value = "市场应用ID")
    private Long publishApplicationId;

    @ApiModelProperty(value = "发布状态")
    private String status;

    @ApiModelProperty("版本日志")
    private String changelog;

    @ApiModelProperty("文档")
    @NotEmpty(message = "")
    private String document;

    @ApiModelProperty("审批信息")
    private String approveMessage;

    @ApiModelProperty("版本的发布时间")
    private Date publishDate;

    @ApiModelProperty("发布错误编码")
    private String publishErrorCode;


    // 应用相关

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getApplicationVersionId() {
        return applicationVersionId;
    }

    public void setApplicationVersionId(Long applicationVersionId) {
        this.applicationVersionId = applicationVersionId;
    }

    public Long getPublishApplicationId() {
        return publishApplicationId;
    }

    public MarketPublishVersionInfoVO setPublishApplicationId(Long publishApplicationId) {
        this.publishApplicationId = publishApplicationId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getApproveMessage() {
        return approveMessage;
    }

    public void setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishErrorCode() {
        return publishErrorCode;
    }

    public void setPublishErrorCode(String publishErrorCode) {
        this.publishErrorCode = publishErrorCode;
    }
}
