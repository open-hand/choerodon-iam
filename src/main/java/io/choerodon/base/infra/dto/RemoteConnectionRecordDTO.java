package io.choerodon.base.infra.dto;

import io.choerodon.mybatis.entity.BaseDTO;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Eugen
 */
@Table(name = "fd_remote_connection_record")
public class RemoteConnectionRecordDTO extends BaseDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(value = "远程连接Token主键")
    private Long remoteTokenId;

    @ApiModelProperty(value = "连接Ip")
    private String sourceIp;

    @ApiModelProperty(value = "操作类型")
    private String operation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRemoteTokenId() {
        return remoteTokenId;
    }

    public void setRemoteTokenId(Long remoteTokenId) {
        this.remoteTokenId = remoteTokenId;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
