package io.choerodon.iam.infra.dto.payload;

import java.util.List;
import java.util.Set;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.iam.api.vo.ProjectCategoryVO;

/**
 * @author flyleft
 * @since 2018/4/9
 */
public class ProjectEventPayload {

    private Long projectId;
    private String projectCode;
    private String projectName;
    private Long organizationId;
    private String organizationCode;
    private String organizationName;
    private String userName;
    private Long userId;
    private String imageUrl;
    private Long programId;
    private Long applicationId;
    private String agileProjectCode;
    private String oldAgileProjectCode;
    @ApiModelProperty("devops基础组件中使用的编码,harbor、gitlab、sonar、chartmuserm")
    private String devopsComponentCode;

    private Set<String> roleLabels;
    /**
     * 项目类型的集合
     */
    private List<ProjectCategoryVO> projectCategoryVOS;

    private Boolean useTemplate;

    /**
     * 项目之前的类型
     */
    private List<ProjectCategoryVO> beforeProjectCategoryVOS;

    /**
     * 现在项目具有的项目类型
     */
    private List<ProjectCategoryVO> newProjectCategoryVOS;

    public String getDevopsComponentCode() {
        return devopsComponentCode;
    }

    public void setDevopsComponentCode(String devopsComponentCode) {
        this.devopsComponentCode = devopsComponentCode;
    }

    public List<ProjectCategoryVO> getNewProjectCategoryVOS() {
        return newProjectCategoryVOS;
    }

    public void setNewProjectCategoryVOS(List<ProjectCategoryVO> newProjectCategoryVOS) {
        this.newProjectCategoryVOS = newProjectCategoryVOS;
    }

    public List<ProjectCategoryVO> getBeforeProjectCategoryVOS() {
        return beforeProjectCategoryVOS;
    }

    public void setBeforeProjectCategoryVOS(List<ProjectCategoryVO> beforeProjectCategoryVOS) {
        this.beforeProjectCategoryVOS = beforeProjectCategoryVOS;
    }

    public List<ProjectCategoryVO> getProjectCategoryVOS() {
        return projectCategoryVOS;
    }

    public void setProjectCategoryVOS(List<ProjectCategoryVO> projectCategoryVOS) {
        this.projectCategoryVOS = projectCategoryVOS;
    }

    public String getOldAgileProjectCode() {
        return oldAgileProjectCode;
    }

    public void setOldAgileProjectCode(String oldAgileProjectCode) {
        this.oldAgileProjectCode = oldAgileProjectCode;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Set<String> getRoleLabels() {
        return roleLabels;
    }

    public void setRoleLabels(Set<String> roleLabels) {
        this.roleLabels = roleLabels;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getAgileProjectCode() {
        return agileProjectCode;
    }

    public void setAgileProjectCode(String agileProjectCode) {
        this.agileProjectCode = agileProjectCode;
    }

    public Boolean getUseTemplate() {
        return useTemplate;
    }

    public void setUseTemplate(Boolean useTemplate) {
        this.useTemplate = useTemplate;
    }
}
