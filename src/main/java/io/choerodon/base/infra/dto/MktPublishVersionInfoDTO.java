package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author Eugen
 * @since 2019-09-10
 */
@Table(name = "mkt_publish_version_info")
public class MktPublishVersionInfoDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "应用版本ID")
    private Long applicationVersionId;


    @ApiModelProperty(value = "市场应用ID")
    private Long publishApplicationId;

    @ApiModelProperty(value = "发布状态")
    private String status;

    @ApiModelProperty("版本申请备注")
    private String remark;

    @ApiModelProperty(value = "发布修复版本的次数")
    private Integer timesOfFixes;

    @ApiModelProperty("版本日志")
    private String changelog;

    @ApiModelProperty("文档")
    private String document;

    @ApiModelProperty("审批信息")
    private String approveMessage;

    @ApiModelProperty("版本的发布时间")
    private Date publishDate;

    @ApiModelProperty("发布错误编码")
    private String publishErrorCode;

    public Long getId() {
        return id;
    }

    public MktPublishVersionInfoDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getApplicationVersionId() {
        return applicationVersionId;
    }

    public MktPublishVersionInfoDTO setApplicationVersionId(Long applicationVersionId) {
        this.applicationVersionId = applicationVersionId;
        return this;
    }

    public Long getPublishApplicationId() {
        return publishApplicationId;
    }

    public MktPublishVersionInfoDTO setPublishApplicationId(Long publishApplicationId) {
        this.publishApplicationId = publishApplicationId;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public MktPublishVersionInfoDTO setStatus(String status) {
        this.status = status;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public MktPublishVersionInfoDTO setRemark(String remark) {
        this.remark = remark;
        return this;
    }

    public Integer getTimesOfFixes() {
        return timesOfFixes;
    }

    public MktPublishVersionInfoDTO setTimesOfFixes(Integer timesOfFixes) {
        this.timesOfFixes = timesOfFixes;
        return this;
    }

    public String getChangelog() {
        return changelog;
    }

    public MktPublishVersionInfoDTO setChangelog(String changelog) {
        this.changelog = changelog;
        return this;
    }

    public String getDocument() {
        return document;
    }

    public MktPublishVersionInfoDTO setDocument(String document) {
        this.document = document;
        return this;
    }

    public String getApproveMessage() {
        return approveMessage;
    }

    public MktPublishVersionInfoDTO setApproveMessage(String approveMessage) {
        this.approveMessage = approveMessage;
        return this;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public MktPublishVersionInfoDTO setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
        return this;
    }

    public String getPublishErrorCode() {
        return publishErrorCode;
    }

    public MktPublishVersionInfoDTO setPublishErrorCode(String publishErrorCode) {
        this.publishErrorCode = publishErrorCode;
        return this;
    }
}
