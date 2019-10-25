package io.choerodon.base.api.vo;

import io.choerodon.base.api.validator.UnPublishVersionUpdate;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author relaxingchu@qq.com
 *
 * 此VO用于修改未发布市场应用版本（修改）
 */
public class MktUnPublishVersionInfoVO {

    @ApiModelProperty("未发布版本Id")
    @NotNull(message = "error.publish.app.version.update.id.cannot.be.null", groups = {UnPublishVersionUpdate.class})
    private Long id;

    @ApiModelProperty(value = "更新应用ID")
    private Long applicationId;

    @ApiModelProperty(value = "市场应用图标URL")
    @NotEmpty(message = "error.unpublished.app.version.update.image.url.cannot.be.null", groups = {UnPublishVersionUpdate.class})
    private String imageUrl;

    @ApiModelProperty("市场应用描述")
    @NotEmpty(message = "error.unpublished.app.version.update.description.cannot.be.null", groups = {UnPublishVersionUpdate.class})
    private String description;

    @ApiModelProperty("市场应用详细介绍")
    private String overview;

    @ApiModelProperty("changeLog")
    private String changelog;

    @ApiModelProperty("文档")
    private String document;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty("乐观所版本号")
    @NotNull(message = "error.publish.app.version.update.object.version.number.cannot.be.null", groups = {UnPublishVersionUpdate.class})
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public MktUnPublishVersionInfoVO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MktUnPublishVersionInfoVO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MktUnPublishVersionInfoVO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MktUnPublishVersionInfoVO setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getChangelog() {
        return changelog;
    }

    public MktUnPublishVersionInfoVO setChangelog(String changelog) {
        this.changelog = changelog;
        return this;
    }

    public String getDocument() {
        return document;
    }

    public MktUnPublishVersionInfoVO setDocument(String document) {
        this.document = document;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public MktUnPublishVersionInfoVO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public MktUnPublishVersionInfoVO setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public MktUnPublishVersionInfoVO setRemark(String remark) {
        this.remark = remark;
        return this;
    }
}
