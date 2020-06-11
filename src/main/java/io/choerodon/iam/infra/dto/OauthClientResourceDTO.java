package io.choerodon.iam.infra.dto;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 客户端关系表
 *
 * @author lihao
 */
@Table(name = "oauth_client_resource")
public class OauthClientResourceDTO extends AuditDomain {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @ApiModelProperty(value = "主键/非必填")
    private Long id;

    @ApiModelProperty(value = "客户端ID")
    private Long clientId;

    @ApiModelProperty(value = "源id")
    private Long sourceId;

    @ApiModelProperty(value = "源类型")
    private String sourceType;

    public Long getId() {
        return id;
    }

    public OauthClientResourceDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public Long getClientId() {
        return clientId;
    }

    public OauthClientResourceDTO setClientId(Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public OauthClientResourceDTO setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public OauthClientResourceDTO setSourceId(Long sourceId) {
        this.sourceId = sourceId;
        return this;
    }
}
