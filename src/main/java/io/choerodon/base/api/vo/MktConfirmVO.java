package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Eugen
 * <p>
 * 此VO用于市场发布的发布新版本
 */
public class MktConfirmVO {
    @ApiModelProperty(value = "市场发布主键（输出/输入）")
    private Long id;

    @ApiModelProperty(value = "市场发布名称（输出）")
    private String name;

    @ApiModelProperty(value = "市场发布版本主键（输出）")
    private Long latestVersionId;

    @ApiModelProperty(value = "市场发布版本名称（输出）")
    private String version;

    @ApiModelProperty(value = "包含的应用服务（输出）")
    private List<AppServiceDetailsVO> appServiceDetailsVOS;

    @ApiModelProperty(value = "应用类型（输出）")
    private String categoryName;

    @ApiModelProperty(value = "应用概述（输出/输入）")
    @NotEmpty(message = "error.mkt.publish.application.overview.can.not.be.empty")
    private String overview;

    @ApiModelProperty("市场应用描述（输出/输入）")
    @NotEmpty(message = "error.mkt.publish.application.description.can.not.be.empty")
    @Size(max = 250, message = "error.mkt.publish.application.description.size")
    private String description;

    @ApiModelProperty("版本日志（输入/输出）")
    @NotEmpty(message = "error.mkt.publish.version.info.changelog.can.not.be.empty")
    private String changelog;

    @ApiModelProperty("文档（输入/输出）")
    @NotEmpty(message = "error.mkt.publish.version.info.document.can.not.be.empty")
    private String document;

    @ApiModelProperty("乐观所版本号（输入/输出）")
    private Long objectVersionNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getLatestVersionId() {
        return latestVersionId;
    }

    public void setLatestVersionId(Long latestVersionId) {
        this.latestVersionId = latestVersionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<AppServiceDetailsVO> getAppServiceDetailsVOS() {
        return appServiceDetailsVOS;
    }

    public void setAppServiceDetailsVOS(List<AppServiceDetailsVO> appServiceDetailsVOS) {
        this.appServiceDetailsVOS = appServiceDetailsVOS;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
