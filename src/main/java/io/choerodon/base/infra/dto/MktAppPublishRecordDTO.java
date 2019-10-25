package io.choerodon.base.infra.dto;

import java.util.Date;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author PENGYUHUA
 * @date 2019/8/20
 */
@Table(name = "mkt_app_publish_record")
public class MktAppPublishRecordDTO extends BaseDTO {
    @Id
    @ApiModelProperty(value = "主键ID/非必填")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "市场应用编码（UUID）")
    private String mktAppCode;

    @ApiModelProperty(value = "市场应用版本")
    private String mktAppVersion;

    @ApiModelProperty(value = "发布用户编号")
    private Long publishUserId;

    @ApiModelProperty(value = "处理状态")
    private String publishStatus;

    @ApiModelProperty(value = "失败原因记录(仅发布失败时有记录)")
    private String publishErrorCode;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMktAppCode() {
        return mktAppCode;
    }

    public void setMktAppCode(String mktAppCode) {
        this.mktAppCode = mktAppCode;
    }

    public String getMktAppVersion() {
        return mktAppVersion;
    }

    public void setMktAppVersion(String mktAppVersion) {
        this.mktAppVersion = mktAppVersion;
    }

    public Long getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Long publishUserId) {
        this.publishUserId = publishUserId;
    }

    public String getPublishStatus() {
        return publishStatus;
    }

    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }

    public String getPublishErrorCode() {
        return publishErrorCode;
    }

    public void setPublishErrorCode(String publishErrorCode) {
        this.publishErrorCode = publishErrorCode;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }
}
