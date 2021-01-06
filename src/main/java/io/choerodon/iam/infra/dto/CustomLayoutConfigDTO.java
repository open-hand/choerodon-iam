package io.choerodon.iam.infra.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import io.swagger.annotations.ApiModelProperty;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 〈功能简述〉
 * 〈〉
 *
 * @author wanghao
 * @since 2021/1/5 18:06
 */
@Table(name = "fd_custom_layout_config")
public class CustomLayoutConfigDTO extends AuditDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "配置ID/非必填")
    private Long id;
    @ApiModelProperty(value = "配置类型/非必填")
    private String sourceType;
    @ApiModelProperty(value = "配置相关对象id/非必填")
    private Long sourceId;
    @ApiModelProperty(value = "配置数据")
    private String data;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CustomLayoutConfigDTO{" +
                "id=" + id +
                ", sourceType='" + sourceType + '\'' +
                ", sourceId=" + sourceId +
                ", data='" + data + '\'' +
                '}';
    }
}
