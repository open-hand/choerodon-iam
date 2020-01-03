package io.choerodon.base.api.vo;

import io.choerodon.base.infra.dto.ApplicationDTO;
import io.choerodon.base.infra.dto.ProjectDTO;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/8/19
 */
public class ProjectAndAppVO {
    // app
    private Long appId;

    private String feedbackToken;

    private String appName;

    private String appCode;

    // project
    private Long projectId;

    private String projectName;

    private String projectCode;

    private String projectCategory;

    // organization
    private Long organizationId;

    public Long getAppId() {
        return appId;
    }

    public void setAppId(Long appId) {
        this.appId = appId;
    }

    public String getFeedbackToken() {
        return feedbackToken;
    }

    public void setFeedbackToken(String feedbackToken) {
        this.feedbackToken = feedbackToken;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectCategory() {
        return projectCategory;
    }

    public void setProjectCategory(String projectCategory) {
        this.projectCategory = projectCategory;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public ProjectAndAppVO(ProjectDTO projectDTO, ApplicationDTO applicationDTO) {
        this.appId = applicationDTO.getId();
        this.appCode = applicationDTO.getCode();
        this.appName = applicationDTO.getName();
        this.feedbackToken = applicationDTO.getFeedbackToken();
        this.projectCode = projectDTO.getCode();
        this.projectId = projectDTO.getId();
        this.projectName = projectDTO.getName();
        this.projectCategory = projectDTO.getCategory();
        this.organizationId = projectDTO.getOrganizationId();
    }

    public ProjectAndAppVO() {
    }
}
