package io.choerodon.iam.api.vo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.iam.domain.entity.User;
import org.hzero.starter.keyencrypt.core.Encrypt;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2020/6/11 16:50
 */
public class QuickLinkVO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "主键ID")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "快速链接名字")
    @NotEmpty
    private String name;

    @ApiModelProperty(value = "快速链接地址")
    @NotEmpty
    private String linkUrl;

    @ApiModelProperty(value = "创建用户id")
    @Encrypt
    private Long createUserId;

    @ApiModelProperty(value = "共享项目id")
    private Long projectId;

    @ApiModelProperty(value = "共享范围")
    @NotEmpty
    private String scope;

    @ApiModelProperty(value = "创建用户")
    private User user;

    @ApiModelProperty(value = "是否可修改")
    private Boolean editFlag = false;

    @ApiModelProperty(value = "是否置顶")
    private Boolean top;

    private String projectName;

    private String projectImage;

    public String getProjectImage() {
        return projectImage;
    }

    public void setProjectImage(String projectImage) {
        this.projectImage = projectImage;
    }

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

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Boolean getEditFlag() {
        return editFlag;
    }

    public void setEditFlag(Boolean editFlag) {
        this.editFlag = editFlag;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getTop() {
        return top;
    }

    public void setTop(Boolean top) {
        this.top = top;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
