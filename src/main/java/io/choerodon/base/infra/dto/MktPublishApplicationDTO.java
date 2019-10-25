package io.choerodon.base.infra.dto;

import io.choerodon.base.api.validator.Insert;
import io.choerodon.base.api.validator.PublishedAppUpdate;
import io.choerodon.base.api.validator.UnpublishAppUpdate;
import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import javax.validation.constraints.*;

import static io.choerodon.base.infra.utils.RegularExpression.*;

/**
 * @author Eugen
 * @since 2019-09-10
 */
@Table(name = "mkt_publish_application")
public class MktPublishApplicationDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "市场发布名称")
    @NotEmpty(message = "error.mkt.publish.application.name.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class})
    @Pattern(regexp = CHINESE_AND_ALPHANUMERIC_AND_SPACE_SYMBOLS_30, message = "error.mkt.publish.application.name.invalid", groups = {Insert.class, UnpublishAppUpdate.class})
    private String name;

    @ApiModelProperty(value = "关联应用Id（fd_application的主键）")
    @NotNull(message = "error.mkt.publish.application.ref.app.id.can.not.be.null", groups = {Insert.class})
    private Long refAppId;

    @Column(name = "is_released")
    @ApiModelProperty(value = "是否已发布")
    private Boolean released;

    @ApiModelProperty("市场应用描述")
    @NotEmpty(message = "error.mkt.publish.application.description.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class, PublishedAppUpdate.class})
    @Size(max = 250, message = "error.mkt.publish.application.description.size", groups = {Insert.class, UnpublishAppUpdate.class, PublishedAppUpdate.class})
    private String description;

    @ApiModelProperty(value = "市场应用图标URL")
    @NotEmpty(message = "error.mkt.publish.application.image.url.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class, PublishedAppUpdate.class})
    private String imageUrl;

    @ApiModelProperty(value = "贡献者")
    @NotEmpty(message = "error.mkt.publish.application.contributor.can.not.be.empty", groups = {Insert.class})
    @Pattern(regexp = CHINESE_AND_ALPHANUMERIC_50, message = "error.mkt.publish.application.contributor.invalid", groups = {Insert.class})
    private String contributor;

    @ApiModelProperty(value = "通知邮箱")
    @NotEmpty(message = "error.mkt.publish.application.notification.email.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class})
    @Email(message = "error.mkt.publish.application.notification.email.invalid", groups = {Insert.class})
    @Email(regexp = EMAIL_COMMON, message = "error.mkt.publish.application.notification.email.invalid", groups = {UnpublishAppUpdate.class})
    private String notificationEmail;

    @ApiModelProperty("发布类型")
    @NotEmpty(message = "error.mkt.publish.application.publish.type.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class})
    private String publishType;

    @Column(name = "is_free")
    @ApiModelProperty(value = "是否是免费应用")
    @NotNull(message = "error.mkt.publish.application.free.can.not.be.null", groups = {Insert.class, UnpublishAppUpdate.class})
    private Boolean free;

    @ApiModelProperty("市场应用详细介绍")
    @NotEmpty(message = "error.mkt.publish.application.update.overview.cannot.be.empty", groups = {PublishedAppUpdate.class})
    private String overview;

    @ApiModelProperty("市场应用类别编码")
    private String categoryCode;

    @ApiModelProperty("市场应用类别名称")
    @NotEmpty(message = "error.mkt.publish.application.category.name.can.not.be.empty", groups = {Insert.class, UnpublishAppUpdate.class})
    @Pattern(regexp = CHINESE_AND_ALPHANUMERIC_AND_SPACE_30, message = "error.mkt.publish.application.category.name.invalid", groups = {Insert.class, UnpublishAppUpdate.class})
    private String categoryName;

    @ApiModelProperty(value = "最新应用版本主键")
    private Long latestVersionId;

    public Long getId() {
        return id;
    }

    public MktPublishApplicationDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MktPublishApplicationDTO setName(String name) {
        this.name = name;
        return this;
    }

    public Long getRefAppId() {
        return refAppId;
    }

    public MktPublishApplicationDTO setRefAppId(Long refAppId) {
        this.refAppId = refAppId;
        return this;
    }

    public Boolean getReleased() {
        return released;
    }

    public MktPublishApplicationDTO setReleased(Boolean released) {
        this.released = released;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MktPublishApplicationDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MktPublishApplicationDTO setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public String getContributor() {
        return contributor;
    }

    public MktPublishApplicationDTO setContributor(String contributor) {
        this.contributor = contributor;
        return this;
    }

    public String getNotificationEmail() {
        return notificationEmail;
    }

    public MktPublishApplicationDTO setNotificationEmail(String notificationEmail) {
        this.notificationEmail = notificationEmail;
        return this;
    }

    public String getPublishType() {
        return publishType;
    }

    public MktPublishApplicationDTO setPublishType(String publishType) {
        this.publishType = publishType;
        return this;
    }

    public Boolean getFree() {
        return free;
    }

    public MktPublishApplicationDTO setFree(Boolean free) {
        this.free = free;
        return this;
    }

    public String getOverview() {
        return overview;
    }

    public MktPublishApplicationDTO setOverview(String overview) {
        this.overview = overview;
        return this;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public MktPublishApplicationDTO setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public MktPublishApplicationDTO setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Long getLatestVersionId() {
        return latestVersionId;
    }

    public MktPublishApplicationDTO setLatestVersionId(Long latestVersionId) {
        this.latestVersionId = latestVersionId;
        return this;
    }
}
