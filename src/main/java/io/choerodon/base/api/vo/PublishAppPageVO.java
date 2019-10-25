package io.choerodon.base.api.vo;

import io.choerodon.base.infra.enums.PublishAppVersionStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * @author pengyuhua
 */
@ApiModel(value = "市场发布列表对象", description = "该对象用于市场发布页的列表展示")
public class PublishAppPageVO {


    //——————————市场应用相关——————————

    @ApiModelProperty("市场应用主键")
    private Long id;
    @ApiModelProperty("关联应用主键")
    private Long refAppId;
    @ApiModelProperty("市场应用名称")
    private String name;
    @ApiModelProperty("是否免费")
    private Boolean free;
    @ApiModelProperty("应用来源")
    private String sourceAppName;
    private Date creationDate;
    /**
     * {@link io.choerodon.base.infra.enums.PublishTypeEnum}
     */
    @ApiModelProperty("发布类型")
    private String publishType;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("应用是否可编辑")
    private Boolean appEditable;
    @ApiModelProperty("调用应用修改接口辨别（已发布/未发布应用）")
    private Boolean editReleased;

    //——————————市场应用版本相关（市场应用下最新发布版本）——————————

    /**
     * {@link PublishAppVersionStatusEnum}
     */
    @ApiModelProperty("市场应用版本状态")
    private String status;
    @ApiModelProperty("最新版本")
    private String latestVersion;

    private Boolean editableByStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRefAppId() {
        return refAppId;
    }

    public void setRefAppId(Long refAppId) {
        this.refAppId = refAppId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getFree() {
        return free;
    }

    public void setFree(Boolean free) {
        this.free = free;
    }

    public String getPublishType() {
        return publishType;
    }

    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getSourceAppName() {
        return sourceAppName;
    }

    public void setSourceAppName(String sourceAppName) {
        this.sourceAppName = sourceAppName;
    }

    public Boolean getAppEditable() {
        return appEditable;
    }

    public PublishAppPageVO setAppEditable(Boolean appEditable) {
        this.appEditable = appEditable;
        return this;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public PublishAppPageVO setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public Boolean getEditReleased() {
        return editReleased;
    }

    public PublishAppPageVO setEditReleased(Boolean editReleased) {
        this.editReleased = editReleased;
        return this;
    }

    public Boolean getEditableByStatus() {
        return editableByStatus;
    }

    public PublishAppPageVO setEditableByStatus(Boolean editableByStatus) {
        this.editableByStatus = editableByStatus;
        return this;
    }
}
