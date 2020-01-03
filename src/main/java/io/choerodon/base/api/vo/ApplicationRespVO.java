package io.choerodon.base.api.vo;

import java.util.Date;

/**
 * @author zongw.lee@gmail.com
 * @date 2019/9/12
 */
public class ApplicationRespVO {
    private Long id;

    private Long projectId;

    private String name;

    private String code;

    private String type;

    private String description;

    private String sourceCode;

    private String feedbackToken;

    private Long objectVersionNumber;

    private String projectName;

    private Long createdBy;

    private String creatorRealName;

    private Date creationDate;

    private Boolean amendable;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getFeedbackToken() {
        return feedbackToken;
    }

    public void setFeedbackToken(String feedbackToken) {
        this.feedbackToken = feedbackToken;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatorRealName() {
        return creatorRealName;
    }

    public void setCreatorRealName(String creatorRealName) {
        this.creatorRealName = creatorRealName;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getAmendable() {
        return amendable;
    }

    public void setAmendable(Boolean amendable) {
        this.amendable = amendable;
    }
}
