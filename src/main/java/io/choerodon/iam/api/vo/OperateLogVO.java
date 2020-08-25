package io.choerodon.iam.api.vo;

import io.swagger.annotations.ApiModelProperty;
import org.hzero.starter.keyencrypt.core.Encrypt;

import java.util.Date;

/**
 * User: Mr.Wang
 * Date: 2020/2/25
 */
public class OperateLogVO {
    @ApiModelProperty(value = "主键ID")
    @Encrypt
    private Long id;

    @ApiModelProperty(value = "操作者ID")
    @Encrypt
    private Long operatorId;

    @ApiModelProperty(value = "操作的类型")
    private String type;

    @ApiModelProperty(value = "执行方法")
    private String method;

    @ApiModelProperty(value = "操作内容")
    private String content;

    @ApiModelProperty(value = "源id")
    @Encrypt
    private Long sourceId;

    @ApiModelProperty(value = "源类型")
    private String sourceType;

    @ApiModelProperty(value = "执行成功与否")
    private Boolean isSuccess;

    @ApiModelProperty
    private Date creationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
