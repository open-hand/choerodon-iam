package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Eugen
 * <p>
 * 此VO用于市场发布的发布新版本
 */
public class MktNewVersionVO {


    @ApiModelProperty(value = "市场发布主键(输出)")//发布新版本-产生应用的市场发布临时记录的主键
    private Long id;

    @ApiModelProperty(value = "市场发布名称（输出）")
    private String name;

    @ApiModelProperty(value = "通知邮箱（输入/输出）")
    @NotEmpty(message = "error.mkt.publish.application.notification.email.can.not.be.empty")
    @Email(message = "error.mkt.publish.application.notification.email.invalid")
    private String notificationEmail;

    @ApiModelProperty(value = "是否新建应用版本（输入）")
    @NotNull(message = "error.mkt.publish.application.create.whether.to.create.can.not.be.null")
    private Boolean whetherToCreate;

    @ApiModelProperty(value = "最新应用版本主键（输入/输出）")
    private Long latestVersionId;

    @ApiModelProperty(value = "选择新建应用版本（输入）")
    private ApplicationVersionQuickCreateVO createVersion;

    @ApiModelProperty("版本日志（输入/输出）")
    @NotEmpty(message = "error.mkt.publish.version.info.changelog.can.not.be.empty")
    private String changelog;

    @ApiModelProperty("文档（输入/输出）")
    @NotEmpty(message = "error.mkt.publish.version.info.document.can.not.be.empty")
    private String document;

    @ApiModelProperty("备注（输入/输出）")
    @Size(max = 250, message = "error.mkt.publish.application.remark.size")
    private String remark;


    public Long getId() {
        return id;
    }

    public MktNewVersionVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public void setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
    }

    public Boolean getWhetherToCreate() {
        return whetherToCreate;
    }

    public MktNewVersionVO setWhetherToCreate(Boolean whetherToCreate) {
        this.whetherToCreate = whetherToCreate;
        return this;
    }

    public Long getLatestVersionId() {
        return latestVersionId;
    }

    public void setLatestVersionId(Long latestVersionId) {
        this.latestVersionId = latestVersionId;
    }

    public ApplicationVersionQuickCreateVO getCreateVersion() {
        return createVersion;
    }

    public MktNewVersionVO setCreateVersion(ApplicationVersionQuickCreateVO createVersion) {
        this.createVersion = createVersion;
        return this;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
