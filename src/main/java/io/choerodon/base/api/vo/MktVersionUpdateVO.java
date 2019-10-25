package io.choerodon.base.api.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Eugen
 * <p>
 * 此VO用于市场发布的发布新版本
 */
public class MktVersionUpdateVO {
    @ApiModelProperty(value = "市场发布版本信息主键（输出/输入）")
    private Long id;

    @ApiModelProperty(value = "市场发布版本名称（输出）")
    private String version;

    @ApiModelProperty(value = "是否新建应用版本（输入）")
    @NotNull(message = "error.mkt.publish.version.info.whether.to.fix.can.not.be.null")
    private Boolean whetherToFix;

    @ApiModelProperty(value = "包含的应用服务（输出）")
    private List<AppServiceDetailsVO> appServiceDetailsVOS;

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getWhetherToFix() {
        return whetherToFix;
    }

    public MktVersionUpdateVO setWhetherToFix(Boolean whetherToFix) {
        this.whetherToFix = whetherToFix;
        return this;
    }

    public List<AppServiceDetailsVO> getAppServiceDetailsVOS() {
        return appServiceDetailsVOS;
    }

    public MktVersionUpdateVO setAppServiceDetailsVOS(List<AppServiceDetailsVO> appServiceDetailsVOS) {
        this.appServiceDetailsVOS = appServiceDetailsVOS;
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

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }
}
